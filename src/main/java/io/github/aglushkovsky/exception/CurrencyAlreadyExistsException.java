package io.github.aglushkovsky.exception;

import java.sql.SQLException;

public class CurrencyAlreadyExistsException extends RuntimeException {
    private static final String CURRENCY_ALREADY_EXISTS = "Валюта с указанным кодом уже существует";

    public CurrencyAlreadyExistsException(SQLException cause) {
        super(CURRENCY_ALREADY_EXISTS, cause);
    }
}
