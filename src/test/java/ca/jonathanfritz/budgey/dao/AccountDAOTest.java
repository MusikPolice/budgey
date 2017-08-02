package ca.jonathanfritz.budgey.dao;

import java.util.Set;

import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import ca.jonathanfritz.budgey.Account;
import ca.jonathanfritz.budgey.guice.BudgeyModule;
import ca.jonathanfritz.budgey.guice.CredentialsModule;
import ca.jonathanfritz.budgey.util.TestHelper;

public class AccountDAOTest {

	private final Injector injector;
	private final AccountDAO accountDao;

	public AccountDAOTest() {
		injector = Guice.createInjector(new BudgeyModule(), new CredentialsModule(null));
		accountDao = injector.getInstance(AccountDAO.class);
		accountDao.createTable();
	}

	/**
	 * Ensures that we can add a new account
	 */
	@Test
	public void addNewAccountTest() {
		final int initialNumAccounts = accountDao.getAccounts().size();

		final Account account = TestHelper.generateRandomAccount();
		Assert.assertTrue(accountDao.insertAccount(account));

		final Set<Account> accounts = accountDao.getAccounts();
		Assert.assertThat(accounts.size(), IsEqual.equalTo(initialNumAccounts + 1));
		final Account foundAccount = accounts.stream()
		                                     .filter(a -> a.getAccountNumber()
		                                                   .equals(account.getAccountNumber()))
		                                     .findFirst()
		                                     .get();
		compare(account, foundAccount);
	}

	public void compare(Account a1, Account a2) {
		Assert.assertThat(a1.getAccountNumber(), IsEqual.equalTo(a2.getAccountNumber()));
		Assert.assertThat(a1.getBalance(), IsEqual.equalTo(a2.getBalance()));
		Assert.assertThat(a1.getType(), IsEqual.equalTo(a2.getType()));
		// TODO: remove transactions from account or compare them?
	}
}
