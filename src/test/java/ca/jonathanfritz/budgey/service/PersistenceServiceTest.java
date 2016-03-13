package ca.jonathanfritz.budgey.service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.UUID;

import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Assert;
import org.junit.Test;

import ca.jonathanfritz.budgey.Account;
import ca.jonathanfritz.budgey.AccountType;
import ca.jonathanfritz.budgey.Credentials;
import ca.jonathanfritz.budgey.Profile;
import ca.jonathanfritz.budgey.Transaction;
import ca.jonathanfritz.budgey.util.jackson.ObjectMapperFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public class PersistenceServiceTest {

	private final EncryptionService encryptionService = new EncryptionService();
	private final ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();

	@Test
	public void newProfileTest() throws IOException {
		// create a profile for a brand new user
		final String username = UUID.randomUUID().toString();
		final String password = "password";
		final Credentials credentials = new Credentials(username, password);

		try {
			final PersistenceService persistenceService = new PersistenceService(credentials, encryptionService, objectMapper);
			persistenceService.start();

			// the profile should be empty
			final Profile profile = persistenceService.getProfile();
			Assert.assertTrue(profile.getLastUpdatedUtc().isBeforeNow());
			Assert.assertTrue(profile.getAccounts().isEmpty());

			// a profile file ought to have been created, but it will be empty
			Assert.assertTrue(Files.exists(credentials.getPath()));
			Assert.assertThat(Files.size(credentials.getPath()), IsEqual.equalTo(0L));

		} finally {
			// cleanup
			Files.deleteIfExists(credentials.getPath());
			Files.deleteIfExists(credentials.getBackupPath());
		}
	}

	@Test
	public void loadExistingProfileTest() throws IOException {
		// create a profile for a brand new user
		final String username = UUID.randomUUID().toString();
		final String password = "password";
		final Credentials credentials = new Credentials(username, password);

		try {
			PersistenceService persistenceService = new PersistenceService(credentials, encryptionService, objectMapper);
			persistenceService.start();

			// add an account to the profile
			final Account account = new Account("1234", AccountType.CHECKING, Money.of(CurrencyUnit.CAD, 10534.23), new HashSet<Transaction>());
			persistenceService.getProfile().getAccounts().add(account);

			// save the account to disk - it should be an encrypted blob of a file at this point
			persistenceService.save();
			Assert.assertTrue(Files.exists(credentials.getPath()));
			Assert.assertTrue(Files.exists(credentials.getBackupPath()));
			Assert.assertThat(Files.size(credentials.getPath()), IsNot.not(IsEqual.equalTo(0L)));

			// re-load the profile and verify
			persistenceService = new PersistenceService(credentials, encryptionService, objectMapper);
			persistenceService.start();

			final Profile profile = persistenceService.getProfile();
			Assert.assertTrue(profile.getLastUpdatedUtc().isBeforeNow());
			Assert.assertThat(profile.getAccounts().size(), IsEqual.equalTo(1));
			Assert.assertThat(profile.getAccounts().toArray(new Account[] {})[0], IsEqual.equalTo(account));
			Assert.assertTrue(profile.getAccounts().toArray(new Account[] {})[0].getTransactions().isEmpty());
			Assert.assertThat(profile.getAccounts().toArray(new Account[] {})[0].getType(), IsEqual.equalTo(account.getType()));
			Assert.assertThat(profile.getAccounts().toArray(new Account[] {})[0].getBalance(), IsEqual.equalTo(account.getBalance()));
		} finally {
			// cleanup
			Files.deleteIfExists(credentials.getPath());
			Files.deleteIfExists(credentials.getBackupPath());
		}
	}

	@Test
	public void zipTest() throws IOException {
		final PersistenceService persistenceService = new PersistenceService(null, encryptionService, objectMapper);

		final byte[] data = UUID.randomUUID().toString().getBytes();
		final byte[] zipped = persistenceService.zip(data);
		final byte[] unzipped = persistenceService.unzip(zipped);

		Assert.assertArrayEquals(data, unzipped);
	}
}
