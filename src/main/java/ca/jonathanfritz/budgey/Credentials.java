package ca.jonathanfritz.budgey;

import java.io.IOException;
import java.nio.file.Path;

/**
 * An immutable container for a set of credentials that map to a saved {@link Profile}
 */
public class Credentials {
	private final String username;
	private final String password;

	/**
	 * Creates a new instance of Credentials.<br/>
	 * This overload supports multiple user profiles
	 * @param username the name of the user. The resulting profile filename will include the username, allowing two or
	 *            more users to maintain separate profiles on the same machine.
	 * @param password the encryption password that unlocks the profile file
	 */
	public Credentials(final String username, final String password) {
		this.username = username;
		this.password = password;
	}

	/**
	 * Creates a new instance of Credentials.<br/>
	 * This overload sets a default profile name.
	 * @param password the encryption password that unlocks the profile file
	 */
	public Credentials(final String password) {
		this(BudgeyFile.getDefaultProfileName(), password);
	}

	/**
	 * @return the username associated with the user's saved {@link Profile} file
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return the encryption password for the user's saved {@link Profile} file
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Gets the path to the user's saved {@link Profile} file. The parent directory of the file will be created if it
	 * does not already exist.
	 * @return the path to the user's saved {@link Profile} file
	 * @throws IOException if the parent directory of the file could not be created
	 */
	public Path getPath() throws IOException {
		return BudgeyFile.getDefaultFilePath(username);
	}

	/**
	 * Gets the path to the user's backup {@link Profile} file. The parent directory of the file will be created if it
	 * does not already exist.
	 * @return the path to the user's backup {@link Profile} file
	 * @throws IOException if the parent directory of the file could not be created
	 */
	public Path getBackupPath() throws IOException {
		return BudgeyFile.getDefaultBackupFilePath(username);
	}
}
