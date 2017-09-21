package ca.jonathanfritz.budgey.dao;

import java.math.BigDecimal;
import java.util.Set;

import org.hamcrest.core.IsEqual;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.Assert;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;

import com.google.inject.Guice;
import com.google.inject.Injector;

import ca.jonathanfritz.budgey.Account;
import ca.jonathanfritz.budgey.AccountType;
import ca.jonathanfritz.budgey.guice.BudgeyModule;
import ca.jonathanfritz.budgey.guice.CredentialsModule;
import ca.jonathanfritz.budgey.util.TestHelper;

public class AccountDAOTest {

	private final Injector injector;
	private final DBI dbi;
	private final AccountDAO accountDao;

	public AccountDAOTest() {
		injector = Guice.createInjector(new BudgeyModule(), new CredentialsModule(null));
		dbi = injector.getInstance(DBI.class);
		accountDao = injector.getInstance(AccountDAO.class);
		accountDao.createTable();
	}

	/**
	 * Ensures that we can add a new account
	 */
	@Test
	public void addNewAccountTest() {
		try (AutoCommittingHandle handle = new AutoCommittingHandle(dbi)) {
			try {
				final int initialNumAccounts = accountDao.getAccounts(handle).size();

				final Account account = TestHelper.generateRandomAccount();
				accountDao.insertAccount(handle, account);

				final Set<Account> accounts = accountDao.getAccounts(handle);
				Assert.assertThat(accounts.size(), IsEqual.equalTo(initialNumAccounts + 1));
				final Account foundAccount = accounts.stream()
				                                     .filter(a -> a.getAccountNumber()
				                                                   .equals(account.getAccountNumber()))
				                                     .findFirst()
				                                     .get();
				compare(account, foundAccount);
			} catch (final Exception ex) {
				handle.rollback();
				throw ex;
			}
		}
	}

	/**
	 * Ensures that we can update an existing account
	 */
	@Test
	public void updateAccountTest() {
		try (AutoCommittingHandle handle = new AutoCommittingHandle(dbi)) {
			try {
				final Account account = TestHelper.generateRandomAccount();
				accountDao.insertAccount(handle, account);

				final Account updatedAccount = Account.newBuilder(account)
				                                      .setType(AccountType.VISA)
				                                      .setBalance(Money.of(CurrencyUnit.USD, BigDecimal.valueOf(1023.56)))
				                                      .build();
				accountDao.updateAccount(handle, updatedAccount);

				final Set<Account> accounts = accountDao.getAccounts(handle);
				final Account foundAccount = accounts.stream()
				                                     .filter(a -> a.getAccountNumber()
				                                                   .equals(account.getAccountNumber()))
				                                     .findFirst()
				                                     .get();
				compare(updatedAccount, foundAccount);
			} catch (final Exception ex) {
				handle.rollback();
				throw ex;
			}
		}
	}

	public void compare(Account a1, Account a2) {
		// we don't care about transactions, because the dao on test doesn't care about them either
		Assert.assertThat(a1.getAccountNumber(), IsEqual.equalTo(a2.getAccountNumber()));
		Assert.assertThat(a1.getBalance(), IsEqual.equalTo(a2.getBalance()));
		Assert.assertThat(a1.getType(), IsEqual.equalTo(a2.getType()));
	}
}
