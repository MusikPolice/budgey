package ca.jonathanfritz.budgey.guice;

import java.util.Set;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.jonathanfritz.budgey.ManagedService;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;

/**
 * This module registers all things that are necessary for both the GUI and CLI versions of Budgey
 */
public class BudgeyModule extends AbstractModule {

	private final Logger log = LoggerFactory.getLogger(BudgeyModule.class);

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
	}
}