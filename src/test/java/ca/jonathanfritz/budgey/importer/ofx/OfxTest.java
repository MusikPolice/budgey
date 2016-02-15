package ca.jonathanfritz.budgey.importer.ofx;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.SortedSet;

import net.sf.ofx4j.domain.data.ResponseEnvelope;
import net.sf.ofx4j.domain.data.ResponseMessage;
import net.sf.ofx4j.domain.data.ResponseMessageSet;
import net.sf.ofx4j.domain.data.banking.BankStatementResponseTransaction;
import net.sf.ofx4j.domain.data.creditcard.CreditCardStatementResponseTransaction;
import net.sf.ofx4j.io.AggregateUnmarshaller;
import net.sf.ofx4j.io.OFXParseException;

import org.hamcrest.core.IsEqual;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import ca.jonathanfritz.budgey.Transaction;
import ca.jonathanfritz.budgey.importer.AbstractParser;

import com.google.common.collect.Lists;

public class OfxTest extends AbstractParser {

	@Test
	public void testScotia() throws IOException, OFXParseException {

		final Double availableBalance = getAvailableBalance("importer/ofx/scotia.ofx");
		final List<Transaction> transactions = getTransactions("importer/ofx/scotia.ofx");

		Assert.assertThat(availableBalance, IsEqual.equalTo(-12780.87));

		Assert.assertThat(transactions.get(0).getAccountNumber(), IsEqual.equalTo("1234567890129044"));
		Assert.assertThat(transactions.get(0).getAmount(), IsEqual.equalTo(Money.of(CurrencyUnit.CAD, 99.42)));
		Assert.assertThat(transactions.get(0).getDescription(), IsEqual.equalTo("PAYMENT"));
		Assert.assertThat(transactions.get(0).getOrder(), IsEqual.equalTo(0));
		Assert.assertThat(transactions.get(0).getDateUtc(), IsEqual.equalTo(DateTime.parse("2016-01-15")));

		Assert.assertThat(transactions.get(1).getAccountNumber(), IsEqual.equalTo("1234567890129044"));
		Assert.assertThat(transactions.get(1).getAmount(), IsEqual.equalTo(Money.of(CurrencyUnit.CAD, -100.81)));
		Assert.assertThat(transactions.get(1).getDescription(), IsEqual.equalTo("Store 1"));
		Assert.assertThat(transactions.get(1).getOrder(), IsEqual.equalTo(0));
		Assert.assertThat(transactions.get(1).getDateUtc(), IsEqual.equalTo(DateTime.parse("2016-02-11")));

		Assert.assertThat(transactions.get(2).getAccountNumber(), IsEqual.equalTo("1234567890129044"));
		Assert.assertThat(transactions.get(2).getAmount(), IsEqual.equalTo(Money.of(CurrencyUnit.CAD, -101.91)));
		Assert.assertThat(transactions.get(2).getDescription(), IsEqual.equalTo("Store 2"));
		Assert.assertThat(transactions.get(2).getOrder(), IsEqual.equalTo(1));
		Assert.assertThat(transactions.get(2).getDateUtc(), IsEqual.equalTo(DateTime.parse("2016-02-11")));

	}

