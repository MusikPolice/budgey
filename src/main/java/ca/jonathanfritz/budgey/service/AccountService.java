package ca.jonathanfritz.budgey.service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ca.jonathanfritz.budgey.Account;
import ca.jonathanfritz.budgey.Transaction;
import ca.jonathanfritz.budgey.dao.AccountDAO;
import ca.jonathanfritz.budgey.dao.TransactionDAO;

import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class AccountService {

	private final AccountDAO accountDao;
	private final TransactionDAO transactionDao;

	@Inject
	public AccountService(AccountDAO accountDao, TransactionDAO transactionDao) {
		this.accountDao = accountDao;
		this.transactionDao = transactionDao;
	}

	public void initialize() {
		accountDao.createTable();
		transactionDao.createTable();
	}

	public void insertAccount(Account account) {
		accountDao.insertAccount(
				account.getAccountNumber(),
				account.getType().getType(),
				account.getBalance().getAmount(),
				account.getBalance().getCurrencyUnit().getCode());

		for (final Transaction transaction : account.getTransactions()) {
			transactionDao.insertTransaction(
					transaction.getAccountNumber(),
					transaction.getDateUtc().getMillis(),
					transaction.getOrder(),
					transaction.getDescription(),
					transaction.getAmount().getAmount());	// TODO: need to store currency code
		}
	}

	public Set<Account> getAccounts() {
		final Set<Account> accounts = new HashSet<>();
		for (final Account account : accountDao.getAccounts()) {
			final Set<Transaction> transactions = iteratorToSet(transactionDao.getTransactions(account.getAccountNumber()), Transaction.class);
			accounts.add(new Account(account.getAccountNumber(), account.getType(), account.getBalance(), transactions));
		}
		return accounts;
	}

	private <T> Set<T> iteratorToSet(Iterator<T> iterator, Class<T> type) {
		return Sets.newHashSet(Iterators.filter(iterator, type));
	}

}
