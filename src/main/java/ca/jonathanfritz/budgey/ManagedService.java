package ca.jonathanfritz.budgey;

/**
 * Objects that implement this interface will be stopped when the application stops
 */
public interface ManagedService {

	void start();

	void stop();
}
