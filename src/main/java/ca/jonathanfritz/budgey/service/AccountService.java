package ca.jonathanfritz.budgey.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import ca.jonathanfritz.budgey.Account;
import ca.jonathanfritz.budgey.AccountType;
import ca.jonathanfritz.budgey.Transaction;
import ca.jonathanfritz.budgey.dao.AccountDAO;
import ca.jonathanfritz.budgey.dao.AutoCommittingHandle;
import ca.jonathanfritz.budgey.dao.TransactionDAO;

public class AccountService {

	private final DBI dbi;
	private final AccountDAO accountDao;
	private final TransactionDAO transactionDao;

	private static final Logger log = LoggerFactory.getLogger(AccountService.class);

	@Inject
	public AccountService(DBI dbi, AccountDAO accountDao, TransactionDAO transactionDao) {
		this.dbi = dbi;
		this.accountDao = accountDao;
		this.transactionDao = transactionDao;
	}

	public void initialize() {
		accountDao.createTable();
		transactionDao.createTable();
	}

	/**
	 * Inserts both the specified account and the transactions that it contains
	 * @param account the account to insert
	 * @throws RuntimeException if the operation fails
	 */
	public void insertAccountWithTransactions(AutoCommittingHandle handle, Account account) {
		accountDao.insertAccount(handle, account);
		for (final Transaction transaction : account.getTransactions()) {
			transactionDao.insertTransaction(handle, transaction);
		}
	}

	/**
	 * Inserts the specified transactions, adding them to existing accounts, or creating new accounts as necessary,
	 * based on the account number associated with each transaction.
	 * TODO: this needs to be refactored and tested
	 * @param transactions the list of transactions to insert
	 */
	public boolean insertTransactionsAndCreateAccounts(List<Transaction> transactions) {
		try (final AutoCommittingHandle handle = new AutoCommittingHandle(dbi)) {
			try {
				// fetch all existing accounts, initializing a map entry for each that contains an empty set of
				// transactions as its value
				final Map<Account, List<Transaction>> existingAccounts = new HashMap<>();
				for (final Account existingAccount : getAccountsWithTransactions(handle)) {
					existingAccounts.put(existingAccount, new ArrayList<>());
				}

				// a map of new accounts to add
				final Map<Account, List<Transaction>> newAccounts = new HashMap<>();

				// sort new transactions into the map of accounts
				for (final Transaction transaction : transactions) {
					final Optional<Account> existingAccount = existingAccounts.keySet()
					                                                          .stream()
					                                                          .filter(a -> a.getAccountNumber()
					                                                                        .equals(transaction.getAccountNumber()))
					                                                          .findFirst();
					final Optional<Account> newAccount = newAccounts.keySet()
					                                                .stream()
					                                                .filter(a -> a.getAccountNumber()
					                                                              .equals(transaction.getAccountNumber()))
					                                                .findFirst();

					if (!existingAccount.isPresent() && newAccount.isPresent()) {
						newAccounts.get(newAccount.get()).add(transaction);
					} else if (existingAccount.isPresent() && !newAccount.isPresent()) {
						existingAccounts.get(existingAccount.get()).add(transaction);
					} else if (!existingAccount.isPresent() && !newAccount.isPresent()) {
						final List<Transaction> newTransactions = Arrays.asList(transaction);
						// TODO: can we assume account type and balance/currency? - instead, prompt user for details?
						final Account account = Account.newBuilder(transaction.getAccountNumber())
						                               .setType(AccountType.CHECKING)
						                               .setBalance(Money.zero(CurrencyUnit.CAD))
						                               .addTransactions(newTransactions)
						                               .build();
						newAccounts.put(account, newTransactions);
					} else if (existingAccount.isPresent() && newAccount.isPresent()) {
						throw new IllegalStateException("Account can't exist in new and existing maps");
					}
				}

				for (final Entry<Account, List<Transaction>> entry : newAccounts.entrySet()) {
					// sum the transactions to compute an updated balance for the account
					final List<Money> transactionAmounts = entry.getValue()
					                                            .stream()
					                                            .map(Transaction::getAmount)
					                                            .collect(Collectors.toList());
					final Money balance = Money.zero(entry.getKey().getBalance().getCurrencyUnit())
					                           .plus(transactionAmounts);

					// insert a copy of the account with the correct balance and list of transactions
					final Account updatedAccount = Account.newBuilder(entry.getKey())
					                                      .setBalance(balance)
					                                      .addTransactions(entry.getValue())
					                                      .build();

					insertAccountWithTransactions(handle, updatedAccount);
				}

				for (final Entry<Account, List<Transaction>> entry : existingAccounts.entrySet()) {
					// sum the transactions to compute an updated balance for the account
					final List<Money> transactionAmounts = entry.getValue()
					                                            .stream()
					                                            .map(Transaction::getAmount)
					                                            .collect(Collectors.toList());
					final Money balance = entry.getKey()
					                           .getBalance()
					                           .plus(transactionAmounts);

					// update the account with the correct balance and set of transactions
					// this adds the new transactions to the set of transactions already in the account
					final Account updatedAccount = Account.newBuilder(entry.getKey())
					                                      .setBalance(balance)
					                                      .addTransactions(entry.getValue())
					                                      .build();

					accountDao.updateAccount(handle, updatedAccount);
					transactionDao.insertTransactions(handle, entry.getValue());
				}
			} catch (final Exception ex) {
				log.error("Failed to insert tranactions", ex);
				handle.rollback();
				return false;
			}
		}

		return true;
	}

	/**
	 * Gets all accounts and their transactions
	 * @param handle the database handle to fetch accounts on
	 * @return a list of {@link Account} objects, each containing a list of the {@link Transaction} objects that belong
	 *         to that account.
	 */
	public List<Account> getAccountsWithTransactions(AutoCommittingHandle handle) {
		final List<Account> accounts = new ArrayList<>();
		for (final Account account : accountDao.getAccounts(handle)) {
			final List<Transaction> transactions = transactionDao.getTransactions(handle, account.getAccountNumber());
			accounts.add(Account.newBuilder(account.getAccountNumber())
			                    .setType(account.getType())
			                    .setBalance(account.getBalance())
			                    .addTransactions(transactions)
			                    .build());
		}
		return accounts;
	}
}
