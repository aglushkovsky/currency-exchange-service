package io.github.aglushkovsky.validator;

import io.github.aglushkovsky.exception.ValidationException;

import java.util.List;
import java.util.StringJoiner;

public interface Validator<T> {
    void validate(T t) throws ValidationException;

    static String getErrorMessage(ValidationResult validationResult, String message) {
        StringJoiner stringJoiner = new StringJoiner(", ",
                message + ": ", "");
        List<String> missingParameterNames = validationResult.getMissingParameterNames();
        for (String parameterName : missingParameterNames) {
            stringJoiner.add(parameterName);
        }
        return stringJoiner.toString();
    }
}
