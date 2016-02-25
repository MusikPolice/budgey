package ca.jonathanfritz.budgey.service;


/**
 * Objects that implement this interface will be stopped when the application stops
 */
public interface ManagedService {

	void start() throws Exception;

	void stop();
}
