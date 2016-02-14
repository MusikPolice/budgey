package ca.jonathanfritz.budgey;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.common.base.Joiner;

/**
 * An immutable container for a set of credentials that map to a saved {@link Profile}
 */
public class Credentials {

	private final Path path;
	private final String password;

	/**
	 * Creates a new instance of Credentials.<br/>
	 * This overload supports multiple user profiles
	 * @param username the name of the user. The resulting profile filename will include the username, allowing two or
	 *            more users to maintain separate profiles on the same machine.
	 * @param password the encryption password that unlocks the profile file
	 */
	public Credentials(String username, String password) {
		path = Paths.get(Joiner.on(File.separator).join(System.getProperty("user.home"), ".budgey", username + ".db"));
		this.password = password;
	}

	/**
	 * Creates a new instance of Credentials.<br/>
	 * This overload sets a default profile name.
	 * @param password the encryption password that unlocks the profile file
	 */
	public Credentials(String password) {
		this("profile", password);
	}

	/**
	 * @return the path to the user's saved {@link Profile} file
	 */
	public Path getPath() {
		return path;
	}

	/**
	 * @return the path to the user's backup {@link Profile} file
	 */
	public Path getBackupPath() {
		final String filename = path.getFileName().toString().substring(0, path.getFileName().toString().lastIndexOf(".db"));
		return path.resolveSibling(filename + ".bak");
	}

	/**
	 * @return the encryption password for the user's saved {@link Profile} file
	 */
	public String getPassword() {
		return password;
	}
}
