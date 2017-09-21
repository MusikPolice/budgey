package ca.jonathanfritz.budgey.dao;

import java.util.Set;
import java.util.stream.Collectors;

import org.skife.jdbi.v2.DBI;

import com.google.inject.Inject;

import ca.jonathanfritz.budgey.Account;

public class AccountDAO {

	private final DBI dbi;

	@Inject
	public AccountDAO(DBI dbi) {
		this.dbi = dbi;
	}

	// TODO: account type should be foreign-keyed to a table of types?
	public boolean createTable() {
		try (AutoCommittingHandle handle = new AutoCommittingHandle(dbi)) {
			try {
				final String sql = "CREATE TABLE IF NOT EXISTS account (account_number VARCHAR(255) PRIMARY KEY, type VARCHAR(20), balance DECIMAL, currency VARCHAR(3))";

				return handle.createStatement(sql)
				             .execute() == 1;

			} catch (final Exception ex) {
				handle.rollback();
				throw new RuntimeException("Failed to create account table", ex);
			}
		}
	}

	/**
	 * Adds an account to the database
	 * @param handle the database transaction to insert the account on
	 * @param account the new account. Transactions will not be inserted by this method.
	 * @throws RuntimeException if the insert fails
	 */
	public void insertAccount(AutoCommittingHandle handle, Account account) {
		final String sql = "INSERT INTO account (account_number, type, balance, currency) "
		        + "VALUES (:accountNumber, :type, :balance, :currency)";

		final int insertedRows = handle.createStatement(sql)
		                               .bind("accountNumber", account.getAccountNumber())
		                               .bind("type", account.getType().toString())
		                               .bind("balance", account.getBalance().getAmount())
		                               .bind("currency", account.getBalance().getCurrencyUnit().getCurrencyCode())
		                               .execute();

		if (insertedRows != 1) {
			throw new RuntimeException("Failed to insert account");
		}
	}

	/**
	 * Updates an existing account
	 * @param handle the database transaction to insert the account on
	 * @param account the updated account. The account number cannot be changed. Transactions will not be modified by
	 *            this function.
	 * @throws RuntimeException if the insert fails
	 */
	public void updateAccount(AutoCommittingHandle handle, Account account) {
		final String sql = "UPDATE account SET type = :type, balance = :balance, currency = :currency "
		        + "WHERE account_number = :accountNumber";

		final int updatedRows = handle.createStatement(sql)
		                              .bind("accountNumber", account.getAccountNumber())
		                              .bind("type", account.getType().toString())
		                              .bind("balance", account.getBalance().getAmount())
		                              .bind("currency", account.getBalance().getCurrencyUnit().getCurrencyCode())
		                              .execute();

		if (updatedRows != 1) {
			throw new RuntimeException("Failed to update account");
		}
	}

	/**
	 * Gets all accounts in the database. Accounts returned by this function do not include transactions.
	 * @param handle the transaction to execute the query on
	 */
	public Set<Account> getAccounts(AutoCommittingHandle handle) {
		final String sql = "SELECT * FROM account";
		return handle.createQuery(sql)
		             .map(new AccountResultSetMapper())
		             .list()
		             .stream()
		             .collect(Collectors.toSet());
	}
}
