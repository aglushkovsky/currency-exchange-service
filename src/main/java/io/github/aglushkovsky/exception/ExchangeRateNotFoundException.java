package io.github.aglushkovsky.exception;

import static io.github.aglushkovsky.error.message.ExchangeRatesErrorMessage.EXCHANGE_RATE_NOT_FOUND;

public class ExchangeRateNotFoundException extends RuntimeException {
    public ExchangeRateNotFoundException() {
        super(EXCHANGE_RATE_NOT_FOUND);
    }
}
