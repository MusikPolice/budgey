package ca.jonathanfritz.budgey.importer;

import ca.jonathanfritz.budgey.Transaction;
import ca.jonathanfritz.budgey.util.ClasspathLoader;
import org.hamcrest.core.IsEqual;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

public class ScotiabankCSVParserTest {

	private static final String csv = "importer/csv/scotia.csv";

	private static final String ACCOUNT_NUMBER = "1234";

	@Test
	public void validTransactionParsingTest() {

		final String line = ClasspathLoader.load(csv);
		final String[] fields = FieldSanitizer.sanitizeFields(line.split(","));
		final ScotiabankCSVParser parser = new ScotiabankCSVParser(ACCOUNT_NUMBER);
		final Transaction transaction = parser.parse(fields);

		Assert.assertThat(transaction.getDescription(), IsEqual.equalTo("MOVATI ATHLETIC WATERLOO WATERLOO     ON"));
		Assert.assertThat(transaction.getAmount().getAmount().doubleValue(), IsEqual.equalTo(-508.50D));
		Assert.assertThat(transaction.getTransactionDate(), IsEqual.equalTo((new DateTime())
				.withTimeAtStartOfDay()
				.withYear(2015)
				.withMonthOfYear(9)
				.withDayOfMonth(1)));
		Assert.assertThat(transaction.getAccountNumber(), IsEqual.equalTo(ACCOUNT_NUMBER));

	}

	@Test
	public void orderTest() {

		final String[] line1 = FieldSanitizer.sanitizeFields("9/1/15,line1,-508.50".split(","));
		final String[] line2 = FieldSanitizer.sanitizeFields("9/2/15,line2,-508.50".split(","));
		final String[] line3 = FieldSanitizer.sanitizeFields("9/2/15,line3,-508.50".split(","));
		final String[] line4 = FieldSanitizer.sanitizeFields("9/3/15,line4,-508.50".split(","));

		final ScotiabankCSVParser parser = new ScotiabankCSVParser(ACCOUNT_NUMBER);

		final Transaction transaction1 = parser.parse(line1);
		final Transaction transaction2 = parser.parse(line2);
		final Transaction transaction3 = parser.parse(line3);
		final Transaction transaction4 = parser.parse(line4);

		Assert.assertThat(transaction1.getOrder(), IsEqual.equalTo(0));
		Assert.assertThat(transaction2.getOrder(), IsEqual.equalTo(0));
		Assert.assertThat(transaction3.getOrder(), IsEqual.equalTo(1));
		Assert.assertThat(transaction4.getOrder(), IsEqual.equalTo(0));

	}

}
