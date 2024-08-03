package io.github.aglushkovsky.exception;

public class ExchangeServiceException extends RuntimeException {
    public ExchangeServiceException(String message) {
        super(message);
    }

    public ExchangeServiceException(Throwable cause) {
        super(cause);
    }
}
