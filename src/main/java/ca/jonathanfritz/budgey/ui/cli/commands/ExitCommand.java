package ca.jonathanfritz.budgey.ui.cli.commands;

import java.util.ArrayList;

import ca.jonathanfritz.budgey.ui.cli.Command;
import ca.jonathanfritz.budgey.ui.cli.ExitApplicationException;
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
	public int getOrder() {
		return Integer.MAX_VALUE;
	}

	@Override
	public ParameterSet getParameterSet() {
		return new ParameterSet(new ArrayList<ParameterSet.Parameter>());
	}

	@Override
	public void execute(ParameterSet parameters) throws ExitApplicationException {
		throw new ExitApplicationException("Goodbye!");
	}
}
