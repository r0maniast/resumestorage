package ru.webapp.sql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.webapp.exception.StorageException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SqlHelper {
    private static final Logger log = LoggerFactory.getLogger(SqlHelper.class);
    private final ConnectionFactory connectionFactory;

    public SqlHelper(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        log.info("SqlHelper initialized with ConnectionFactory: {}", connectionFactory.getClass().getSimpleName());
    }

    public void execute(String sql) {
        log.debug("Executing SQL statement: {}", sql);
        execute(sql, ps -> {
            boolean result = ps.execute();
            log.debug("Statement execute() returned: {}", result);
            return result;
        });
    }

    public <T> T execute(String request, SqlExecutor<T> executor) {
        log.debug("Preparing to execute query: {}", request);
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(request)) {
            log.trace("Connection obtained and statement prepared");
            T result = executor.execute(ps);
            log.trace("Query executed successfully");
            return result;
        } catch (SQLException e) {
            log.error("Error executing SQL: {}", request, e);
            throw ExceptionUtil.convertException(e);
        }
    }

    public <T> T transactionalExecute(SqlTransaction<T> executor) {
        log.debug("Starting transactional execution");
        try (Connection conn = connectionFactory.getConnection()) {
            try {
                conn.setAutoCommit(false);
                log.trace("Auto-commit disabled");
                T res = executor.execute(conn);
                conn.commit();
                log.debug("Transaction committed successfully");
                return res;
            } catch (SQLException e) {
                log.warn("Transaction failed, rolling back", e);
                conn.rollback();
                log.debug("Rollback completed");
                throw ExceptionUtil.convertException(e);
            }
        } catch (SQLException e) {
            log.error("Error in transactional execution", e);
            throw new StorageException(e);
        }
    }
}