	@Test
	public void testTangerine() throws IOException, OFXParseException {

		final Double availableBalance = getAvailableBalance("importer/ofx/tangerine.ofx");
		final List<Transaction> transactions = getTransactions("importer/ofx/tangerine.ofx");

		Assert.assertThat(availableBalance, IsEqual.equalTo(100.33));

		Assert.assertThat(transactions.get(0).getAccountNumber(), IsEqual.equalTo("1234567890"));
		Assert.assertThat(transactions.get(0).getAmount(), IsEqual.equalTo(Money.of(CurrencyUnit.CAD, -100.12)));
		Assert.assertThat(transactions.get(0).getDescription(), IsEqual.equalTo("EFT"));
		Assert.assertThat(transactions.get(0).getOrder(), IsEqual.equalTo(0));
		Assert.assertThat(transactions.get(0).getDateUtc(), IsEqual.equalTo(DateTime.parse("2015-03-02")));

		Assert.assertThat(transactions.get(1).getAccountNumber(), IsEqual.equalTo("1234567890"));
		Assert.assertThat(transactions.get(1).getAmount(), IsEqual.equalTo(Money.of(CurrencyUnit.CAD, -100.88)));
		Assert.assertThat(transactions.get(1).getDescription(), IsEqual.equalTo("Bill Payment"));
		Assert.assertThat(transactions.get(1).getOrder(), IsEqual.equalTo(0));
		Assert.assertThat(transactions.get(1).getDateUtc(), IsEqual.equalTo(DateTime.parse("2015-03-03")));

		Assert.assertThat(transactions.get(2).getAccountNumber(), IsEqual.equalTo("1234567890"));
		Assert.assertThat(transactions.get(2).getAmount(), IsEqual.equalTo(Money.of(CurrencyUnit.CAD, 100.93)));
		Assert.assertThat(transactions.get(2).getDescription(), IsEqual.equalTo("Internet Deposit from Tangerine"));
		Assert.assertThat(transactions.get(2).getOrder(), IsEqual.equalTo(0));
		Assert.assertThat(transactions.get(2).getDateUtc(), IsEqual.equalTo(DateTime.parse("2015-03-06")));

		Assert.assertThat(transactions.get(3).getAccountNumber(), IsEqual.equalTo("1234567890"));
		Assert.assertThat(transactions.get(3).getAmount(), IsEqual.equalTo(Money.of(CurrencyUnit.CAD, -100.53)));
		Assert.assertThat(transactions.get(3).getDescription(), IsEqual.equalTo("Withdrawal ABM"));
		Assert.assertThat(transactions.get(3).getOrder(), IsEqual.equalTo(1));
		Assert.assertThat(transactions.get(3).getDateUtc(), IsEqual.equalTo(DateTime.parse("2015-03-06")));

	}

	@Test
	public void testRoyal() throws IOException, OFXParseException {

		final Double availableBalance = getAvailableBalance("importer/ofx/royal.ofx");
		final List<Transaction> transactions = getTransactions("importer/ofx/royal.ofx");

		Assert.assertThat(availableBalance, IsEqual.equalTo(-100.03));

		Assert.assertThat(transactions.get(0).getAccountNumber(), IsEqual.equalTo("1234567890123456"));
		Assert.assertThat(transactions.get(0).getAmount(), IsEqual.equalTo(Money.of(CurrencyUnit.CAD, 100.01)));
		Assert.assertThat(transactions.get(0).getDescription(), IsEqual.equalTo("Test 1"));
		Assert.assertThat(transactions.get(0).getOrder(), IsEqual.equalTo(0));
		Assert.assertThat(transactions.get(0).getDateUtc(), IsEqual.equalTo(DateTime.parse("2016-02-08")));

		Assert.assertThat(transactions.get(1).getAccountNumber(), IsEqual.equalTo("1234567890123456"));
		Assert.assertThat(transactions.get(1).getAmount(), IsEqual.equalTo(Money.of(CurrencyUnit.CAD, -100.02)));
		Assert.assertThat(transactions.get(1).getDescription(), IsEqual.equalTo("Test 2"));
		Assert.assertThat(transactions.get(1).getOrder(), IsEqual.equalTo(0));
		Assert.assertThat(transactions.get(1).getDateUtc(), IsEqual.equalTo(DateTime.parse("2016-01-18")));

	}

