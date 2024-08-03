package io.github.aglushkovsky.util;

import io.github.aglushkovsky.exception.ParseException;
import jakarta.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.aglushkovsky.error.message.ExchangeRatesErrorMessage.EXCHANGE_RATE_PAIR_CODES_SHOULD_CONTAIN_SIX_CHARACTERS;
import static io.github.aglushkovsky.util.ISO4217CheckUtils.*;

public class ParseUtils {
    public static String parseCurrencyCode(String pathInfo) {
        String[] pathParameters = pathInfo.split("/");
        String currencyCode = pathParameters[1];

        if (!isISO4217(currencyCode)) {
            throw new ParseException(ISO4217_VIOLATION_ERROR_MESSAGE);
        }

        return currencyCode;
    }

    public static CurrencyCodesPair parseCurrencyPairCodes(String pathInfo) {
        String currencyCodes = pathInfo.split("/")[1];

        int numberOfCharactersInPairCurrencyCodes = NUMBER_OF_CHARACTERS_IN_CURRENCY_CODE * 2;
        if (currencyCodes.length() != numberOfCharactersInPairCurrencyCodes) {
            throw new ParseException(EXCHANGE_RATE_PAIR_CODES_SHOULD_CONTAIN_SIX_CHARACTERS);
        }

        String firstCode = currencyCodes.substring(0, 3);
        String secondCode = currencyCodes.substring(3, 6);
        if (!isISO4217(firstCode) || !isISO4217(secondCode)) {
            throw new ParseException(ISO4217_VIOLATION_ERROR_MESSAGE);
        }

        return new CurrencyCodesPair(firstCode, secondCode);
    }

    public static BigDecimal parseBigDecimal(String parameter) {
        try {
            return BigDecimal.valueOf(Double.parseDouble(parameter));
        } catch (NumberFormatException e) {
            throw new ParseException("Ошибка при получении числа из параметра " + parameter, e);
        }
    }

    public static Map<String, List<String>> parseRequestParameters(HttpServletRequest req) throws IOException {
        Map<String, List<String>> parametersMap = new HashMap<>();
        String requestBody = getRequestBody(req);

        String[] parameters = requestBody.split("&");
        for (String parameter : parameters) {
            String[] keyValuePair = parameter.split("=", 2);
            if (keyValuePair.length == 2) {
                String key = keyValuePair[0];
                String value = keyValuePair[1];
                if (parametersMap.containsKey(key)) {
                    List<String> values = parametersMap.get(key);
                    values.add(value);
                } else {
                    parametersMap.put(key, new ArrayList<>(List.of(value)));
                }
            }
        }

        return parametersMap;
    }

    private static String getRequestBody(HttpServletRequest req) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reqReader = req.getReader()) {
            String line;
            while ((line = reqReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        }
        return stringBuilder.toString();
    }

    private ParseUtils() {
    }
}
