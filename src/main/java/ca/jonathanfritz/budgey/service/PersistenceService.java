package ca.jonathanfritz.budgey.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.jonathanfritz.budgey.Account;
import ca.jonathanfritz.budgey.Credentials;
import ca.jonathanfritz.budgey.Profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;

public class PersistenceService implements ManagedService {

	private final Credentials credentials;
	private final EncryptionService encryptionService;
	private final AccountService accountService;
	private final ObjectMapper objectMapper;

	public final static String JSON_FILE_NAME = "profile.json";

	private final static Logger log = LoggerFactory.getLogger(PersistenceService.class);

	@Inject
	public PersistenceService(Credentials credentials, EncryptionService encryptionService, AccountService accountService, ObjectMapper objectMapper) {
		this.credentials = credentials;
		this.encryptionService = encryptionService;
		this.accountService = accountService;
		this.objectMapper = objectMapper;
	}

	@Override
	public void start() throws IOException {
		final File profileFile = getProfileFile(false);
		if (Files.size(profileFile.toPath()) == 0) {
			// this is a new profile file, so there's nothing to deserialize
			return;
		}

		Profile profile = null;
		try (final FileInputStream in = new FileInputStream(profileFile)) {
			final byte[] encrypted = IOUtils.toByteArray(in);
			final byte[] zipped = encryptionService.decrypt(encrypted, credentials.getPassword());
			final byte[] data = unzip(zipped);
			profile = objectMapper.readValue(data, Profile.class);
		}

		// TODO: load profile into an in-memory database
		accountService.initialize();
		for (final Account account : profile.getAccounts()) {
			accountService.insertAccount(account);
		}
	}

	public boolean save() {
		try {
			// TODO: only create backup if the profile has actually changed. This may require deserializing contents of
			// profile.db and comparing them to the profile we're trying to save, or tracking updates in a log table
			log.debug("Saving profile");
			final File profileFile = getProfileFile(true);

			// TODO: pull all data out of the db and put it into profile - need a service layer here that populates
			// transactions in accounts
			final Profile profile = new Profile();
			for (final Account account : accountService.getAccounts()) {
				profile.addAccount(account);
			}

			try (FileOutputStream out = new FileOutputStream(profileFile)) {
				final byte[] data = objectMapper.writeValueAsBytes(profile);
				final byte[] zipped = zip(data);
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

	protected byte[] zip(byte[] uncompressed) throws IOException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (final ZipOutputStream zip = new ZipOutputStream(out)) {
			final ZipEntry entry = new ZipEntry(JSON_FILE_NAME);
			zip.putNextEntry(entry);
			zip.write(uncompressed);
			zip.closeEntry();
		}
		return out.toByteArray();
	}

	protected byte[] unzip(byte[] compressed) throws IOException {
		final ByteArrayInputStream in = new ByteArrayInputStream(compressed);
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		try (final ZipInputStream zip = new ZipInputStream(in)) {
			zip.getNextEntry();

			int count = 0;
			final byte[] buff = new byte[1024];
			while ((count = zip.read(buff, 0, buff.length)) != -1) {
				out.write(buff, 0, count);
			}
		}
		return out.toByteArray();
	}

	@Override
	public void stop() {
		save();
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