	private List<Transaction> getTransactions(final String resource) throws IOException, OFXParseException {

		final List<Transaction> transactions = Lists.newArrayList();

		final InputStream stream = this.getClass().getClassLoader().getResourceAsStream(resource);

		final AggregateUnmarshaller<ResponseEnvelope> unmarshaller = new AggregateUnmarshaller<>(ResponseEnvelope.class);
		final ResponseEnvelope envelope = unmarshaller.unmarshal(stream);

		final SortedSet<ResponseMessageSet> messageSets = envelope.getMessageSets();

		for (final ResponseMessageSet messageSet : messageSets) {

			final List<ResponseMessage> responseMessages = messageSet.getResponseMessages();
			for (final ResponseMessage responseMessage : responseMessages) {

				if (responseMessage instanceof CreditCardStatementResponseTransaction) {

					final CreditCardStatementResponseTransaction creditCardStatementResponseTransaction = (CreditCardStatementResponseTransaction) responseMessage;

					final String accountNumber = creditCardStatementResponseTransaction.getMessage().getAccount().getAccountNumber();
					final String currencyCode = creditCardStatementResponseTransaction.getMessage().getCurrencyCode();

					creditCardStatementResponseTransaction.getMessage().getTransactionList().getTransactions().forEach(ofxTransaction -> {

						final DateTime date = new DateTime(ofxTransaction.getDatePosted()).withTimeAtStartOfDay();

						final Transaction transaction = Transaction.newBuilder()
						        .setAccountNumber(accountNumber)
						        .setAmount(Money.parse(String.format("%s %.2f", currencyCode, ofxTransaction.getAmount())))
						        .setDescription(ofxTransaction.getName())
						        .setOrder(getOrderForDate(date))
						        .setDateUtc(date)
						        .build();

						transactions.add(transaction);

					});

				}

				if (responseMessage instanceof BankStatementResponseTransaction) {

					final BankStatementResponseTransaction bankStatementResponseTransaction = (BankStatementResponseTransaction) responseMessage;

					final String accountNumber = bankStatementResponseTransaction.getMessage().getAccount().getAccountNumber();
					final String currencyCode = bankStatementResponseTransaction.getMessage().getCurrencyCode();

					bankStatementResponseTransaction.getMessage().getTransactionList().getTransactions().forEach(ofxTransaction -> {

						final DateTime date = new DateTime(ofxTransaction.getDatePosted()).withTimeAtStartOfDay();

						final Transaction transaction = Transaction.newBuilder()
						        .setAccountNumber(accountNumber)
						        .setAmount(Money.parse(String.format("%s %.2f", currencyCode, ofxTransaction.getAmount())))
						        .setDescription(ofxTransaction.getName())
						        .setOrder(getOrderForDate(date))
						        .setDateUtc(date)
						        .build();

						transactions.add(transaction);

					});

				}

			}

		}

		return transactions;

	}

	private Double getAvailableBalance(final String resource) throws IOException, OFXParseException {

		final InputStream stream = this.getClass().getClassLoader().getResourceAsStream(resource);

		final AggregateUnmarshaller<ResponseEnvelope> unmarshaller = new AggregateUnmarshaller<>(ResponseEnvelope.class);
		final ResponseEnvelope envelope = unmarshaller.unmarshal(stream);

		final SortedSet<ResponseMessageSet> messageSets = envelope.getMessageSets();

		for (final ResponseMessageSet messageSet : messageSets) {

			final List<ResponseMessage> responseMessages = messageSet.getResponseMessages();
			for (final ResponseMessage responseMessage : responseMessages) {

				if (responseMessage instanceof CreditCardStatementResponseTransaction) {

					final CreditCardStatementResponseTransaction creditCardStatementResponseTransaction = (CreditCardStatementResponseTransaction) responseMessage;
					return creditCardStatementResponseTransaction.getMessage().getAvailableBalance().getAmount();

				}

				if (responseMessage instanceof BankStatementResponseTransaction) {

					final BankStatementResponseTransaction bankStatementResponseTransaction = (BankStatementResponseTransaction) responseMessage;
					return bankStatementResponseTransaction.getMessage().getAvailableBalance().getAmount();

				}

			}

		}

		return null;

	}

	@Override
	public List<Transaction> parse(final Path path) throws IOException {
		return null;
	}
}
