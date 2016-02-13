package ca.jonathanfritz.budgey.ui.cli.commands;

import java.util.ArrayList;

import ca.jonathanfritz.budgey.ui.cli.Command;
import ca.jonathanfritz.budgey.ui.cli.ParameterSet;

/**
 * Exits the CLI
 */
public class ExitCommand implements Command {

	@Override
	public String getName() {
		return "exit";
	}

	@Override
	public String getDescription() {
		return "closes the application";
	}

	@Override
	public ParameterSet getParameterSet() {
		return new ParameterSet(new ArrayList<ParameterSet.Parameter>());
	}

	@Override
	public void execute(ParameterSet parameters) {
		System.out.println("Goodbye!");
		System.exit(0);
	}

}
