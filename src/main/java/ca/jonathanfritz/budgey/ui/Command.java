package ca.jonathanfritz.budgey.ui;


public interface Command {

	String getName();

	ParameterSet getParameterSet();

	void execute(ParameterSet parameters);
}
