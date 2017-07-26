package ca.jonathanfritz.budgey.dao;

import java.math.BigDecimal;
import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import ca.jonathanfritz.budgey.Account;

@RegisterMapper(AccountResultSetMapper.class)
public interface AccountDAO {

	// TODO: account type should be foreign-keyed to a table of types?
	@SqlUpdate("CREATE TABLE IF NOT EXISTS account (account_number VARCHAR(255) PRIMARY KEY, type VARCHAR(20), balance DECIMAL, currency VARCHAR(3));")
	void createTable();

	/**
	 * Adds an account to the database
	 * @param accountNumber the unique account identifier
	 * @param type the type of account
	 * @param balance current balance of the account
	 * @param currency ISO-4217 3 character currency code, uppercase
	 * @return the number of rows inserted into the database
	 */
	@SqlUpdate("INSERT INTO account (account_number, type, balance, currency) VALUES (:accountNumber, :type, :balance, :currency);")
	int insertAccount(@Bind("accountNumber") String accountNumber, @Bind("type") String type, @Bind("balance") BigDecimal balance, @Bind("currency") String currency);

	@SqlQuery("SELECT * FROM account;")
	List<Account> getAccounts();
}
