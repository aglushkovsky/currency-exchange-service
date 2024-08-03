package io.github.aglushkovsky.mapper.exchangerate;

import io.github.aglushkovsky.dto.currency.CurrencyResponseDto;
import io.github.aglushkovsky.dto.exchangerate.ExchangeRateResponseDto;
import io.github.aglushkovsky.entity.Currency;
import io.github.aglushkovsky.entity.ExchangeRate;
import io.github.aglushkovsky.mapper.Mapper;

public class ExchangeRateMapper implements Mapper<ExchangeRate, ExchangeRateResponseDto> {
    private static final ExchangeRateMapper INSTANCE = new ExchangeRateMapper();

    @Override
    public ExchangeRateResponseDto mapFrom(ExchangeRate exchangeRate) {
        Currency baseCurrency = exchangeRate.getBaseCurrency();
        Currency targetCurrency = exchangeRate.getTargetCurrency();
        return new ExchangeRateResponseDto(
                exchangeRate.getId(),
                buildCurrencyResponseDto(baseCurrency),
                buildCurrencyResponseDto(targetCurrency),
                exchangeRate.getRate()
        );
    }

    private CurrencyResponseDto buildCurrencyResponseDto(Currency currency) {
        return new CurrencyResponseDto(
                currency.getId(),
                currency.getFullName(),
                currency.getCode(),
                currency.getSign()
        );
    }

    public static ExchangeRateMapper getInstance() {
        return INSTANCE;
    }

    private ExchangeRateMapper() {
    }
}
