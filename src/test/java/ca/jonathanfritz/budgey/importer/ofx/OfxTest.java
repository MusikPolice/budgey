package ca.jonathanfritz.budgey.importer.ofx;

import ca.jonathanfritz.budgey.Transaction;
import ca.jonathanfritz.budgey.importer.AbstractCSVParser;
import com.google.common.collect.Lists;
import net.sf.ofx4j.domain.data.MessageSetType;
import net.sf.ofx4j.domain.data.ResponseEnvelope;
import net.sf.ofx4j.domain.data.ResponseMessage;
import net.sf.ofx4j.domain.data.ResponseMessageSet;
import net.sf.ofx4j.domain.data.creditcard.CreditCardStatementResponseTransaction;
import net.sf.ofx4j.io.AggregateUnmarshaller;
import net.sf.ofx4j.io.OFXParseException;
import org.hamcrest.core.IsEqual;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.SortedSet;

public class OfxTest extends AbstractCSVParser {

	@Test
	public void test() throws IOException, OFXParseException {

		final List<Transaction> transactions = Lists.newArrayList();
		double availableBalance = 0d;

		final InputStream stream = this.getClass().getClassLoader().getResourceAsStream("importer/ofx/scotia.ofx");

		final AggregateUnmarshaller<ResponseEnvelope> unmarshaller = new AggregateUnmarshaller<>(ResponseEnvelope.class);
		final ResponseEnvelope envelope = unmarshaller.unmarshal(stream);

		final SortedSet<ResponseMessageSet> messageSets = envelope.getMessageSets();

		for(final ResponseMessageSet messageSet : messageSets) {

			if(messageSet.getType().equals(MessageSetType.creditcard)) {

				final List<ResponseMessage> responseMessages = messageSet.getResponseMessages();
				for(final ResponseMessage responseMessage : responseMessages) {

					if(responseMessage instanceof CreditCardStatementResponseTransaction) {

						final CreditCardStatementResponseTransaction creditCardStatementResponseTransaction = (CreditCardStatementResponseTransaction) responseMessage;

						availableBalance = creditCardStatementResponseTransaction.getMessage().getAvailableBalance().getAmount();
						final String accountNumber = creditCardStatementResponseTransaction.getMessage().getAccount().getAccountNumber();
						final String currencyCode = creditCardStatementResponseTransaction.getMessage().getCurrencyCode();

						creditCardStatementResponseTransaction.getMessage().getTransactionList().getTransactions().forEach(ofxTransaction -> {

							final DateTime date = new DateTime(ofxTransaction.getDatePosted()).withTimeAtStartOfDay();

							final Transaction transaction = Transaction.newBuilder()
									.setAccountNumber(accountNumber)
									.setAmount(Money.parse(String.format("%s %.2f", currencyCode, ofxTransaction.getAmount())))
									.setDescription(ofxTransaction.getName())
									.setOrder(this.getOrderForDate(date))
									.setTransactionDate(date)
									.build();

							transactions.add(transaction);

						});

					}

				}

			}

		}

		transactions.forEach(System.out::println);

		Assert.assertThat(availableBalance, IsEqual.equalTo(-12780.87));

		Assert.assertThat(transactions.get(0).getAccountNumber(), IsEqual.equalTo("1234567890129044"));
		Assert.assertThat(transactions.get(0).getAmount(), IsEqual.equalTo(Money.of(CurrencyUnit.CAD, 99.42)));
		Assert.assertThat(transactions.get(0).getDescription(), IsEqual.equalTo("PAYMENT"));
		Assert.assertThat(transactions.get(0).getOrder(), IsEqual.equalTo(0));
		Assert.assertThat(transactions.get(0).getTransactionDate(), IsEqual.equalTo(DateTime.parse("2016-01-15")));

		Assert.assertThat(transactions.get(1).getAccountNumber(), IsEqual.equalTo("1234567890129044"));
		Assert.assertThat(transactions.get(1).getAmount(), IsEqual.equalTo(Money.of(CurrencyUnit.CAD, -100.81)));
		Assert.assertThat(transactions.get(1).getDescription(), IsEqual.equalTo("Store 1"));
		Assert.assertThat(transactions.get(1).getOrder(), IsEqual.equalTo(0));
		Assert.assertThat(transactions.get(1).getTransactionDate(), IsEqual.equalTo(DateTime.parse("2016-02-11")));

		Assert.assertThat(transactions.get(2).getAccountNumber(), IsEqual.equalTo("1234567890129044"));
		Assert.assertThat(transactions.get(2).getAmount(), IsEqual.equalTo(Money.of(CurrencyUnit.CAD, -101.91)));
		Assert.assertThat(transactions.get(2).getDescription(), IsEqual.equalTo("Store 2"));
		Assert.assertThat(transactions.get(2).getOrder(), IsEqual.equalTo(1));
		Assert.assertThat(transactions.get(2).getTransactionDate(), IsEqual.equalTo(DateTime.parse("2016-02-11")));

	}

}
