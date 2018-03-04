package ca.jonathanfritz.budgey;

import java.io.IOException;

import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;

public class CredentialsTest {

	@Test
	public void defaultPathTest() throws IOException {
		final String password = "password";
		final Credentials creds = new Credentials(password);

		Assert.assertThat(creds.getPassword(), IsEqual.equalTo(password));
		Assert.assertThat(creds.getPath()
		                       .getFileName()
		                       .toString(), IsEqual.equalTo(BudgeyFile.getDefaultProfileName()
		                               + BudgeyFile.DATABASE_FILE_EXTENSION));
		Assert.assertThat(creds.getBackupPath()
		                       .getFileName()
		                       .toString(), IsEqual.equalTo(BudgeyFile.getDefaultProfileName()
		                               + BudgeyFile.DATABASE_BACKUP_FILE_EXTENSION));
		Assert.assertThat(creds.getPath().getParent(), IsEqual.equalTo(creds.getBackupPath().getParent()));
	}

	@Test
	public void customPathTest() throws IOException {
		final String username = "username";
		final String password = "password";
		final Credentials creds = new Credentials(username, password);

		Assert.assertThat(creds.getPassword(), IsEqual.equalTo(password));
		Assert.assertThat(creds.getPath()
		                       .getFileName()
		                       .toString(), IsEqual.equalTo(username + BudgeyFile.DATABASE_FILE_EXTENSION));
		Assert.assertThat(creds.getBackupPath()
		                       .getFileName()
		                       .toString(), IsEqual.equalTo(username + BudgeyFile.DATABASE_BACKUP_FILE_EXTENSION));
		Assert.assertThat(creds.getPath().getParent(), IsEqual.equalTo(creds.getBackupPath().getParent()));
	}
}
