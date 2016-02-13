package ca.jonathanfritz.budgey.ui;

public interface Command {

	String getName();

	String getDescription();

	ParameterSet getParameterSet();

	void execute(ParameterSet parameters);
}
