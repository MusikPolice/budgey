package ca.jonathanfritz.budgey;

import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;

public class CredentialsTest {

	@Test
	public void defaultPathTest() {
		final String password = "password";
		final Credentials creds = new Credentials(password);

		Assert.assertThat(creds.getPassword(), IsEqual.equalTo(password));
		Assert.assertThat(creds.getPath().getFileName().toString(), IsEqual.equalTo("profile.db"));
		Assert.assertThat(creds.getBackupPath().getFileName().toString(), IsEqual.equalTo("profile.bak"));
		Assert.assertThat(creds.getPath().getParent(), IsEqual.equalTo(creds.getBackupPath().getParent()));
	}

	@Test
	public void customPathTest() {
		final String username = "username";
		final String password = "password";
		final Credentials creds = new Credentials(username, password);

		Assert.assertThat(creds.getPassword(), IsEqual.equalTo(password));
		Assert.assertThat(creds.getPath().getFileName().toString(), IsEqual.equalTo(username + ".db"));
		Assert.assertThat(creds.getBackupPath().getFileName().toString(), IsEqual.equalTo(username + ".bak"));
		Assert.assertThat(creds.getPath().getParent(), IsEqual.equalTo(creds.getBackupPath().getParent()));
	}
}
