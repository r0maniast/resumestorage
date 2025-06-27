package ru.webapp.sql;

import org.postgresql.util.PSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.webapp.exception.ExistStorageException;
import ru.webapp.exception.StorageException;

import java.sql.SQLException;

public class ExceptionUtil {
    private static final Logger log = LoggerFactory.getLogger(ExceptionUtil.class);

    private ExceptionUtil() {
    }

    public static StorageException convertException(SQLException e) {
        log.debug("Converting SQLException with state {} and message: {}", e.getSQLState(), e.getMessage());
        if (e instanceof PSQLException) {
            String state = e.getSQLState();
            if ("23505".equals(state)) {
                log.warn("Unique constraint violation detected (SQLState={}), throwing ExistStorageException", state);
                return new ExistStorageException(null);
            }
        }
        log.error("General SQL exception, wrapping in StorageException", e);
        return new StorageException(e);
    }
}
