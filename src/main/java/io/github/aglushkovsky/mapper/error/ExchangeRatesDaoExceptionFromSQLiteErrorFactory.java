package io.github.aglushkovsky.mapper.error;

import io.github.aglushkovsky.exception.DaoException;
import io.github.aglushkovsky.exception.ExchangeRateAlreadyExistsException;
import org.sqlite.SQLiteErrorCode;

import java.sql.SQLException;

public class ExchangeRatesDaoExceptionFromSQLiteErrorFactory implements ExceptionFromDBErrorFactory {
    private static final ExchangeRatesDaoExceptionFromSQLiteErrorFactory INSTANCE = new ExchangeRatesDaoExceptionFromSQLiteErrorFactory();
    
    @Override
    public void createAndThrow(int errorCode, SQLException e) {
        if (errorCode == SQLiteErrorCode.SQLITE_CONSTRAINT.code) {
            throw new ExchangeRateAlreadyExistsException(e);
        } else {
            throw new DaoException(e);
        }
    }

    public static ExchangeRatesDaoExceptionFromSQLiteErrorFactory getInstance() {
        return INSTANCE;
    }
    
    private ExchangeRatesDaoExceptionFromSQLiteErrorFactory() {
    }
}
