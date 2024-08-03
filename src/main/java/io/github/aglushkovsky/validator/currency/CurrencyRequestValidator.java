package io.github.aglushkovsky.validator.currency;

import io.github.aglushkovsky.dto.currency.CurrencyRequestDto;
import io.github.aglushkovsky.exception.ValidationException;
import io.github.aglushkovsky.validator.ValidationResult;
import io.github.aglushkovsky.validator.Validator;

import static io.github.aglushkovsky.error.message.ValidationErrorMessage.REQUIRED_PARAMETERS_ARE_MISSING;

public class CurrencyRequestValidator implements Validator<CurrencyRequestDto> {
    private static final CurrencyRequestValidator INSTANCE = new CurrencyRequestValidator();

    @Override
    public void validate(CurrencyRequestDto currency) {
        ValidationResult validationResult = new ValidationResult();

        String currencyCode = currency.getCode();
        if (currencyCode == null || currencyCode.isEmpty()) {
            validationResult.add("code");
        }
        String currencyName = currency.getName();
        if (currencyName == null || currencyName.isEmpty()) {
            validationResult.add("name");
        }
        String currencySign = currency.getSign();
        if (currencySign == null || currencySign.isEmpty()) {
            validationResult.add("sign");
        }

        if (!validationResult.isValid()) {
            throw new ValidationException(Validator.getErrorMessage(validationResult, REQUIRED_PARAMETERS_ARE_MISSING));
        }
    }

    public static CurrencyRequestValidator getInstance() {
        return INSTANCE;
    }

    private CurrencyRequestValidator() {
    }
}
