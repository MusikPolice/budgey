package ca.jonathanfritz.budgey.guice;

import ca.jonathanfritz.budgey.Credentials;

import com.google.inject.AbstractModule;

public class CredentialsModule extends AbstractModule {

	private final String username;
	private final String password;

	public CredentialsModule(String username, String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	protected void configure() {
		bind(Credentials.class).toInstance(new Credentials(username, password));
	}
}
