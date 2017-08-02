package ca.jonathanfritz.budgey.dao;

import java.util.List;

import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import ca.jonathanfritz.budgey.Account;
import ca.jonathanfritz.budgey.Transaction;
import ca.jonathanfritz.budgey.guice.BudgeyModule;
import ca.jonathanfritz.budgey.guice.CredentialsModule;
import ca.jonathanfritz.budgey.util.TestHelper;

public class TransactionDAOTest {

	private final Injector injector;
	private final TransactionDAO transactionDao;

	public TransactionDAOTest() {
		injector = Guice.createInjector(new BudgeyModule(), new CredentialsModule(null));
		transactionDao = injector.getInstance(TransactionDAO.class);
		transactionDao.createTable();
	}

	@Test
	public void addTransactionsToAccountTest() {
		// create ten transactions
		final TestHelper helper = TestHelper.newBuilder(injector)
		                                    .withAccount()
		                                    .withTransactions(10)
		                                    .build();
		final Account account = helper.getAccounts().stream().findFirst().get();
		Assert.assertTrue(transactionDao.insertTransactions(helper.getTransactions(account)));

		// read 'em back
		final List<Transaction> foundTransactions = transactionDao.getTransactions(account.getAccountNumber());
		Assert.assertThat(foundTransactions.size(), IsEqual.equalTo(10));
		for (final Transaction t1 : foundTransactions) {
			final Transaction t2 = helper.getTransactions(account)
			                             .stream()
			                             .filter(t -> t.equals(t1))
			                             .findFirst()
			                             .orElse(null);
			compare(t1, t2);
		}
	}

	private void compare(Transaction t1, Transaction t2) {
		Assert.assertThat(t1.getAccountNumber(), IsEqual.equalTo(t2.getAccountNumber()));
		Assert.assertThat(t1.getDescription(), IsEqual.equalTo(t2.getDescription()));
		Assert.assertThat(t1.getAmount(), IsEqual.equalTo(t2.getAmount()));
		Assert.assertThat(t1.getDateUtc(), IsEqual.equalTo(t2.getDateUtc()));
		Assert.assertThat(t1.getOrder(), IsEqual.equalTo(t2.getOrder()));
	}
}