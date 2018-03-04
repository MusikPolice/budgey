package ca.jonathanfritz.budgey.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.IOUtils;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import ca.jonathanfritz.budgey.Account;
import ca.jonathanfritz.budgey.Credentials;
import ca.jonathanfritz.budgey.Profile;
import ca.jonathanfritz.budgey.dao.AutoCommittingHandle;

public class PersistenceService implements ManagedService {

	private final Credentials credentials;
	private final EncryptionService encryptionService;
	private final CompressionService compressionService;
	private final AccountService accountService;
	private final ObjectMapper objectMapper;
	private final DBI dbi;

	private static final Logger log = LoggerFactory.getLogger(PersistenceService.class);

	@Inject
	public PersistenceService(Credentials credentials, EncryptionService encryptionService, CompressionService compressionService, AccountService accountService, ObjectMapper objectMapper, DBI dbi) {
		this.credentials = credentials;
		this.encryptionService = encryptionService;
		this.compressionService = compressionService;
		this.accountService = accountService; 
		this.objectMapper = objectMapper;
		this.dbi = dbi;
	}

	@Override
	public void start() throws IOException {
		Profile profile = null;
		final File profileFile = getOrCreateProfileFile(false);
		if (Files.size(profileFile.toPath()) != 0) {
			// read the profile from disk
			try (final FileInputStream in = new FileInputStream(profileFile)) {
				final byte[] encrypted = IOUtils.toByteArray(in);
				final byte[] zipped = encryptionService.decrypt(encrypted, credentials.getPassword());
				final byte[] data = compressionService.unzip(zipped);
				profile = objectMapper.readValue(data, Profile.class);
			} catch (final EncryptionOperationNotPossibleException ex) {
				log.error("Failed to decrypt profile", ex);
			}
		}

		accountService.initialize();
		if (profile == null) {
			return;
		}

		try (AutoCommittingHandle handle = new AutoCommittingHandle(dbi)) {
			try {
				// load profile into an in-memory database
				for (final Account account : profile.getAccounts()) {
					accountService.insertAccountWithTransactions(handle, account);
				}
			} catch (final Exception ex) {
				handle.rollback();
				log.error("Failed to initialize in-memory database", ex);
			}
		}
	}

	public boolean save() {
		try {
			// TODO: only create backup if the profile has actually changed. This may require deserializing contents of
			// profile.db and comparing them to the profile we're trying to save, or tracking updates in a log table
			log.debug("Saving profile");
			final File profileFile = getOrCreateProfileFile(true);

			// pull all data out of the db and put it into profile
			final Profile profile = new Profile();
			try (AutoCommittingHandle handle = new AutoCommittingHandle(dbi)) {
				try {
					for (final Account account : accountService.getAccountsWithTransactions(handle)) {
						profile.addAccount(account);
					}
				} catch (final Exception ex) {
					handle.rollback();
					log.error("Failed to dump in-memory database", ex);
				}
			}

			// write the profile out to disk
			try (FileOutputStream out = new FileOutputStream(profileFile)) {
				final byte[] data = objectMapper.writeValueAsBytes(profile);
				final byte[] zipped = compressionService.zip(data);
				final byte[] encrypted = encryptionService.encrypt(zipped, credentials.getPassword());
				out.write(encrypted);
			}

			log.debug("Success");
		} catch (final IOException e) {
			log.error("Failed to save profile file", e);
			return false;
		}
		return true;
	}

	@Override
	public void stop() {
		save();
		// TODO: drop and re-create database tables so that a new instance of persistence service could be started
	}

	/**
	 * Deletes the profile and backup file. Only intended for use in tests
	 */
	protected void deleteProfile() throws IOException {
		Files.delete(credentials.getPath());
		Files.delete(credentials.getBackupPath());
	}

	/**
	 * Attempts to create a profile.db file inside of a .budgey folder in the users' home directory.<br/>
	 * @param backup if true, and profile.db already exists, a backup of profile.db will be made as profile.bak
	 * @return a {@link File} handler that points to the profile.db file
	 * @throws IOException if file creation or copy fails
	 */
	private File getOrCreateProfileFile(boolean backup) throws IOException {
		final Path path = credentials.getPath();
		if (!path.toFile().exists()) {
			log.debug("Attempting to create profile file " + path.toString());
			try {
				Files.createDirectories(path.getParent());
				Files.createFile(path);
			} catch (final IOException e) {
				throw new IOException("Failed to create profile file " + path.toString(), e);
			}
			log.debug("Success");
		} else if (backup) {
			log.debug("Creating a backup of existing profile file");
			final Path backupPath = credentials.getBackupPath();
			try {
				Files.copy(path, backupPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
			} catch (final IOException e) {
				throw new IOException("Failed to copy existing profile file " + path.toString() + " to "
				        + backupPath.toString(), e);
			}
		}
		return new File(path.toUri());
	}
}
