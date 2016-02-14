package ca.jonathanfritz.budgey.guice;

import java.util.Set;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.jonathanfritz.budgey.ui.cli.Command;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

/**
 * This module registers things that are only necessary for the CLI version of budgey
 */
public class BudgeyCLIModule extends AbstractModule {

	private final Logger log = LoggerFactory.getLogger(BudgeyCLIModule.class);

	@Override
	protected void configure() {
		// auto-discover and register cli commands with classpath scanning
		// this allows a list of all commands to be injected as Set<Command>
		log.debug("Registering commands...");
		final Reflections reflections = new Reflections("ca.jonathanfritz.budgey");
		final Set<Class<? extends Command>> commands = reflections.getSubTypesOf(Command.class);
		final Multibinder<Command> commandBinder = Multibinder.newSetBinder(binder(), Command.class);
		for (final Class<? extends Command> clazz : commands) {
			log.debug(clazz.getCanonicalName());
			commandBinder.addBinding().to(clazz);
		}
	}

}
