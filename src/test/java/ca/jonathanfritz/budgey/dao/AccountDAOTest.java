package ca.jonathanfritz.budgey.dao;

import java.util.Set;
import java.util.UUID;

import org.hamcrest.core.IsEqual;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Assert;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import ca.jonathanfritz.budgey.Account;
import ca.jonathanfritz.budgey.AccountType;
import ca.jonathanfritz.budgey.guice.BudgeyModule;
import ca.jonathanfritz.budgey.guice.CredentialsModule;

public class AccountDAOTest {

	private final AccountDAO accountDao;

	public AccountDAOTest() {
		final Injector injector = Guice.createInjector(new BudgeyModule(), new CredentialsModule(null));
		accountDao = injector.getInstance(AccountDAO.class);
		accountDao.createTable();
	}

	/**
	 * Ensures that we can add a new account
	 */
	@Test
	public void addNewAccountTest() {
		final int initialNumAccounts = accountDao.getAccounts()
		        .size();

		final String accountNumber = UUID.randomUUID()
		        .toString();
		final AccountType type = AccountType.CHECKING;
		final Money balance = Money.of(CurrencyUnit.CAD, 123.5);
		Assert.assertTrue(accountDao.insertAccount(accountNumber, type, balance));

		final Set<Account> accounts = accountDao.getAccounts();
		Assert.assertThat(accounts.size(), IsEqual.equalTo(initialNumAccounts + 1));
		final Account foundAccount = accounts.stream()
		        .filter(a -> a.getAccountNumber()
		                .equals(accountNumber))
		        .findFirst()
		        .get();
		Assert.assertThat(foundAccount.getAccountNumber(), IsEqual.equalTo(accountNumber));
		Assert.assertThat(foundAccount.getBalance(), IsEqual.equalTo(balance));
		Assert.assertThat(foundAccount.getType(), IsEqual.equalTo(type));
	}
}
