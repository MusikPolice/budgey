package ca.jonathanfritz.budgey.dao;

import java.math.BigDecimal;
import java.util.Iterator;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import ca.jonathanfritz.budgey.Transaction;

@RegisterMapper(Transaction.class)
public interface TransactionDAO {

	// TODO: account_number should be foreign-keyed on accounts?
	@SqlUpdate("CREATE TABLE `transaction` (`account_number` VARCHAR(255), `time_millis` BIGINT, `order` SMALLINT, `description` VARCHAR(255), `amount` DECIMAL);")
	void createTable();

	@SqlUpdate("INSERT INTO `transaction` (`account_number`, `time_millis`, `order`, `description`, `amount`) VALUES (:accountNumber, :timeMillis, :order, :description, :amount);")
	int insertTransaction(@Bind("accountNumber") String accountNumber, @Bind("timeMillis") long timeMillis, @Bind("order") int order, @Bind("description") String description, @Bind("amount") BigDecimal amount);

	@SqlQuery("SELECT * FROM `transaction` WHERE account_number = :accountNumber;")
	Iterator<Transaction> getTransactions(@Bind("accountNumber") String accountNumber);
}
