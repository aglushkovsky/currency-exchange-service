package io.github.aglushkovsky.mapper.error;

import io.github.aglushkovsky.exception.CurrencyAlreadyExistsException;
import io.github.aglushkovsky.exception.DaoException;
import org.sqlite.SQLiteErrorCode;

import java.sql.SQLException;

public class CurrencyDaoExceptionFromSQLiteErrorFactory implements ExceptionFromDBErrorFactory {
    private static final CurrencyDaoExceptionFromSQLiteErrorFactory INSTANCE = new CurrencyDaoExceptionFromSQLiteErrorFactory();

    @Override
    public void createAndThrow(int errorCode, SQLException e) {
        if (errorCode == SQLiteErrorCode.SQLITE_CONSTRAINT.code) {
            throw new CurrencyAlreadyExistsException(e);
        } else {
            throw new DaoException(e);
        }
    }

    public static CurrencyDaoExceptionFromSQLiteErrorFactory getInstance() {
        return INSTANCE;
    }

    private CurrencyDaoExceptionFromSQLiteErrorFactory() {
    }
}
