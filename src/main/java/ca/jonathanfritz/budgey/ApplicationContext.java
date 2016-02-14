package ca.jonathanfritz.budgey;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * An application context for Budgey.<br/>
 * On startup, it discovers Guice modules and initializes an injector. It also discovers implementations of
 * {@link ManagedService}, each of which will be initialized have its {@link ManagedService#start()} method called.<br/>
 * When the application is cleanly exited, it is important to call the {@link #stop()} method, which will make sure that
 * all instances of {@link ManagedService} are stopped cleanly.
 */
public class ApplicationContext implements Closeable {

	private final Injector injector;
	private final Set<ManagedService> managedServices = new HashSet<>();

	private final static Logger log = LoggerFactory.getLogger(ApplicationContext.class);

	public ApplicationContext() {
		// auto-discover and register modules with classpath scanning
		// TODO: we have a module specifically for CLI stuff. Maybe it shouldn't be registered if the GUI is running
		log.debug("Registering Guice modules...");
		final Reflections reflections = new Reflections("ca.jonathanfritz.budgey");
		final Set<Class<? extends AbstractModule>> moduleTypes = reflections.getSubTypesOf(AbstractModule.class);
		final Set<Module> modules = new HashSet<>();
		for (final Class<? extends AbstractModule> clazz : moduleTypes) {
			try {
				// betting on modules having a noarg constructor...
				log.debug(clazz.getCanonicalName());
				modules.add(clazz.getConstructor().newInstance());
			} catch (final Exception e) {
				log.error("Failed to initialize Module " + clazz.getCanonicalName()
				        + ". Does it have a zero argument constructor?", e);
			}
		}
		injector = Guice.createInjector(modules);

		// initialize managed services
		start();
	}

	public Injector getInjector() {
		return injector;
	}

	private void start() {
		log.debug("Starting managed services...");
		managedServices.addAll(injector.getInstance(Key.get(new TypeLiteral<Set<ManagedService>>() {
		})));
		for (final ManagedService s : managedServices) {
			log.debug("\t" + s.getClass().getCanonicalName());
			s.start();
		}
	}

	public void stop() {
		log.debug("Stopping managed services...");
		for (final ManagedService s : managedServices) {
			s.stop();
		}
	}

	@Override
	public void close() throws IOException {
		stop();
	}
}
