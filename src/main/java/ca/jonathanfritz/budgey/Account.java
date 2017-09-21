package ca.jonathanfritz.budgey;

import java.util.ArrayList;
import java.util.List;

import org.joda.money.Money;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an account at a financial institution. Two accounts are equal if and only if they have the same account
 * number.
 */
public class Account {

	private final String accountNumber;
	private final AccountType type;
	private final Money balance;
	private final List<Transaction> transactions = new ArrayList<>();

	/**
	 * For Jackson
	 */
	@JsonCreator
	public Account(@JsonProperty("accountNumber") String accountNumber, @JsonProperty("type") AccountType type, @JsonProperty("balance") Money balance, @JsonProperty("transactions") List<Transaction> transactions) {
		this.accountNumber = accountNumber;
		this.type = type;
		this.balance = balance;
		this.transactions.addAll(transactions);
	}

	public Account(Builder builder) {
		accountNumber = builder.accountNumber;
		type = builder.type;
		balance = builder.balance;
		transactions.addAll(builder.transactions);
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public AccountType getType() {
		return type;
	}

	public Money getBalance() {
		return balance;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accountNumber == null) ? 0 : accountNumber.hashCode());
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
		final Account other = (Account) obj;
		if (accountNumber == null) {
			if (other.accountNumber != null) {
				return false;
			}
		} else if (!accountNumber.equals(other.accountNumber)) {
			return false;
		}
		return true;
	}

	public static Builder newBuilder(String accountNumber) {
		return new Builder(accountNumber);
	}

	public static Builder newBuilder(Account account) {
		return new Builder(account.accountNumber).setBalance(account.balance)
		                                         .addTransactions(account.transactions)
		                                         .setType(account.type);
	}

	public static class Builder {

		private final String accountNumber;
		private AccountType type;
		private Money balance;
		private final List<Transaction> transactions = new ArrayList<>();

		public Builder(String accountNumber) {
			this.accountNumber = accountNumber;
		}

		public Builder setType(AccountType type) {
			this.type = type;
			return this;
		}

		// TODO: should this be removed, and updated implicitly when transactions are added?
		public Builder setBalance(Money balance) {
			this.balance = balance;
			return this;
		}

		public Builder addTransactions(List<Transaction> transactions) {
			this.transactions.addAll(transactions);
			return this;
		}

		public Account build() {
			return new Account(this);
		}
	}
}
