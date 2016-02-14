package ca.jonathanfritz.budgey.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.jonathanfritz.budgey.Credentials;
import ca.jonathanfritz.budgey.Profile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.google.inject.Inject;

public class PersistenceService implements ManagedService {

	private final Credentials credentials;
	private final ObjectMapper objectMapper;
	private Profile profile;

	private final static Logger log = LoggerFactory.getLogger(PersistenceService.class);

	@Inject
	public PersistenceService(Credentials credentials, ObjectMapper objectMapper) throws IOException {
		this.credentials = credentials;
		this.objectMapper = objectMapper;
	}

	@Override
	public void start() throws IOException {
		// TODO: compression+encryption
		final File profileFile = getProfileFile(false);
		if (Files.size(profileFile.toPath()) == 0) {
			// this is a new profile file, so there's nothing to deserialize
			profile = new Profile();
			return;
		}

		profile = objectMapper.readValue(profileFile, Profile.class);
	}

	/**
	 * Returns the profile for the current user.<br/>
	 * The returned profile will be automatically saved when the application is closed, or whenever the {@link #save()}
	 * method is called.
	 */
	public Profile getProfile() {
		return profile;
	}

	public boolean save() {
		try {
			// TODO: only create backup if the profile has actually changed. This may require deserializing contents of
			// profile.db and comparing them to the profile we're trying to save.
			log.debug("Saving profile");
			final File profileFile = getProfileFile(true);
			objectMapper.writeValue(profileFile, profile);
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
	 * Attempts to create a profile.db file inside of a .budgey folder in the users' home directory.<br/>
	 * @param backup if true, and profile.db already exists, a backup of profile.db will be made as profile.bak
	 * @return a {@link File} handler that points to the profile.db file
	 * @throws IOException if file creation or copy fails
	 */
	private File getProfileFile(boolean backup) throws IOException {
		final Path path = Paths.get(Joiner.on(File.separator).join(System.getProperty("user.home"), ".budgey", "profile.db"));
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
			final Path backupFile = path.resolveSibling("profile.bak");
			try {
				Files.copy(path, backupFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
			} catch (final IOException e) {
				throw new IOException("Failed to copy existing profile file " + path.toString() + " to "
				        + backupFile.toString(), e);
			}
		}
		return new File(path.toUri());
	}
}
