package io.github.aglushkovsky.mapper.currency;

import io.github.aglushkovsky.dto.currency.CurrencyRequestDto;
import io.github.aglushkovsky.entity.Currency;
import io.github.aglushkovsky.mapper.Mapper;

public class CurrencyRequestMapper implements Mapper<CurrencyRequestDto, Currency> {
    private static final CurrencyRequestMapper INSTANCE = new CurrencyRequestMapper();

    @Override
    public Currency mapFrom(CurrencyRequestDto currencyRequestParametersDto) {
        return new Currency(currencyRequestParametersDto.getCode(),
                currencyRequestParametersDto.getName(),
                currencyRequestParametersDto.getSign());
    }

    public static CurrencyRequestMapper getInstance() {
        return INSTANCE;
    }

    private CurrencyRequestMapper() {
    }
}
