package io.github.aglushkovsky.validator;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
    private final List<String> missingRequiredParameterNames = new ArrayList<>();

    public void add(String missingParameterName) {
        missingRequiredParameterNames.add(missingParameterName);
    }

    public List<String> getMissingParameterNames() {
        return missingRequiredParameterNames;
    }

    public boolean isValid() {
        return missingRequiredParameterNames.isEmpty();
    }
}
