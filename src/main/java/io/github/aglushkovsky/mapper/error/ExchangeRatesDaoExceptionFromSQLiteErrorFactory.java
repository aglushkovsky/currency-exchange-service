package io.github.aglushkovsky.mapper.error;

import io.github.aglushkovsky.exception.CurrencyInExchangeRateNotFoundException;
import io.github.aglushkovsky.exception.DaoException;
import io.github.aglushkovsky.exception.ExchangeRateAlreadyExistsException;
import org.sqlite.SQLiteException;

import java.sql.SQLException;

import static org.sqlite.SQLiteErrorCode.*;

public class ExchangeRatesDaoExceptionFromSQLiteErrorFactory implements ExceptionFromDBErrorFactory {
    private static final ExchangeRatesDaoExceptionFromSQLiteErrorFactory INSTANCE = new ExchangeRatesDaoExceptionFromSQLiteErrorFactory();
    
    @Override
    public void createAndThrow(SQLException e) {
        SQLiteException sqLiteException = (SQLiteException) e;
        int errorCode = sqLiteException.getResultCode().code;

        if (errorCode == SQLITE_CONSTRAINT_UNIQUE.code) {
            throw new ExchangeRateAlreadyExistsException(e);
        } else if (errorCode == SQLITE_CONSTRAINT_NOTNULL.code) {
            throw new CurrencyInExchangeRateNotFoundException(e);
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
