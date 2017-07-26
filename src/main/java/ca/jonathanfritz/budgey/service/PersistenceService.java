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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

import ca.jonathanfritz.budgey.Account;
import ca.jonathanfritz.budgey.Credentials;
import ca.jonathanfritz.budgey.Profile;

public class PersistenceService implements ManagedService {

	private final Credentials credentials;
	private final EncryptionService encryptionService;
	private final CompressionService compressionService;
	private final AccountService accountService;
	private final ObjectMapper objectMapper;

	private final static Logger log = LoggerFactory.getLogger(PersistenceService.class);

	@Inject
	public PersistenceService(Credentials credentials, EncryptionService encryptionService, CompressionService compressionService, AccountService accountService, ObjectMapper objectMapper) {
		this.credentials = credentials;
		this.encryptionService = encryptionService;
		this.compressionService = compressionService;
		this.accountService = accountService;
		this.objectMapper = objectMapper;
	}

	@Override
	public void start() throws IOException {
		Profile profile = null;
		final File profileFile = getProfileFile(false);
		if (Files.size(profileFile.toPath()) != 0) {
			// read the profile from disk
			try (final FileInputStream in = new FileInputStream(profileFile)) {
				final byte[] encrypted = IOUtils.toByteArray(in);
				final byte[] zipped = encryptionService.decrypt(encrypted, credentials.getPassword());
				final byte[] data = compressionService.unzip(zipped);
				profile = objectMapper.readValue(data, Profile.class);
			} catch (final EncryptionOperationNotPossibleException ex) {
				log.error("Failed to decrypt profile");
			}
		}

		// load profile into an in-memory database
		accountService.initialize();
		if (profile != null) {
			for (final Account account : profile.getAccounts()) {
				accountService.insertAccount(account);
			}
		}
	}

	public boolean save() {
		try {
			// TODO: only create backup if the profile has actually changed. This may require deserializing contents of
			// profile.db and comparing them to the profile we're trying to save, or tracking updates in a log table
			log.debug("Saving profile");
			final File profileFile = getProfileFile(true);

			// pull all data out of the db and put it into profile
			final Profile profile = new Profile();
			for (final Account account : accountService.getAccounts()) {
				profile.addAccount(account);
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
	private File getProfileFile(boolean backup) throws IOException {
		final Path path = credentials.getPath();
		if (!Files.exists(path)) {
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
