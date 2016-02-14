package ca.jonathanfritz.budgey;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.jonathanfritz.budgey.guice.BudgeyModule;
import ca.jonathanfritz.budgey.guice.CredentialsModule;
import ca.jonathanfritz.budgey.services.ManagedService;

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

	/**
	 * Creates an application context for the default username
	 * @param password the password to unlock the user's {@link Profile}
	 * @param additionalModules any additional Guice {@link Module}s that need to be loaded into the injector
	 * @throws Exception thrown if context does not start successfully
	 */
	public ApplicationContext(String password, Module... additionalModules) throws Exception {
		this(null, password, additionalModules);
	}

	/**
	 * Creates an application context for the specified user
	 * @param username the user to create the context for. This property will be used to load the appropriate
	 *            {@link Profile}, as opposed to the default one.
	 * @param password the password to unlock the user's {@link Profile}
	 * @param additionalModules any additional Guice {@link Module}s that need to be loaded into the injector
	 * @throws Exception thrown if context does not start successfully
	 */
	public ApplicationContext(String username, String password, Module... additionalModules) throws Exception {
		// we always bind BudgeyModule in addition to the modules requested by the caller
		log.debug("Registering Guice modules...");
		final List<Module> modules = new ArrayList<>();
		modules.addAll(Arrays.asList(new BudgeyModule(), new CredentialsModule(username, password)));
		modules.addAll(Arrays.asList(additionalModules));
		for (final Module m : modules) {
			log.debug(m.getClass().getCanonicalName());
		}
		injector = Guice.createInjector(modules);

		// initialize managed services
		start();
	}

	public Injector getInjector() {
		return injector;
	}

	private void start() throws Exception {
		log.debug("Starting managed services...");
		managedServices.addAll(injector.getInstance(Key.get(new TypeLiteral<Set<ManagedService>>() {
		})));
		for (final ManagedService s : managedServices) {
			log.debug(s.getClass().getCanonicalName());
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
