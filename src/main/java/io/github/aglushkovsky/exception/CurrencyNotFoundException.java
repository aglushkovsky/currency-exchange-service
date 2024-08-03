package io.github.aglushkovsky.exception;

import static io.github.aglushkovsky.error.message.CurrencyErrorMessage.CURRENCY_NOT_FOUND;

public class CurrencyNotFoundException extends RuntimeException {
    public CurrencyNotFoundException() {
        super(CURRENCY_NOT_FOUND);
    }
}
