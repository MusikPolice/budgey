package ca.jonathanfritz.budgey.importer.ofx;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
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

import org.joda.money.Money;
import org.joda.time.DateTime;

import ca.jonathanfritz.budgey.Transaction;
import ca.jonathanfritz.budgey.importer.AbstractParser;

import com.google.common.collect.Lists;

public class OfxParser extends AbstractParser {

	@Override
	public List<Transaction> parse(final Path path) throws IOException {

		final List<Transaction> transactions = Lists.newArrayList();
		final InputStream stream = Files.newInputStream(path);

		final AggregateUnmarshaller<ResponseEnvelope> unmarshaller = new AggregateUnmarshaller<>(ResponseEnvelope.class);

		final ResponseEnvelope envelope;
		try {
			envelope = unmarshaller.unmarshal(stream);
		} catch (final OFXParseException e) {
			throw new IOException("Failed to parse file", e);
		}

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

}
