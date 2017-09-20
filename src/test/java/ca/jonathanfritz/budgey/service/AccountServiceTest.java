package ca.jonathanfritz.budgey.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.hamcrest.core.IsEqual;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import ca.jonathanfritz.budgey.Account;
import ca.jonathanfritz.budgey.AccountType;
import ca.jonathanfritz.budgey.Credentials;
import ca.jonathanfritz.budgey.Transaction;
import ca.jonathanfritz.budgey.guice.BudgeyModule;

public class AccountServiceTest {

	private AccountService service;

	@Before
	public void setup() {
		final Injector injector = Guice.createInjector(new BudgeyModule(), new AbstractModule() {

			@Override
			protected void configure() {
				bind(Credentials.class).toInstance(new Credentials(""));
			}
		});
		service = injector.getInstance(AccountService.class);
		service.initialize();
	}

	@Test
	public void insertRetrieveTest() {
		final int numStartingAccounts = service.getAccounts().size();

		final Random r = new Random();
		final String accountNumber = UUID.randomUUID().toString();

		// generate random transactions
		final int numTransactions = 10;
		double sum = 0;
		final List<Transaction> transactions = new ArrayList<>();
		for (int i = 0; i < numTransactions; i++) {
			final DateTime dateUtc = DateTime.now(DateTimeZone.UTC).plusHours(numTransactions - i);
			final String description = UUID.randomUUID().toString();
			final BigDecimal bigDecimal = BigDecimal.valueOf(r.nextDouble() * 27).setScale(2, RoundingMode.HALF_UP);
			final Money amount = Money.of(CurrencyUnit.CAD, bigDecimal);
			sum += amount.getAmount().doubleValue();
			transactions.add(new Transaction(accountNumber, dateUtc, i, description, amount));
		}

		// they belong to an account
		final AccountType type = AccountType.CHECKING;
		final Money balance = Money.of(CurrencyUnit.CAD, BigDecimal.valueOf(sum).setScale(2, RoundingMode.HALF_UP));
		final Account account = new Account(accountNumber, type, balance, transactions);

		// store it and retrieve it
		service.insertAccount(account);
		final List<Account> accounts = service.getAccounts();
		Assert.assertThat(accounts.size(), IsEqual.equalTo(numStartingAccounts + 1));

		// it should be identical to what we stored
		final Account account2 = accounts.stream()
		                                 .filter(a -> a.getAccountNumber().equals(accountNumber))
		                                 .findFirst()
		                                 .orElse(null);
		Assert.assertThat(account2.getBalance(), IsEqual.equalTo(account.getBalance()));
		Assert.assertThat(account2.getType(), IsEqual.equalTo(account.getType()));
		Assert.assertThat(account2.getTransactions().size(), IsEqual.equalTo(numTransactions));

		for (int i = 0; i < numTransactions; i++) {
			final Transaction transaction1 = transactions.get(i);
			final Transaction transaction2 = account2.getTransactions().get(i);

			Assert.assertThat(transaction2.getAccountNumber(), IsEqual.equalTo(transaction1.getAccountNumber()));
			Assert.assertThat(transaction2.getDescription(), IsEqual.equalTo(transaction1.getDescription()));
			Assert.assertThat(transaction2.getAmount(), IsEqual.equalTo(transaction1.getAmount()));
			Assert.assertThat(transaction2.getDateUtc(), IsEqual.equalTo(transaction1.getDateUtc()));
			Assert.assertThat(transaction2.getOrder(), IsEqual.equalTo(transaction1.getOrder()));
		}
	}
}
