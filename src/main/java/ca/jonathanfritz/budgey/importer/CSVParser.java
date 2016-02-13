package ca.jonathanfritz.budgey.importer;

import ca.jonathanfritz.budgey.Transaction;

public interface CSVParser {

	/**
	 * Attempts to build a {@link Transaction} object out of the array of fields that were read from a CSV file.
	 * @param fields one line of a CSV file, tokenized on the comma character
	 * @return a {@link Transaction}, or null if the transaction could not be parsed
	 */
	Transaction parse(String[] fields);
}
