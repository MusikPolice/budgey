package ca.jonathanfritz.budgey.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.jonathanfritz.budgey.commands.ImportCommand;
import ca.jonathanfritz.budgey.ui.ParameterSet.Parameter;

public class BudgeyCLI {

	private final List<Command> commands = new ArrayList<>();

	private final static Logger log = LoggerFactory.getLogger(BudgeyCLI.class);

	public BudgeyCLI() {
		final ImportCommand importCommand = new ImportCommand();

		commands.add(importCommand);
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

			// run the command
			command.execute(parameterSet);
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

	public static void main(String[] args) {
		final BudgeyCLI budgey = new BudgeyCLI();
		budgey.run();
	}
}
