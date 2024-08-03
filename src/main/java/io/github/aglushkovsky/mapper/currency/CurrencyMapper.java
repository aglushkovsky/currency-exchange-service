package io.github.aglushkovsky.mapper.currency;

import io.github.aglushkovsky.dto.currency.CurrencyResponseDto;
import io.github.aglushkovsky.entity.Currency;
import io.github.aglushkovsky.mapper.Mapper;

public class CurrencyMapper implements Mapper<Currency, CurrencyResponseDto> {
    private static final CurrencyMapper INSTANCE = new CurrencyMapper();

    @Override
    public CurrencyResponseDto mapFrom(Currency currency) {
        return new CurrencyResponseDto(
                currency.getId(),
                currency.getFullName(),
                currency.getCode(),
                currency.getSign()
        );
    }

    public static CurrencyMapper getInstance() {
        return INSTANCE;
    }

    private CurrencyMapper() {
    }
}
