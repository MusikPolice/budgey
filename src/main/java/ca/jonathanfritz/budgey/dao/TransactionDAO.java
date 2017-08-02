package ca.jonathanfritz.budgey.dao;

import java.util.List;
import java.util.Set;

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
	 * @param accountNumber the unique id of the account that the transaction affected
	 * @param timeUtc the time at which the transaction took place, expressed in UTC
	 * @param order for transactions that occur on the same date and don't have the required precision, this field
	 *            infers an ordering from the order they were received from the financial institution
	 * @param description a textual description of the transaction
	 * @param amount the amount of the transaction
	 * @return true if the operation succeeds, false otherwise
	 */
	public boolean insertTransaction(Transaction transaction) {
		try (AutoCommittingHandle handle = new AutoCommittingHandle(dbi)) {
			try {
				return insertTransaction(handle, transaction);
			} catch (final Exception ex) {
				handle.rollback();
				throw new RuntimeException("Failed to insert transaction", ex);
			}
		}
	}

	public boolean insertTransaction(AutoCommittingHandle handle, Transaction transaction) {
		final String sql = "INSERT INTO `transaction` (`account_number`, `time_millis`, `order`, `description`, `amount`, `currency`) "
		        + "VALUES (:accountNumber, :timeMillis, :order, :description, :amount, :currency)";

		return handle.createStatement(sql)
		             .bind("accountNumber", transaction.getAccountNumber())
		             .bind("timeMillis", transaction.getDateUtc()
		                                            .getMillis())
		             .bind("order", transaction.getOrder())
		             .bind("description", transaction.getDescription())
		             .bind("amount", transaction.getAmount()
		                                        .getAmount())
		             .bind("currency", transaction.getAmount()
		                                          .getCurrencyUnit()
		                                          .getCurrencyCode())
		             .execute() == 1;
	}

	public boolean insertTransactions(Set<Transaction> transactions) {
		try (AutoCommittingHandle handle = new AutoCommittingHandle(dbi)) {
			try {
				return insertTransactions(handle, transactions);
			} catch (final Exception ex) {
				handle.rollback();
				throw new RuntimeException("Failed to insert transactions", ex);
			}
		}
	}

	public boolean insertTransactions(AutoCommittingHandle handle, Set<Transaction> transactions) {
		for (final Transaction transaction : transactions) {
			if (!insertTransaction(handle, transaction)) {
				throw new RuntimeException("Failed to insert transactions");
			}
		}
		return true;
	}

	/**
	 * Fetches all transactions for the specified account
	 * @param accountNumber the unique id of the account to get transactions for
	 * @return a set of {@link Transaction}, ordered by the time at which the transaction occurred, followed by the
	 *         order field
	 */
	public List<Transaction> getTransactions(String accountNumber) {
		try (AutoCommittingHandle handle = new AutoCommittingHandle(dbi)) {
			try {
				final String sql = "SELECT * FROM `transaction` "
				        + "WHERE account_number = :accountNumber "
				        + "ORDER BY time_millis DESC, `order` DESC";

				return handle.createQuery(sql)
				             .bind("accountNumber", accountNumber)
				             .map(new TransactionResultSetMapper())
				             .list();
			} catch (final Exception ex) {
				handle.rollback();
				throw new RuntimeException("Failed to get transactions for account " + accountNumber, ex);
			}
		}
	}
}
