package ca.jonathanfritz.budgey.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import ca.jonathanfritz.budgey.Transaction;

public class TransactionResultSetMapper implements ResultSetMapper<Transaction> {

	/**
	 * Maps an in-memory database row into a useful object
	 * @see Transaction
	 * @see TransactionDAO
	 */
	@Override
	public Transaction map(int index, ResultSet r, StatementContext ctx) throws SQLException {
		final String accountNumber = r.getString("account_number");
		final DateTime dateUtc = new DateTime(r.getLong("time_millis"), DateTimeZone.UTC);
		final int order = r.getInt("order");
		final String description = r.getString("description");
		final Money amount = Money.of(CurrencyUnit.getInstance(r.getString("currency")), r.getBigDecimal("amount"));
		return new Transaction(accountNumber, dateUtc, order, description, amount);
	}
}
