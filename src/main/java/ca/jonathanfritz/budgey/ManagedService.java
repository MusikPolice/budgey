package ca.jonathanfritz.budgey;

import com.google.inject.Singleton;

/**
 * Objects that implement this interface will be stopped when the application stops
 */
@Singleton
public interface ManagedService {

	void start();

	void stop();
}
