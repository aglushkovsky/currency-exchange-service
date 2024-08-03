package io.github.aglushkovsky.service;

import io.github.aglushkovsky.dao.ExchangeRatesDao;
import io.github.aglushkovsky.dto.currency.CurrencyResponseDto;
import io.github.aglushkovsky.dto.exchange.ExchangeRequestDto;
import io.github.aglushkovsky.dto.service.ExchangeServiceResponseDto;
import io.github.aglushkovsky.entity.ExchangeRate;
import io.github.aglushkovsky.exception.ExchangeRateNotFoundException;
import io.github.aglushkovsky.exception.ExchangeServiceException;
import io.github.aglushkovsky.exception.ParseException;
import io.github.aglushkovsky.mapper.currency.CurrencyMapper;
import io.github.aglushkovsky.util.ParseUtils;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Optional;

import static java.math.RoundingMode.HALF_EVEN;

public class ExchangeService {
    private static final ExchangeService INSTANCE = new ExchangeService();
    private static final String USD_CURRENCY_CODE = "USD";
    private final ExchangeRatesDao exchangeRatesDao = ExchangeRatesDao.getInstance();
    private final CurrencyMapper currencyMapper = CurrencyMapper.getInstance();

    public ExchangeServiceResponseDto getExchangeRate(ExchangeRequestDto requestParameters) {
        String from = requestParameters.getFrom();
        String to = requestParameters.getTo();

        ExchangeRate exchangeRate = findExchangeRate(from, to)
                .orElseThrow(ExchangeRateNotFoundException::new);

        CurrencyResponseDto baseCurrencyResponseDto = currencyMapper.mapFrom(exchangeRate.getBaseCurrency());
        CurrencyResponseDto targetCurrencyResponseDto = currencyMapper.mapFrom(exchangeRate.getTargetCurrency());

        BigDecimal amount;
        try {
            amount = ParseUtils.parseBigDecimal(requestParameters.getAmount());
        } catch (ParseException e) {
            throw new ExchangeServiceException(e);
        }

        BigDecimal convertedAmount = amount.multiply(exchangeRate.getRate(), MathContext.DECIMAL128)
                .setScale(2, HALF_EVEN);

        return new ExchangeServiceResponseDto(
                baseCurrencyResponseDto,
                targetCurrencyResponseDto,
                exchangeRate.getRate(),
                amount,
                convertedAmount);
    }

    private Optional<ExchangeRate> findExchangeRate(String from, String to) {
        Optional<ExchangeRate> exchangeRateOptional = findDirectRate(from, to);
        if (exchangeRateOptional.isEmpty()) {
            exchangeRateOptional = findReverseRate(from, to);
        }
        if (exchangeRateOptional.isEmpty()) {
            exchangeRateOptional = findCrossRate(from, to);
        }
        return exchangeRateOptional;
    }

    private Optional<ExchangeRate> findCrossRate(String baseCode, String targetCode) {
        Optional<ExchangeRate> usdFromRate = exchangeRatesDao.findByCurrencyCodes(USD_CURRENCY_CODE, baseCode);
        Optional<ExchangeRate> usdToRate = exchangeRatesDao.findByCurrencyCodes(USD_CURRENCY_CODE, targetCode);

        if (usdFromRate.isEmpty() || usdToRate.isEmpty()) {
            return Optional.empty();
        }

        ExchangeRate baseExchangeRate = usdFromRate.get();
        ExchangeRate targetExchangeRate = usdToRate.get();

        BigDecimal resultRate = calculateCrossRate(targetExchangeRate, baseExchangeRate);
        ExchangeRate exchangeRate = new ExchangeRate(
                baseExchangeRate.getTargetCurrency(),
                targetExchangeRate.getTargetCurrency(),
                resultRate
        );

        return Optional.of(exchangeRate);
    }

    private BigDecimal calculateCrossRate(ExchangeRate targetExchangeRate, ExchangeRate baseExchangeRate) {
        return targetExchangeRate.getRate().divide(baseExchangeRate.getRate(), 6, RoundingMode.HALF_DOWN);
    }

    private Optional<ExchangeRate> findReverseRate(String from, String to) {
        Optional<ExchangeRate> reverseRateOptional = exchangeRatesDao.findByCurrencyCodes(to, from);

        if (reverseRateOptional.isEmpty()) {
            return Optional.empty();
        }

        ExchangeRate reverseRate = reverseRateOptional.get();
        BigDecimal resultRate = calculateReverseRate(reverseRate.getRate());
        ExchangeRate exchangeRate = new ExchangeRate(
                reverseRate.getBaseCurrency(),
                reverseRate.getTargetCurrency(),
                resultRate
        );

        return Optional.of(exchangeRate);
    }

    private BigDecimal calculateReverseRate(BigDecimal rate) {
        return BigDecimal.ONE.divide(rate, 4, RoundingMode.HALF_DOWN);
    }

    private Optional<ExchangeRate> findDirectRate(String from, String to) {
        return exchangeRatesDao.findByCurrencyCodes(from, to);
    }

    public static ExchangeService getInstance() {
        return INSTANCE;
    }
    
    private ExchangeService() {
    }
}
