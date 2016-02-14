package ca.jonathanfritz.budgey.ui.cli;

/**
 * To add an option to the CLI interface, create an implementation of this class in the
 * ca.jonathanfritz.budgey.ui.cli.commands package. It will be auto-discovered via classpath scanning and added to the
 * main menu.
 */
public interface Command {

	/**
	 * @return the name of the command. One word only.
	 */
	String getName();

	/**
	 * @return a description of the command. Maximum 80 characters.
	 */
	String getDescription();

	/**
	 * @return the desired sort order for commands in the CLI menu
	 */
	int getOrder();

	/**
	 * @return the parameters that this command needs in order to do its work
	 */
	ParameterSet getParameterSet();

	/**
	 * This method will be called when the command is executed by the user. The logic that the command performs should
	 * live here.
	 * @param parameters the parameters returned by {@link #getParameterSet()}, now with values.
	 */
	void execute(ParameterSet parameters) throws ExitApplicationException;
}
