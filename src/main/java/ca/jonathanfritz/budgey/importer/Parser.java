package ca.jonathanfritz.budgey.importer;

import ca.jonathanfritz.budgey.Transaction;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface Parser {

	List<Transaction> parse(final Path path) throws IOException;

}
