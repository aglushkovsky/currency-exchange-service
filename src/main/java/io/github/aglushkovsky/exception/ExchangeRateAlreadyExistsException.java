package io.github.aglushkovsky.exception;

import java.sql.SQLException;

public class ExchangeRateAlreadyExistsException extends RuntimeException {
    private static final String EXCHANGE_RATE_ALREADY_EXISTS = "Курс с указанными кодами валют уже существует";

    public ExchangeRateAlreadyExistsException(SQLException e) {
        super(EXCHANGE_RATE_ALREADY_EXISTS, e);
    }
}
