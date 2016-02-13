package ca.jonathanfritz.budgey;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.money.Money;
import org.joda.time.DateTime;

/**
 * Represents a transaction at a financial institution
 */
public class Transaction {
	private final String accountNumber;
	private final DateTime transactionDate;
	private final int order;
	private final String description;
	private final Money amount;

	private Transaction(Builder builder) {
		accountNumber = builder.accountNumber;
		transactionDate = builder.transactionDate;
		order = builder.order;
		description = builder.description;
		amount = builder.amount;
	}

	/**
	 * @return the unique identifier of the account that was updated by this transaction
	 */
	public String getAccountNumber() {
		return accountNumber;
	}

	/**
	 * @return the date on which this transaction took place. If a time for the transaction is available, it should also
	 *         be expressed in this field.
	 */
	public DateTime getTransactionDate() {
		return transactionDate;
	}

	/**
	 * @return for transactions that occur on the same date and don't have the required precision, this field
	 *         infers an ordering from the order they were received from the financial institution
	 */
	public int getOrder() {
		return order;
	}

	/**
	 * @return A simple description for a transaction
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the amount spent or earned. This amount is signed, so both negative and positive values are supported.
	 */
	public Money getAmount() {
		return amount;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public static Builder newBuilder() {
		return new Builder();
	}

	public static class Builder {
		private String accountNumber;
		private DateTime transactionDate;
		private int order;
		private String description;
		private Money amount;

		/**
		 * @param accountNumber the unique identifier of the account that was updated by this transaction
		 */
		public Builder setAccountNumber(String accountNumber) {
			this.accountNumber = accountNumber;
			return this;
		}

		/**
		 * @param transactionDate the date on which this transaction took place. If a time for the transaction is
		 *            available, it should also be expressed in this field.
		 */
		public Builder setTransactionDate(DateTime transactionDate) {
			this.transactionDate = transactionDate;
			return this;
		}

		/**
		 * @param order for transactions that occur on the same date and don't have the required precision, this field
		 *         infers an ordering from the order they were received from the financial institution
		 */
		public Builder setOrder(int order) {
			this.order = order;
			return this;
		}

		/**
		 * @param description A simple description for a transaction.
		 */
		public Builder setDescription(String description) {
			this.description = description;
			return this;
		}

		/**
		 * @param amount the amount spent or earned. This amount is signed, so both negative and positive values are
		 *            supported.
		 */
		public Builder setAmount(Money amount) {
			this.amount = amount;
			return this;
		}

		/**
		 * @return a new instance of {@link Transaction} with all of the attributes of this {@link Builder}
		 */
		public Transaction build() {
			return new Transaction(this);
		}
	}
}
