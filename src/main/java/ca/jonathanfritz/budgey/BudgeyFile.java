package ca.jonathanfritz.budgey;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;

/**
 * Utility methods for finding and making use of the budgey file and backup file that are persisted to disk on save
 */
public class BudgeyFile {
	public final static String DATABASE_FILE_EXTENSION = ".db";
	public final static String DATABASE_BACKUP_FILE_EXTENSION = ".bak";

	public final static Path BUDGEY_FOLDER = Paths.get(Joiner.on(File.separator)
	                                                         .join(System.getProperty("user.home"), ".budgey"));

	private final static Logger log = LoggerFactory.getLogger(BudgeyFile.class);

	/**
	 * Gets the default absolute path to the persisted budgey file. If the parent directory does not exist, it will be
	 * created.
	 * @param username the name of the user that the file belongs to
	 * @return the default absolute path to the persisted budgey file
	 * @throws IOException if the directory could not be created
	 */
	public static Path getDefaultFilePath(final String username) throws IOException {
		return Paths.get(Joiner.on(File.separator)
		                       .join(getOrCreateDefaultDirectory(), username + DATABASE_FILE_EXTENSION));
	}

	/**
	 * Gets the default absolute path to the backup of the persisted budgey file. If the parent directory does not
	 * exist, it will be created.
	 * @param username the name of the user that the file belongs to.
	 * @return the default absolute path to the backup of the persisted budgey file
	 * @throws IOException if the directory could not be created
	 */
	public static Path getDefaultBackupFilePath(final String username) throws IOException {
		return Paths.get(Joiner.on(File.separator)
		                       .join(getOrCreateDefaultDirectory(), username + DATABASE_BACKUP_FILE_EXTENSION));
	}

	/**
	 * Gets the default absolute path to the directory that contains the persisted budgey file. If the directory does
	 * not exist, it will be created.
	 * @return the default absolute path to the directory that contains the persisted budgey file
	 * @throws IOException if the directory could not be created
	 */
	public static Path getOrCreateDefaultDirectory() throws IOException {
		try {
			return Files.createDirectories(BUDGEY_FOLDER);
		} catch (final IOException e) {
			log.error("Failed to create default directory", e);
			throw e;
		}
	}

	/**
	 * @return the default profile name. On most systems, this is the username of the currently logged in user.
	 */
	public static String getDefaultProfileName() {
		final String username = System.getProperty("user.name");
		return username == null ? "profile" : username;
	}
}
