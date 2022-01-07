package ca.jonathanfritz.budgey;

import java.io.IOException;
import java.nio.file.Path;

/**
 * An immutable container for a set of credentials that map to a saved {@link Profile}
 */
public class Credentials {
	private String username = BudgeyFile.getDefaultProfileName();
	private final String password;
	private Path path;

	private Credentials(final Builder builder) {
		if (builder.username != null) {
			username = builder.username;
		}
		password = builder.password;
		path = builder.path;
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
		if (path == null) {
			path = BudgeyFile.getDefaultFilePath(username);
		}
		return path;
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

	public static Builder newBuilder(final String password) {
		return new Builder(password);
	}

	public static class Builder {
		private String username;
		private final String password;
		private Path path;

		private Builder(final String password) {
			this.password = password;
		}

		public Builder setUsername(final String username) {
			this.username = username;
			return this;
		}

		public Builder setPath(final Path path) {
			this.path = path;
			return this;
		}

		public Credentials build() {
			return new Credentials(this);
		}
	}
}
