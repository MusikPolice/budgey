package ca.jonathanfritz.budgey.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.skife.jdbi.v2.DBI;

import com.google.inject.Injector;

import ca.jonathanfritz.budgey.Account;
import ca.jonathanfritz.budgey.AccountType;
import ca.jonathanfritz.budgey.Transaction;
import ca.jonathanfritz.budgey.dao.AccountDAO;
import ca.jonathanfritz.budgey.dao.AutoCommittingHandle;
import ca.jonathanfritz.budgey.dao.TransactionDAO;

public class TestHelper {

	private final Map<Account, List<Transaction>> accounts = new HashMap<>();

	private TestHelper(Builder builder) {
		accounts.putAll(builder.accounts);
	}

	public Set<Account> getAccounts() {
		return accounts.keySet();
	}

	public List<Transaction> getTransactions(Account account) {
		return accounts.getOrDefault(account, new ArrayList<>());
	}

	public static Builder newBuilder(Injector injector) {
		return new Builder(injector);
	}

	public static class Builder {

		private final Map<Account, List<Transaction>> accounts = new HashMap<>();

		private final Injector injector;

		private Builder(Injector injector) {
			this.injector = injector;
		}

		public TransactionBuilder withAccount() {
			final Account account = generateRandomAccount();
			accounts.put(account, new ArrayList<>());
			return new TransactionBuilder(this, account);
		}

		public Builder insertIntoDatabase() {
			final AccountDAO accountDao = injector.getInstance(AccountDAO.class);
			final TransactionDAO transactionDao = injector.getInstance(TransactionDAO.class);
			final DBI dbi = injector.getInstance(DBI.class);

			try (AutoCommittingHandle handle = new AutoCommittingHandle(dbi)) {
				try {
					for (final Entry<Account, List<Transaction>> entry : accounts.entrySet()) {
						accountDao.insertAccount(handle, entry.getKey());
						transactionDao.insertTransactions(handle, entry.getValue());
					}
				} catch (final Exception ex) {
					handle.rollback();
					throw new RuntimeException("Failed to insert records into database", ex);
				}
			}
			return this;
		}

		public TestHelper build() {
			return new TestHelper(this);
		}
	}

	public static class TransactionBuilder {
		private final Builder builder;
		private final Account account;

		private TransactionBuilder(Builder builder, Account account) {
			this.builder = builder;
			this.account = account;
		}

		public Builder withTransactions(int numTransactions) {
			for (int i = 0; i < numTransactions; i++) {
				final Transaction transaction = generateRandomTransaction(account);
				builder.accounts.get(account)
				                .add(transaction);
			}
			return builder;

		}
	}

	public static Account generateRandomAccount() {
		return Account.newBuilder(UUID.randomUUID().toString())
		              .setBalance(Money.zero(CurrencyUnit.CAD))
		              .addTransactions(new ArrayList<>())
		              .setType(AccountType.CHECKING)
		              .build();
	}

	public static Transaction generateRandomTransaction(Account account) {
		final Random random = new Random();
		final DateTime dateTimeUtc = DateTime.now(DateTimeZone.UTC).minusHours(random.nextInt(100));
		final CurrencyUnit currencyUnit = account.getBalance().getCurrencyUnit();
		final double amount = Math.round(random.nextDouble() * random.nextInt(1000));

		return new Transaction(account.getAccountNumber(), dateTimeUtc, 0, "Transaction "
		        + UUID.randomUUID().toString(), Money.of(currencyUnit, amount));
	}
}
