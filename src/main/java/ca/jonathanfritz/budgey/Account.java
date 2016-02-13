package ca.jonathanfritz.budgey;

import java.util.HashSet;
import java.util.Set;

import org.joda.money.Money;

/**
 * Represents an account at a financial institution. Two accounts are equal if and only if they have the same account
 * number.
 */
public class Account {

	private final String accountNumber;
	private final AccountType type;
	private final Money balance;
	private final Set<Transaction> transactions = new HashSet<>();

	public Account(String accountNumber, AccountType type, Money balance) {
		this.accountNumber = accountNumber;
		this.type = type;
		this.balance = balance;
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
}
