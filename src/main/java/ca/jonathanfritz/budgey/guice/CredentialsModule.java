package ca.jonathanfritz.budgey.guice;

import ca.jonathanfritz.budgey.Credentials;

import com.google.common.base.Strings;
import com.google.inject.AbstractModule;

public class CredentialsModule extends AbstractModule {

	private final String username;
	private final String password;

	public CredentialsModule(String password) {
		username = null;
		this.password = password;
	}

	public CredentialsModule(String username, String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	protected void configure() {
		if (!Strings.isNullOrEmpty(username)) {
			bind(Credentials.class).toInstance(new Credentials(username, password));
		} else {
			bind(Credentials.class).toInstance(new Credentials(password));
		}
	}
}
