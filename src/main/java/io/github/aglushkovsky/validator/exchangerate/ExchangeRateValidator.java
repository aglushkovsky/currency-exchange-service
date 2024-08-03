package io.github.aglushkovsky.validator.exchangerate;

import io.github.aglushkovsky.entity.ExchangeRate;
import io.github.aglushkovsky.exception.ValidationException;
import io.github.aglushkovsky.validator.ValidationResult;
import io.github.aglushkovsky.validator.Validator;

public class ExchangeRateValidator implements Validator<ExchangeRate> {
    private static final ExchangeRateValidator INSTANCE = new ExchangeRateValidator();

    @Override
    public void validate(ExchangeRate exchangeRate) throws ValidationException {
        ValidationResult validationResult = new ValidationResult();

        if (exchangeRate.getBaseCurrency() == null) {
            validationResult.add("");
        }
    }

    private ExchangeRateValidator() {
    }
}
