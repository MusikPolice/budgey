package ca.jonathanfritz.budgey.ui.cli.commands;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;

import ca.jonathanfritz.budgey.Transaction;
import ca.jonathanfritz.budgey.importer.Parser;
import ca.jonathanfritz.budgey.importer.ofx.OfxParser;
import ca.jonathanfritz.budgey.service.AccountService;
import ca.jonathanfritz.budgey.ui.cli.Command;
import ca.jonathanfritz.budgey.ui.cli.ParameterSet;
import ca.jonathanfritz.budgey.ui.cli.ParameterSet.Parameter;

/**
 * Tries to import a file full of transactions.<br />
 * TODO: use classpath scanning to find all the parsers, auto-discover the most appropriate one
 */
public class ImportOfxCommand implements Command {

	private final AccountService accountService;

	private static final String FILE_KEY = "file";

	@Inject
	public ImportOfxCommand(AccountService accountService) {
		this.accountService = accountService;
	}

	@Override
	public String getName() {
		return "import-ofx";
	}

	@Override
	public String getDescription() {
		return "imports transactions from an ofx file";
	}

	@Override
	public int getOrder() {
		return 1;
	}

	@Override
	public ParameterSet getParameterSet() {
		final List<Parameter> parameters = new ArrayList<>();
		parameters.add(new Parameter(FILE_KEY, "the path to the file to import", String.class));
		return new ParameterSet(parameters);
	}

	@Override
	public void execute(ParameterSet parameters) {

		final String file = parameters.getParameterValue(FILE_KEY, String.class);
		final String absolutePath = file.replaceFirst("^~", System.getProperty("user.home"));

		final Parser parser = new OfxParser();

		try {
			final List<Transaction> transactions = parser.parse(Paths.get(absolutePath));
			accountService.insertTransactionsAndCreateAccounts(transactions);
		} catch (final IOException e) {
			throw new RuntimeException("Failed to import file", e);
		}
	}
}
