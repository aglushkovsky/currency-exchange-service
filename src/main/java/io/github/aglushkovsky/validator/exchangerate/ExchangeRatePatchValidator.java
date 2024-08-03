package io.github.aglushkovsky.validator.exchangerate;

import io.github.aglushkovsky.dto.exchangerate.ExchangeRatePatchDto;
import io.github.aglushkovsky.exception.ValidationException;
import io.github.aglushkovsky.validator.ValidationResult;
import io.github.aglushkovsky.validator.Validator;

import static io.github.aglushkovsky.error.message.ValidationErrorMessage.REQUIRED_PARAMETERS_ARE_MISSING;

public class ExchangeRatePatchValidator implements Validator<ExchangeRatePatchDto> {
    private static final ExchangeRatePatchValidator INSTANCE = new ExchangeRatePatchValidator();
    
    @Override
    public void validate(ExchangeRatePatchDto exchangeRatePatchParametersDto) {
        ValidationResult validationResult = new ValidationResult();

        String rateParameter = exchangeRatePatchParametersDto.getRate();
        if (rateParameter == null || rateParameter.isEmpty()) {
            validationResult.add("rate");
        }

        if (!validationResult.isValid()) {
            throw new ValidationException(Validator.getErrorMessage(validationResult, REQUIRED_PARAMETERS_ARE_MISSING));
        }
    }

    public static ExchangeRatePatchValidator getInstance() {
        return INSTANCE;
    }

    private ExchangeRatePatchValidator() {
    }
}
