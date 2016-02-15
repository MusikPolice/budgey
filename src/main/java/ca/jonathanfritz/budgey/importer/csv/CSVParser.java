package ca.jonathanfritz.budgey.importer.csv;

import ca.jonathanfritz.budgey.Transaction;
import ca.jonathanfritz.budgey.importer.Parser;

public interface CSVParser extends Parser {

	/**
	 * Attempts to build a {@link Transaction} object out of the array of fields that were read from a CSV file.
	 * @param fields one line of a CSV file, tokenized on the comma character
	 * @return a {@link Transaction}, or null if the transaction could not be parsed
	 */
	Transaction parse(final String[] fields);

}
