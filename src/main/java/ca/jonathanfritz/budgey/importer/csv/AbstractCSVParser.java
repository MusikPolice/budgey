package ca.jonathanfritz.budgey.importer.csv;

import ca.jonathanfritz.budgey.Transaction;
import ca.jonathanfritz.budgey.importer.AbstractParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractCSVParser extends AbstractParser implements CSVParser {

	@Override
	public List<Transaction> parse(final Path path) throws IOException {

		final List<String> lines = Files.readAllLines(path);
		return lines.stream()
				.map(line -> FieldSanitizer.sanitizeFields(line.split(",")))
				.map(this::parse)
				.collect(Collectors.toList());

	}


}
