package ca.jonathanfritz.budgey.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.jonathanfritz.budgey.Transaction;
import ca.jonathanfritz.budgey.importer.CSVImporter;
import ca.jonathanfritz.budgey.importer.CSVParser;
import ca.jonathanfritz.budgey.importer.RoyalBankCSVParser;
import ca.jonathanfritz.budgey.importer.ScotiabankCSVParser;
import ca.jonathanfritz.budgey.ui.Command;
import ca.jonathanfritz.budgey.ui.ParameterSet;
import ca.jonathanfritz.budgey.ui.ParameterSet.Parameter;

public class ImportCommand implements Command {

	private static final String PARSER_KEY = "parser";
	private static final String FILE_KEY = "file";

	private static final String ROYAL_BANK_PARSER = "royal";
	private static final String SCOTIA_BANK_PARSER = "scotia";

	@Override
	public String getName() {
		return "import";
	}

	@Override
	public ParameterSet getParameterSet() {
		final List<Parameter> parameters = new ArrayList<>();
		parameters.add(new Parameter(PARSER_KEY, String.class));
		parameters.add(new Parameter(FILE_KEY, String.class));
		return new ParameterSet(parameters);
	}

	@Override
	public void execute(ParameterSet parameters) {
		final String parserOption = parameters.getParameterValue(PARSER_KEY, String.class);
		final String file = parameters.getParameterValue(FILE_KEY, String.class);

		CSVParser parser;

		switch (parserOption) {
			default:
			case ROYAL_BANK_PARSER:
				parser = new RoyalBankCSVParser();
				break;
			case SCOTIA_BANK_PARSER:
				parser = new ScotiabankCSVParser();
				break;
		}

		final CSVImporter<CSVParser> csvImporter = new CSVImporter<>(parser);
		List<Transaction> transactions;
		try {
			transactions = csvImporter.importFile(file);
		} catch (final IOException e) {
			throw new RuntimeException("Failed to import file", e);
		}

		for (final Transaction t : transactions) {
			System.out.println(t.toString());
		}
	}
}
