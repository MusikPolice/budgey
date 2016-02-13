package ca.jonathanfritz.budgey.importer;

import org.hamcrest.core.IsEqual;
import org.joda.money.CurrencyUnit;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import ca.jonathanfritz.budgey.Transaction;
import ca.jonathanfritz.budgey.util.ClasspathLoader;

public class RoyalBankCSVParserTest {

	private static final String csv = "importer/csv/rbc-chequing-with-quotes.csv";
	private static final String usdCsv = "importer/csv/rbc-usd-transaction.csv";

	@Test
	public void validTransactionParsingTest() {
		final String line = ClasspathLoader.load(csv);
		final String[] fields = FieldSanitizer.sanitizeFields(line.split(","));
		final RoyalBankCSVParser parser = new RoyalBankCSVParser();
		final Transaction transaction = parser.parse(fields);

		Assert.assertThat(transaction.getAccountNumber(), IsEqual.equalTo("079022-11555"));
		Assert.assertThat(transaction.getDescription(), IsEqual.equalTo("Email Trfs. INTERAC E-TRF- 7336"));
		Assert.assertThat(transaction.getAmount().getAmount().doubleValue(), IsEqual.equalTo(53.00D));
		Assert.assertThat(transaction.getAmount().getCurrencyUnit(), IsEqual.equalTo(CurrencyUnit.CAD));
		Assert.assertThat(transaction.getTransactionDate(), IsEqual.equalTo((new DateTime())
				.withTimeAtStartOfDay()
				.withYear(2015)
				.withMonthOfYear(10)
				.withDayOfMonth(13)));
	}

	@Test
	public void usdParsingTest() {
		final String line = ClasspathLoader.load(usdCsv);
		final String[] fields = FieldSanitizer.sanitizeFields(line.split(","));
		final RoyalBankCSVParser parser = new RoyalBankCSVParser();
		final Transaction transaction = parser.parse(fields);

		Assert.assertThat(transaction.getAccountNumber(), IsEqual.equalTo("079022-11555"));
		Assert.assertThat(transaction.getDescription(), IsEqual.equalTo("Email Trfs. INTERAC E-TRF- 7336"));
		Assert.assertThat(transaction.getAmount().getAmount().doubleValue(), IsEqual.equalTo(75.00D));
		Assert.assertThat(transaction.getAmount().getCurrencyUnit(), IsEqual.equalTo(CurrencyUnit.USD));
		Assert.assertThat(transaction.getTransactionDate(), IsEqual.equalTo((new DateTime())
				.withTimeAtStartOfDay()
				.withYear(2015)
				.withMonthOfYear(10)
				.withDayOfMonth(13)));
	}

	@Test
	public void orderTest() {

		final String line1 = "Chequing,079022-11555,10/13/2015,,\"Email Trfs\",\"INTERAC E-TRF- 7336 \",53.00,,";
		final String line2 = "Chequing,079022-11555,10/14/2015,,\"Email Trfs\",\"INTERAC E-TRF- 7336 \",53.00,,";
		final String line3 = "Chequing,079022-11555,10/14/2015,,\"Email Trfs\",\"INTERAC E-TRF- 7336 \",53.00,,";
		final String line4 = "Chequing,079022-11555,10/15/2015,,\"Email Trfs\",\"INTERAC E-TRF- 7336 \",53.00,,";

		final String[] sanitized1 = FieldSanitizer.sanitizeFields(line1.split(","));
		final String[] sanitized2 = FieldSanitizer.sanitizeFields(line2.split(","));
		final String[] sanitized3 = FieldSanitizer.sanitizeFields(line3.split(","));
		final String[] sanitized4 = FieldSanitizer.sanitizeFields(line4.split(","));

		final RoyalBankCSVParser parser = new RoyalBankCSVParser();

		final Transaction transaction1 = parser.parse(sanitized1);
		final Transaction transaction2 = parser.parse(sanitized2);
		final Transaction transaction3 = parser.parse(sanitized3);
		final Transaction transaction4 = parser.parse(sanitized4);

		Assert.assertThat(transaction1.getOrder(), IsEqual.equalTo(0));
		Assert.assertThat(transaction2.getOrder(), IsEqual.equalTo(0));
		Assert.assertThat(transaction3.getOrder(), IsEqual.equalTo(1));
		Assert.assertThat(transaction4.getOrder(), IsEqual.equalTo(0));

	}

}
