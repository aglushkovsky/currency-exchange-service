package io.github.aglushkovsky.exception;

import java.sql.SQLException;

public class CurrencyInExchangeRateNotFoundException extends RuntimeException {
    private static final String CURRENCY_IN_EXCHANGE_RATE_NOT_FOUND = "Одна или обе валюты в обменном курсе не найдены.";

    public CurrencyInExchangeRateNotFoundException(SQLException e) {
        super(CURRENCY_IN_EXCHANGE_RATE_NOT_FOUND, e);
    }
}
