package ca.jonathanfritz.budgey.importer.csv;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import ca.jonathanfritz.budgey.Transaction;

import com.google.common.base.Joiner;

public class RoyalBankCSVParser extends AbstractCSVParser implements CSVParser {

	// date format is 10/13/2015
	private final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
	.appendMonthOfYear(2)
	.appendLiteral('/')
	.appendDayOfMonth(2)
	.appendLiteral('/')
	.appendYear(4, 4)
	.toFormatter();

	private final NumberFormat cadFormat = NumberFormat.getNumberInstance(Locale.CANADA);
	private final NumberFormat usdFormat = NumberFormat.getNumberInstance(Locale.US);

	@Override
	public Transaction parse(String[] fields) {
		// try to discard headers
		if ("Account Type".equals(fields[0])) {
			return null;
		}

		String description = fields[4];
		if (!StringUtils.isBlank(fields[5])) {
			description += ". " + fields[5];
		}

		Money amount = Money.zero(CurrencyUnit.CAD);
		try {
			if (fields.length > 6 && StringUtils.isNotBlank(fields[6])) {
				final BigDecimal cad = new BigDecimal(cadFormat.parse(fields[6]).toString());
				amount = Money.of(CurrencyUnit.CAD, cad);
			} else if (fields.length > 7 && StringUtils.isNotBlank(fields[7])) {
				final BigDecimal usd = new BigDecimal(usdFormat.parse(fields[7]).toString());
				amount = Money.of(CurrencyUnit.USD, usd);
			}
		} catch (final ParseException e) {
			throw new RuntimeException("Failed to parse transaction [" + Joiner.on(",").join(fields) + "]", e);
		}

		final DateTime date = formatter.parseDateTime(fields[2]);

		return Transaction
				.newBuilder()
				.setAccountNumber(fields[1])
				.setDateUtc(date)
				.setOrder(getOrderForDate(date))
				.setDescription(description)
				.setAmount(amount)
				.build();
	}
}
