package ca.jonathanfritz.budgey.ui.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.jonathanfritz.budgey.ApplicationContext;
import ca.jonathanfritz.budgey.guice.BudgeyCLIModule;
import ca.jonathanfritz.budgey.ui.cli.ParameterSet.Parameter;

import com.google.inject.Inject;

public class BudgeyCLI {

	private final List<Command> commands = new ArrayList<>();

	private final static Logger log = LoggerFactory.getLogger(BudgeyCLI.class);

	@Inject
	public BudgeyCLI(Set<Command> commands) {
		// sort the commands so that they appear in the desired order
		final Map<Integer, Command> unsortedCommands = new TreeMap<>();
		for (final Command command : commands) {
			unsortedCommands.put(command.getOrder(), command);
		}
		this.commands.addAll(unsortedCommands.values());
		log.debug("Initialization complete");
	}

	public void run() {
		System.out.println("Budgey - v1");
		System.out.println("A budgeting tool for people with dollars and sense");
		while (true) {
			System.out.println("\nOptions:");
			for (int i = 1; i <= commands.size(); i++) {
				final int index = i - 1;
				System.out.println(i + ") " + commands.get(index).getName() + " - "
				        + commands.get(index).getDescription());
			}
			final int selection = getInteger();
			if (selection == 0 || selection > commands.size()) {
				System.out.println("Invalid input");
				continue;
			}

			// get parameter values from the user
			final Command command = commands.get(selection - 1);
			final ParameterSet parameterSet = command.getParameterSet();
			final List<Parameter> parameters = parameterSet.getParameters();
			for (final Parameter p : parameters) {
				System.out.println(p.getName() + " - " + p.getDescription());
				if (p.getType() == String.class) {
					parameterSet.setParameterValue(p.getName(), getString());
				} else if (p.getType() == Integer.class) {
					parameterSet.setParameterValue(p.getName(), getInteger());
				}
			}

			try {
				// run the command
				command.execute(parameterSet);
			} catch (final ExitApplicationException e) {
				System.out.println(e.getMessage());
				break;
			}
		}
	}

	private int getInteger() {
		try {
			final String str = getString();
			return Integer.parseInt(str);
		} catch (final NumberFormatException e) {
			return 0;
		}
	}

	private String getString() {
		final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			System.out.print("> ");
			try {
				return bufferedReader.readLine();
			} catch (final IOException e) {
				continue;
			}
		}
	}

	public static void main(String[] args) throws IOException {
		log.debug("Initializing Budgey CLI");
		// TODO: get an (optional) username and a (not optional) password from the user
		try (final ApplicationContext budgey = new ApplicationContext("", new BudgeyCLIModule())) {
			// the run method of the CLI will block until the user invokes the exit command
			// at that time, the Budgey application context will be stopped
			final BudgeyCLI cli = budgey.getInjector().getInstance(BudgeyCLI.class);
			cli.run();
		} catch (final Exception e) {
			log.error("Failed to start Application Context", e);
		}
		log.debug("Budgey CLI stopped");
	}
}
