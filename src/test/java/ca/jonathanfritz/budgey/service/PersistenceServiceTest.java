package ca.jonathanfritz.budgey.service;

import java.io.IOException;
import java.util.UUID;

import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import ca.jonathanfritz.budgey.Credentials;
import ca.jonathanfritz.budgey.dao.AutoCommittingHandle;
import ca.jonathanfritz.budgey.guice.BudgeyModule;

public class PersistenceServiceTest {

	private final String username = UUID.randomUUID().toString();
	private final String password = UUID.randomUUID().toString();
	private PersistenceService persistenceService;
	private AccountService accountService;
	private DBI dbi;

	private static final Logger log = LoggerFactory.getLogger(PersistenceServiceTest.class);

	@Before
	public void setup() {
		final Injector injector = Guice.createInjector(new BudgeyModule(), new AbstractModule() {

			@Override
			protected void configure() {
				bind(Credentials.class).toInstance(new Credentials(username, password));
			}
		});
		persistenceService = injector.getInstance(PersistenceService.class);
		accountService = injector.getInstance(AccountService.class);
		dbi = injector.getInstance(DBI.class);
	}

	@Test
	@Ignore("In-memory database is a singleton and isn't being cleared between persistence service activations, leading to primary key violations")
	public void createCloseOpenEmptyProfileTest() {
		try (AutoCommittingHandle handle = new AutoCommittingHandle(dbi)) {
			log.info("Starting 1");
			persistenceService.start();
			log.info("Started 1");
			final int numStartingAccounts = accountService.getAccountsWithTransactions(handle).size();
			log.info("Stopping 1");
			persistenceService.stop();
			log.info("Starting 2");
			persistenceService.start();
			log.info("Started 1");
			Assert.assertThat(accountService.getAccountsWithTransactions(handle)
			                                .size(), IsEqual.equalTo(numStartingAccounts));
			log.info("Stopping 1");
			persistenceService.stop();

			// cleanup
			log.info("Deleting");
			persistenceService.deleteProfile();
		} catch (final IOException e) {
			log.error("Test failed", e);
			Assert.fail();
		}
	}
}
