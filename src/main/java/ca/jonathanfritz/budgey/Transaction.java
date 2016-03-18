package ca.jonathanfritz.budgey;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.money.Money;
import org.joda.time.DateTime;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a transaction at a financial institution. Two transactions are equal if and only if they share the same
 * transaction date, description and amount.
 */
public class Transaction implements ResultSetMapper<Transaction> {
	private final String accountNumber;
	private final DateTime dateUtc;
	private final int order;
	private final String description;
	private final Money amount;

	@JsonCreator
	public Transaction(@JsonProperty("accountNumber") String accountNumber, @JsonProperty("dateUtc") DateTime dateUtc, @JsonProperty("order") int order, @JsonProperty("description") String description, @JsonProperty("amount") Money amount) {
		this.accountNumber = accountNumber;
		this.dateUtc = dateUtc;
		this.order = order;
		this.description = description;
		this.amount = amount;
	}

	private Transaction(Builder builder) {
		accountNumber = builder.accountNumber;
		dateUtc = builder.dateUtc;
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
	public DateTime getDateUtc() {
		return dateUtc;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amount == null) ? 0 : amount.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((dateUtc == null) ? 0 : dateUtc.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Transaction other = (Transaction) obj;
		if (amount == null) {
			if (other.amount != null) {
				return false;
			}
		} else if (!amount.equals(other.amount)) {
			return false;
		}
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		if (dateUtc == null) {
			if (other.dateUtc != null) {
				return false;
			}
		} else if (!dateUtc.equals(other.dateUtc)) {
			return false;
		}
		return true;
	}

	public static class Builder {
		private String accountNumber;
		private DateTime dateUtc;
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
		public Builder setDateUtc(DateTime dateUtc) {
			this.dateUtc = dateUtc;
			return this;
		}

		/**
		 * @param order for transactions that occur on the same date and don't have the required precision, this field
		 *            infers an ordering from the order they were received from the financial institution
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

	@Override
	public Transaction map(int index, ResultSet r, StatementContext ctx) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
}
