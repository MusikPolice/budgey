package ca.jonathanfritz.budgey.ui.cli.commands;

import ca.jonathanfritz.budgey.Transaction;
import ca.jonathanfritz.budgey.importer.Parser;
import ca.jonathanfritz.budgey.importer.csv.RoyalBankCSVParser;
import ca.jonathanfritz.budgey.importer.csv.ScotiabankCSVParser;
import ca.jonathanfritz.budgey.ui.cli.Command;
import ca.jonathanfritz.budgey.ui.cli.ParameterSet;
import ca.jonathanfritz.budgey.ui.cli.ParameterSet.Parameter;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Tries to import a file full of transactions.<br />
 * TODO: use classpath scanning to find all the parsers, auto-discover the most appropriate one
 */
public class ImportCsvCommand implements Command {

	private static final String PARSER_KEY = "parser";
	private static final String FILE_KEY = "file";
	private static final String ACCOUNT_KEY = "account";

	private static final String ROYAL_BANK_PARSER = "royal";
	private static final String SCOTIA_BANK_PARSER = "scotia";

	@Override
	public String getName() {
		return "import-csv";
	}

	@Override
	public String getDescription() {
		return "imports transactions from a csv file";
	}

	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public ParameterSet getParameterSet() {
		final List<Parameter> parameters = new ArrayList<>();
		parameters.add(new Parameter(PARSER_KEY, "the name of the parser to use [" + ROYAL_BANK_PARSER + ", "
		        + SCOTIA_BANK_PARSER + "]", String.class));
		parameters.add(new Parameter(FILE_KEY, "the path to the file to import", String.class));
		parameters.add(new Parameter(ACCOUNT_KEY, "Account number. [1234]", String.class));
		return new ParameterSet(parameters);
	}

	@Override
	public void execute(ParameterSet parameters) {

		final String parserOption = parameters.getParameterValue(PARSER_KEY, String.class);
		final String file = parameters.getParameterValue(FILE_KEY, String.class);
		final String account = parameters.getParameterValue(ACCOUNT_KEY, String.class);

		final String absolutePath = file.replaceFirst("^~", System.getProperty("user.home"));

		Parser parser;

		// TODO: register and inject a set of parsers, then choose the correct one by name
		// see examples of finding ManagedServces and Commands in BudgeyModule and BudgeyCLIModule
		switch (parserOption) {
			default:
			case ROYAL_BANK_PARSER:
				parser = new RoyalBankCSVParser();
				break;
			case SCOTIA_BANK_PARSER:
				parser = new ScotiabankCSVParser(account);
				break;
		}

		List<Transaction> transactions;
		try {
			transactions = parser.parse(Paths.get(absolutePath));
		} catch (final IOException e) {
			throw new RuntimeException("Failed to import file", e);
		}

		for (final Transaction t : transactions) {
			System.out.println(t.toString());
		}
	}
}
