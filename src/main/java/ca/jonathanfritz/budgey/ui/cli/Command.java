package ca.jonathanfritz.budgey.ui.cli;

public interface Command {

	String getName();

	String getDescription();

	ParameterSet getParameterSet();

	void execute(ParameterSet parameters);
}
