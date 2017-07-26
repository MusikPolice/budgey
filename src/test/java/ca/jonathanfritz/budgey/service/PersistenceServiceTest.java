package ca.jonathanfritz.budgey.service;

import java.io.IOException;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.jonathanfritz.budgey.Credentials;
import ca.jonathanfritz.budgey.guice.BudgeyModule;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class PersistenceServiceTest {

	private final String username = UUID.randomUUID().toString();
	private final String password = UUID.randomUUID().toString();
	private PersistenceService persistenceService;
	private AccountService accountService;

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
	}

	@Test
	public void createCloseOpenEmptyProfileTest() {
		try {
			log.info("Starting 1");
			persistenceService.start();
			log.info("Started 1");
			Assert.assertTrue(accountService.getAccounts().isEmpty());
			log.info("Stopping 1");
			persistenceService.stop();
			log.info("Starting 2");
			persistenceService.start();
			log.info("Started 1");
			Assert.assertTrue(accountService.getAccounts().isEmpty());
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
