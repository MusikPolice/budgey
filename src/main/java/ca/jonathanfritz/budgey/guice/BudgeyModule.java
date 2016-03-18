package ca.jonathanfritz.budgey.guice;

import java.util.Set;

import org.reflections.Reflections;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.logging.SLF4JLog;
import org.skife.jdbi.v2.logging.SLF4JLog.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.jonathanfritz.budgey.dao.AccountDAO;
import ca.jonathanfritz.budgey.dao.TransactionDAO;
import ca.jonathanfritz.budgey.service.ManagedService;
import ca.jonathanfritz.budgey.service.PersistenceService;
import ca.jonathanfritz.budgey.util.jackson.ObjectMapperFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;

/**
 * This module registers all things that are necessary for both the GUI and CLI versions of Budgey
 */
public class BudgeyModule extends AbstractModule {

	private final Logger log = LoggerFactory.getLogger(BudgeyModule.class);
	private final Logger dbLog = LoggerFactory.getLogger(PersistenceService.class);

	@Override
	protected void configure() {
		// auto-discover and register managed services with classpath scanning
		// this allows a list of all managed services to be injected as Set<ManagedService>
		// each ManagedService instance is a singleton, so we know that the same one is used everywhere
		log.debug("Registering managed services...");
		final Reflections reflections = new Reflections("ca.jonathanfritz.budgey");
		final Set<Class<? extends ManagedService>> services = reflections.getSubTypesOf(ManagedService.class);
		final Multibinder<ManagedService> managedServiceBinder = Multibinder.newSetBinder(binder(), ManagedService.class);
		for (final Class<? extends ManagedService> clazz : services) {
			log.debug(clazz.getCanonicalName());
			managedServiceBinder.addBinding().to(clazz).in(Singleton.class);
		}

		// globally configured ObjectMapper
		bind(ObjectMapper.class).toInstance(ObjectMapperFactory.getObjectMapper());
	}

	@Provides
	@Singleton
	public DBI providesDBI() {
		final DBI dbi = new DBI("jdbc:h2:mem:budgey");
		dbi.setSQLLog(new SLF4JLog(dbLog, Level.INFO));
		return dbi;
	}

	@Provides
	@Singleton
	public AccountDAO providesAccountDao(DBI dbi) {
		final AccountDAO dao = dbi.onDemand(AccountDAO.class);
		return dao;
	}

	@Provides
	@Singleton
	public TransactionDAO providesTransactionDAO(DBI dbi) {
		final TransactionDAO dao = dbi.onDemand(TransactionDAO.class);
		return dao;
	}
}