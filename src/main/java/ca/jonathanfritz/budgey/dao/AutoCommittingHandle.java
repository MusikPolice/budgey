package ca.jonathanfritz.budgey.dao;

import java.io.Closeable;
import java.util.Map;

import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoCommittingHandle implements Closeable {

	private final Handle handle;

	private static final Logger log = LoggerFactory.getLogger(AutoCommittingHandle.class);

	public AutoCommittingHandle(DBI dbi) {
		log.debug("Transaction started");
		handle = dbi.open()
		        .begin();
	}

	public Query<Map<String, Object>> createQuery(String sql) {
		if (!handle.isInTransaction()) {
			throw new IllegalStateException("Transaction has already been closed");
		}
		log.debug("Creating query {}", sql);
		return handle.createQuery(sql);
	}

	public Update createStatement(String sql) {
		if (!handle.isInTransaction()) {
			throw new IllegalStateException("Transaction has already been closed");
		}
		log.debug("Creating statement {}", sql);
		return handle.createStatement(sql);
	}

	public void rollback() {
		if (!handle.isInTransaction()) {
			throw new IllegalStateException("Transaction has already been closed");
		}

		handle.rollback();
		log.debug("Transaction rolled back");
	}

	@Override
	public void close() {
		try {
			if (handle == null) {
				log.warn("Handle is null");
				return;
			}
			if (handle.isInTransaction()) {
				handle.commit();
				log.debug("Transaction committed");
			}
			handle.close();
			log.debug("Transaction closed");
		} catch (final Exception ex) {
			log.error("Failed to close database handle", ex);
		}
	}
}
