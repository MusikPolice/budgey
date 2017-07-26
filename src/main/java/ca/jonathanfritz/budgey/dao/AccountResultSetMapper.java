package ca.jonathanfritz.budgey.dao;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import ca.jonathanfritz.budgey.Account;
import ca.jonathanfritz.budgey.AccountType;
import ca.jonathanfritz.budgey.Transaction;

public class AccountResultSetMapper implements ResultSetMapper<Account> {
	/**
	 * Maps an in-memory database row into a useful object
	 * @see Account
	 * @see AccountDAO
	 */
	@Override
	public Account map(int index, ResultSet r, StatementContext ctx) throws SQLException {
		final String accountNumber = r.getString("account_number");
		final AccountType accountType = AccountType.fromString(r.getString("type"));
		final BigDecimal amount = r.getBigDecimal("balance");
		final CurrencyUnit currency = CurrencyUnit.of(r.getString("currency"));
		final Money balance = Money.of(currency, amount);
		return new Account(accountNumber, accountType, balance, new ArrayList<Transaction>());
	}
}
