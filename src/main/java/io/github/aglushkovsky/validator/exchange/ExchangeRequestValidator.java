package io.github.aglushkovsky.validator.exchange;

import io.github.aglushkovsky.dto.exchange.ExchangeRequestDto;
import io.github.aglushkovsky.exception.ValidationException;
import io.github.aglushkovsky.validator.ValidationResult;
import io.github.aglushkovsky.validator.Validator;

import static io.github.aglushkovsky.error.message.ValidationErrorMessage.REQUIRED_PARAMETERS_ARE_MISSING;

public class ExchangeRequestValidator implements Validator<ExchangeRequestDto> {
    private static final ExchangeRequestValidator INSTANCE = new ExchangeRequestValidator();

    @Override
    public void validate(ExchangeRequestDto exchangeRequestParametersDto) {
        ValidationResult validationResult = new ValidationResult();

        String fromParameter = exchangeRequestParametersDto.getFrom();
        if (fromParameter == null || fromParameter.isEmpty()) {
            validationResult.add("from");
        }
        String toParameter = exchangeRequestParametersDto.getTo();
        if (toParameter == null || toParameter.isEmpty()) {
            validationResult.add("to");
        }
        String amountParameter = exchangeRequestParametersDto.getAmount();
        if (amountParameter == null || amountParameter.isEmpty()) {
            validationResult.add("amount");
        }

        if (!validationResult.isValid()) {
            throw new ValidationException(Validator.getErrorMessage(validationResult, REQUIRED_PARAMETERS_ARE_MISSING));
        }
    }

    public static ExchangeRequestValidator getInstance() {
        return INSTANCE;
    }

    private ExchangeRequestValidator() {
    }
}
