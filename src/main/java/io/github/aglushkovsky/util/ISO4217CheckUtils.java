package io.github.aglushkovsky.util;

public class ISO4217CheckUtils {
    public static final String ISO4217_VIOLATION_ERROR_MESSAGE = "Код валюты должен быть записан в формате ISO 4217";
    public static final int NUMBER_OF_CHARACTERS_IN_CURRENCY_CODE = 3;

    public static boolean isISO4217(String currencyCode) {
        if (currencyCode.length() != NUMBER_OF_CHARACTERS_IN_CURRENCY_CODE) {
            return false;
        }

        return currencyCode.chars().allMatch(Character::isLetter);
    }
}
