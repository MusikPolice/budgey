package ca.jonathanfritz.budgey.dao;

import java.math.BigDecimal;
import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import ca.jonathanfritz.budgey.Transaction;

@RegisterMapper(TransactionResultSetMapper.class)
public interface TransactionDAO {

	// TODO: account_number should be foreign-keyed on accounts?
	@SqlUpdate("CREATE TABLE IF NOT EXISTS `transaction` (`account_number` VARCHAR(255), `time_millis` BIGINT, `order` SMALLINT, `description` VARCHAR(255), `amount` DECIMAL, currency VARCHAR(3));")
	void createTable();

	@SqlUpdate("INSERT INTO `transaction` (`account_number`, `time_millis`, `order`, `description`, `amount`, `currency`) VALUES (:accountNumber, :timeMillis, :order, :description, :amount, :currency);")
	int insertTransaction(@Bind("accountNumber") String accountNumber, @Bind("timeMillis") long timeMillis, @Bind("order") int order, @Bind("description") String description, @Bind("amount") BigDecimal amount, @Bind("currency") String currency);

	@SqlQuery("SELECT * FROM `transaction` WHERE account_number = :accountNumber ORDER BY time_millis DESC;")
	List<Transaction> getTransactions(@Bind("accountNumber") String accountNumber);
}
