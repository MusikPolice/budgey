package ca.jonathanfritz.budgey.dao;

import java.util.List;

import org.skife.jdbi.v2.DBI;

import com.google.inject.Inject;

import ca.jonathanfritz.budgey.Transaction;

public class TransactionDAO {

	private final DBI dbi;

	@Inject
	public TransactionDAO(DBI dbi) {
		this.dbi = dbi;
	}

	// TODO: account_number should be foreign-keyed on accounts?
	public boolean createTable() {
		try (AutoCommittingHandle handle = new AutoCommittingHandle(dbi)) {
			try {
				final String sql = "CREATE TABLE IF NOT EXISTS `transaction` (`account_number` VARCHAR(255), `time_millis` BIGINT, `order` SMALLINT, `description` VARCHAR(255), `amount` DECIMAL, currency VARCHAR(3))";

				return handle.createStatement(sql)
				             .execute() == 1;

			} catch (final Exception ex) {
				handle.rollback();
				throw new RuntimeException("Failed to create transaction table", ex);
			}
		}
	}

	/**
	 * Inserts a transaction into the database
	 * @param handle the database handle to insert the account transaction on
	 * @param transaction the account transaction to insert
	 * @throws RuntimeException if the operation fails
	 */
	public void insertTransaction(AutoCommittingHandle handle, Transaction transaction) {
		final String sql = "INSERT INTO `transaction` (`account_number`, `time_millis`, `order`, `description`, `amount`, `currency`) "
		        + "VALUES (:accountNumber, :timeMillis, :order, :description, :amount, :currency)";

		final int numRows = handle.createStatement(sql)
		                          .bind("accountNumber", transaction.getAccountNumber())
		                          .bind("timeMillis", transaction.getDateUtc().getMillis())
		                          .bind("order", transaction.getOrder())
		                          .bind("description", transaction.getDescription())
		                          .bind("amount", transaction.getAmount().getAmount())
		                          .bind("currency", transaction.getAmount().getCurrencyUnit().getCurrencyCode())
		                          .execute();

		if (numRows != 1) {
			throw new RuntimeException("Failed to insert transaction");
		}
	}

	/**
	 * Inserts the specified transactions into the database
	 * @param handle the database handle to insert the account transaction on
	 * @param transactions the list of account transactions to insert
	 * @throws RuntimeException if the operation fails
	 */
	public void insertTransactions(AutoCommittingHandle handle, List<Transaction> transactions) {
		for (final Transaction transaction : transactions) {
			insertTransaction(handle, transaction);
		}
	}

	/**
	 * Fetches all transactions for the specified account
	 * @param handle the database handle to fetch the transactions on
	 * @param accountNumber the unique id of the account to get transactions for
	 * @return a set of {@link Transaction}, ordered by the time at which the transaction occurred, followed by the
	 *         order field
	 */
	public List<Transaction> getTransactions(AutoCommittingHandle handle, String accountNumber) {
		final String sql = "SELECT * FROM `transaction` "
		        + "WHERE account_number = :accountNumber "
		        + "ORDER BY time_millis DESC, `order` DESC";

		return handle.createQuery(sql)
		             .bind("accountNumber", accountNumber)
		             .map(new TransactionResultSetMapper())
		             .list();
	}
}
