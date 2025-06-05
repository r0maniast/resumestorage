package ru.webapp.sql;

import org.postgresql.util.PSQLException;
import ru.webapp.exception.ExistStorageException;
import ru.webapp.exception.StorageException;

import java.sql.SQLException;

public class ExceptionUtil {
    private ExceptionUtil() {

    }

    public static StorageException convertException(SQLException e) {
        if (e instanceof PSQLException) {
            if ("23505".equals(e.getSQLState()))
                return new ExistStorageException(null);
        }
        return new StorageException(e);
    }
}
