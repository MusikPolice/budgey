package ca.jonathanfritz.budgey.ui.cli;

/**
 * Thrown by a {@link Command} that wants the application to exit cleanly. The simple case for using this exception is
 * if the Command implements an exit option that the user can select. Other commands might throw it if something goes so
 * horribly wrong that the application shouldn't continue to run.
 */
@SuppressWarnings("serial")
public class ExitApplicationException extends Exception {

	public ExitApplicationException(String message) {
		super(message);
	}
}
