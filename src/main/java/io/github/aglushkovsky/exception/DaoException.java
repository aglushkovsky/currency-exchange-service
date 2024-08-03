package io.github.aglushkovsky.exception;

import java.sql.SQLException;

public class DaoException extends RuntimeException {
    public static final int CONSTRAINT_VIOLATION_ERROR_CODE = 19;
    public static final String INTERNAL_SERVER_ERROR = "Внутренняя ошибка сервера";

    public DaoException(SQLException e) {
        super(INTERNAL_SERVER_ERROR, e);
    }

    public DaoException(String message, SQLException cause) {
        super(message, cause);
    }

    public DaoException(String message) {
        super(message);
    }

    @Override
    public synchronized SQLException getCause() {
        return (SQLException) super.getCause();
    }
}