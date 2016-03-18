package ca.jonathanfritz.budgey;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import ca.jonathanfritz.budgey.dao.AccountDAO;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an account at a financial institution. Two accounts are equal if and only if they have the same account
 * number.
 */
public class Account implements ResultSetMapper<Account> {

	private final String accountNumber;
	private final AccountType type;
	private final Money balance;
	private final Set<Transaction> transactions;

	@JsonCreator
	public Account(@JsonProperty("accountNumber") String accountNumber, @JsonProperty("type") AccountType type, @JsonProperty("balance") Money balance, @JsonProperty("transactions") Set<Transaction> transactions) {
		this.accountNumber = accountNumber;
		this.type = type;
		this.balance = balance;
		this.transactions = transactions;
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

	public Set<Transaction> getTransactions() {
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

	/**
	 * Maps an in-memory database row into a useful object. See {@link AccountDAO} for more details.
	 */
	@Override
	public Account map(int index, ResultSet r, StatementContext ctx) throws SQLException {
		final String accountNumber = r.getString("account_number");
		final AccountType accountType = AccountType.fromString(r.getString("type"));
		final BigDecimal amount = r.getBigDecimal("balance");
		final CurrencyUnit currency = CurrencyUnit.of(r.getString("currency"));
		final Money balance = Money.of(currency, amount);
		return new Account(accountNumber, accountType, balance, new HashSet<Transaction>());
	}
}
