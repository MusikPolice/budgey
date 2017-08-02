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
	 * @param accountNumber the unique account identifier
	 * @param type the type of account
	 * @param balance current balance of the account
	 * @param currency the ISO-4217 currency code
	 * @return true if the operation succeeds, false otherwise
	 */
	public boolean insertAccount(Account account) {
		try (AutoCommittingHandle handle = new AutoCommittingHandle(dbi)) {
			try {
				return insertAccount(handle, account);
			} catch (final Exception ex) {
				handle.rollback();
				throw new RuntimeException("Failed to insert account", ex);
			}
		}
	}

	public boolean insertAccount(AutoCommittingHandle handle, Account account) {
		final String sql = "INSERT INTO account (account_number, type, balance, currency) "
		        + "VALUES (:accountNumber, :type, :balance, :currency)";

		return handle.createStatement(sql)
		             .bind("accountNumber", account.getAccountNumber())
		             .bind("type", account.getType()
		                                  .toString())
		             .bind("balance", account.getBalance()
		                                     .getAmount())
		             .bind("currency", account.getBalance()
		                                      .getCurrencyUnit()
		                                      .getCurrencyCode())
		             .execute() == 1;
	}

	/**
	 * Gets all accounts in the database. Accounts returned by this function do not include transactions.
	 */
	public Set<Account> getAccounts() {
		try (AutoCommittingHandle handle = new AutoCommittingHandle(dbi)) {
			try {
				return getAccounts(handle);
			} catch (final Exception ex) {
				handle.rollback();
				throw new RuntimeException("Failed to get accounts", ex);
			}
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
