package io.github.aglushkovsky.validator.exchangerate;

import io.github.aglushkovsky.dto.exchangerate.ExchangeRateRequestDto;
import io.github.aglushkovsky.exception.ValidationException;
import io.github.aglushkovsky.validator.ValidationResult;
import io.github.aglushkovsky.validator.Validator;

import static io.github.aglushkovsky.error.message.ValidationErrorMessage.BASE_AND_TARGET_CURRENCY_CODES_EQUALITY_NOT_ALLOWED;
import static io.github.aglushkovsky.error.message.ValidationErrorMessage.REQUIRED_PARAMETERS_ARE_MISSING;
import static io.github.aglushkovsky.util.ISO4217CheckUtils.ISO4217_VIOLATION_ERROR_MESSAGE;
import static io.github.aglushkovsky.util.ISO4217CheckUtils.isISO4217;

public class ExchangeRateRequestValidator implements Validator<ExchangeRateRequestDto> {
    private static final ExchangeRateRequestValidator INSTANCE = new ExchangeRateRequestValidator();

    @Override
    public void validate(ExchangeRateRequestDto exchangeRateRequestParametersDto) {
        ValidationResult validationResult = new ValidationResult();

        String baseCurrencyCodeParameter = exchangeRateRequestParametersDto.getBaseCurrencyCode();
        if (baseCurrencyCodeParameter == null || baseCurrencyCodeParameter.isEmpty()) {
            validationResult.add("baseCurrencyCode");
        }
        String targetCurrencyCodeParameter = exchangeRateRequestParametersDto.getTargetCurrencyCode();
        if (targetCurrencyCodeParameter == null || targetCurrencyCodeParameter.isEmpty()) {
            validationResult.add("targetCurrencyCode");
        }
        String rateParameter = exchangeRateRequestParametersDto.getRate();
        if (rateParameter == null || rateParameter.isEmpty()) {
            validationResult.add("rate");
        }

        if (!validationResult.isValid()) {
            throw new ValidationException(Validator.getErrorMessage(validationResult, REQUIRED_PARAMETERS_ARE_MISSING));
        }

        if (!isISO4217(baseCurrencyCodeParameter) || !isISO4217(targetCurrencyCodeParameter)) {
            throw new ValidationException(ISO4217_VIOLATION_ERROR_MESSAGE);
        }

        if (baseCurrencyCodeParameter.equals(targetCurrencyCodeParameter)) {
            throw new ValidationException(BASE_AND_TARGET_CURRENCY_CODES_EQUALITY_NOT_ALLOWED);
        }
    }

    public static ExchangeRateRequestValidator getInstance() {
        return INSTANCE;
    }

    private ExchangeRateRequestValidator() {
    }
}
