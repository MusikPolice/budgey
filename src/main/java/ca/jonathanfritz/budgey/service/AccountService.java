package ca.jonathanfritz.budgey.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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

	public void insertAccount(Account account) {
		// TODO: database transaction!
		accountDao.insertAccount(account);

		for (final Transaction transaction : account.getTransactions()) {
			transactionDao.insertTransaction(transaction);
		}
	}

	public void insertTransactions(List<Transaction> transactions) {
		try (AutoCommittingHandle handle = new AutoCommittingHandle(dbi)) {
			try {
				// make sure accounts exist
				final List<Account> existingAccounts = getAccounts();
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
					insertAccount(account);
				}

				for (final Transaction t : transactions) {
					// TODO: need a transaction service - maybe it should handle creating the accounts?
					System.out.println(t.toString());
				}
			} catch (final Exception ex) {
				log.error("Failed to insert tranactions", ex);
				handle.rollback();
			}
		}
	}

	public List<Account> getAccounts() {
		try (AutoCommittingHandle handle = new AutoCommittingHandle(dbi)) {
			try {
				final List<Account> accounts = new ArrayList<>();
				for (final Account account : accountDao.getAccounts(handle)) {

					// TODO: this needs to be in the database transaction
					final List<Transaction> transactions = transactionDao.getTransactions(account.getAccountNumber());
					accounts.add(new Account(account.getAccountNumber(), account.getType(), account
					                                                                               .getBalance(), transactions));
				}
				return accounts;
			} catch (final Exception ex) {
				handle.rollback();
				throw new RuntimeException("Failed to insert tranactions", ex);
			}
		}
	}
}
