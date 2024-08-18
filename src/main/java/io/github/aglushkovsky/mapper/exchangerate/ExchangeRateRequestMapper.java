package io.github.aglushkovsky.mapper.exchangerate;

import io.github.aglushkovsky.dao.CurrencyDao;
import io.github.aglushkovsky.dto.exchangerate.ExchangeRateRequestDto;
import io.github.aglushkovsky.entity.Currency;
import io.github.aglushkovsky.entity.ExchangeRate;
import io.github.aglushkovsky.mapper.Mapper;
import io.github.aglushkovsky.util.ParseUtils;

import java.util.NoSuchElementException;

public class ExchangeRateRequestMapper implements Mapper<ExchangeRateRequestDto, ExchangeRate> {
    private static final String BASE_CURRENCY_NOT_FOUND = "Базовая валюта не найдена";
    private static final String TARGET_CURRENCY_NOT_FOUND = "Целевая валюта не найдена";
    private static final ExchangeRateRequestMapper INSTANCE = new ExchangeRateRequestMapper();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    @Override
    public ExchangeRate mapFrom(ExchangeRateRequestDto exchangeRateRequestParametersDto) {
        return new ExchangeRate(
                new Currency(exchangeRateRequestParametersDto.getBaseCurrencyCode()),
                new Currency(exchangeRateRequestParametersDto.getTargetCurrencyCode()),
                ParseUtils.parseBigDecimal(exchangeRateRequestParametersDto.getRate())
        );
    }

    public static ExchangeRateRequestMapper getInstance() {
        return INSTANCE;
    }
}
