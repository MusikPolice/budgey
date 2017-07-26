package ca.jonathanfritz.budgey.ui.cli.commands;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import com.google.inject.Inject;

import ca.jonathanfritz.budgey.Account;
import ca.jonathanfritz.budgey.AccountType;
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

		List<Transaction> transactions;
		try {
			transactions = parser.parse(Paths.get(absolutePath));
		} catch (final IOException e) {
			throw new RuntimeException("Failed to import file", e);
		}

		// make sure accounts exist
		// TODO: this should all be done in one big transaction
		final Set<Account> existingAccounts = accountService.getAccounts();
		final Set<Account> accountsToAdd = transactions.stream()
		        .map(Transaction::getAccountNumber)
		        .distinct()
		        .filter(accountNumber -> existingAccounts.stream()
		                .noneMatch(existingAccount -> existingAccount.getAccountNumber()
		                        .equals(accountNumber)))
		        // TODO: we don't know details about this account yet
		        .map(accountNumber -> new Account(accountNumber, AccountType.CHECKING, Money
		                .zero(CurrencyUnit.CAD), transactions))
		        .collect(Collectors.toSet());
		for (final Account account : accountsToAdd) {
			accountService.insertAccount(account);
		}

		for (final Transaction t : transactions) {
			// TODO: need a transaction service - maybe it should handle creating the accounts?
			System.out.println(t.toString());
		}
	}
}
