package io.github.aglushkovsky.servlet.exchangerate;

import io.github.aglushkovsky.dao.ExchangeRatesDao;
import io.github.aglushkovsky.dto.exchangerate.ExchangeRatePatchDto;
import io.github.aglushkovsky.dto.exchangerate.ExchangeRateResponseDto;
import io.github.aglushkovsky.entity.ExchangeRate;
import io.github.aglushkovsky.error.ResponseError;
import io.github.aglushkovsky.exception.DaoException;
import io.github.aglushkovsky.exception.ExchangeRateNotFoundException;
import io.github.aglushkovsky.exception.ParseException;
import io.github.aglushkovsky.exception.ValidationException;
import io.github.aglushkovsky.mapper.exchangerate.ExchangeRateMapper;
import io.github.aglushkovsky.util.CurrencyCodesPair;
import io.github.aglushkovsky.util.ParseUtils;
import io.github.aglushkovsky.util.ResponseUtils;
import io.github.aglushkovsky.validator.exchangerate.ExchangeRatePatchValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static io.github.aglushkovsky.error.message.ExchangeRatesErrorMessage.*;
import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ExchangeRatesDao exchangeRatesDao = ExchangeRatesDao.getInstance();
    private final ExchangeRateMapper exchangeRateMapper = ExchangeRateMapper.getInstance();
    private final ExchangeRatePatchValidator exchangeRatePatchValidator = ExchangeRatePatchValidator.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (!isPathParameterSpecified(pathInfo)) {
            ResponseUtils.sendError(ResponseError.of(SC_BAD_REQUEST, EXCHANGE_RATE_PAIR_CODES_NOT_SPECIFIED), resp);
            return;
        }

        try {
            CurrencyCodesPair currencyPairCodes = ParseUtils.parseCurrencyPairCodes(pathInfo);
            ExchangeRateResponseDto exchangeRateResponseDto = getExchangeRateDtoByCodes(currencyPairCodes, resp);
            ResponseUtils.sendObject(exchangeRateResponseDto, resp);
        } catch (ParseException e) {
            ResponseUtils.sendError(ResponseError.of(SC_BAD_REQUEST, e.getMessage()), resp);
        } catch (ExchangeRateNotFoundException e) {
            ResponseUtils.sendError(ResponseError.of(SC_NOT_FOUND, e.getMessage()), resp);
        }
    }

    private boolean isPathParameterSpecified(String pathInfo) {
        return pathInfo != null && !pathInfo.equals("/");
    }

    private ExchangeRateResponseDto getExchangeRateDtoByCodes(CurrencyCodesPair currencyPairCodes, HttpServletResponse resp)
            throws IOException {
        String firstCode = currencyPairCodes.firstCode();
        String secondCode = currencyPairCodes.secondCode();
        ExchangeRateResponseDto exchangeRateResponseDto = null;
        try {
            ExchangeRate exchangeRate = exchangeRatesDao.findByCurrencyCodes(firstCode, secondCode)
                    .orElseThrow(ExchangeRateNotFoundException::new);
            exchangeRateResponseDto = exchangeRateMapper.mapFrom(exchangeRate);
        } catch (DaoException e) {
            ResponseUtils.sendError(ResponseError.of(SC_INTERNAL_SERVER_ERROR, e.getMessage()), resp);
        }
        return exchangeRateResponseDto;
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (!isPathParameterSpecified(pathInfo)) {
            ResponseUtils.sendError(ResponseError.of(SC_BAD_REQUEST, EXCHANGE_RATE_PAIR_CODES_NOT_SPECIFIED), resp);
            return;
        }

        Map<String, List<String>> parameters = ParseUtils.parseRequestParameters(req);
        ExchangeRatePatchDto patchParametersDto = new ExchangeRatePatchDto(parameters.get("rate").get(0));
        try {
            exchangeRatePatchValidator.validate(patchParametersDto);
        } catch (ValidationException e) {
            ResponseUtils.sendError(ResponseError.of(SC_BAD_REQUEST, e.getMessage()), resp);
            return;
        }

        try {
            CurrencyCodesPair currencyCodesPair = ParseUtils.parseCurrencyPairCodes(pathInfo);
            BigDecimal rate = ParseUtils.parseBigDecimal(patchParametersDto.getRate());
            ExchangeRate exchangeRate = getUpdatedExchangeRate(currencyCodesPair, rate);
            exchangeRatesDao.update(exchangeRate);
            ExchangeRateResponseDto exchangeRateResponseDto = exchangeRateMapper.mapFrom(exchangeRate);
            ResponseUtils.sendObject(exchangeRateResponseDto, resp);
        } catch (ParseException e) {
            ResponseUtils.sendError(ResponseError.of(SC_BAD_REQUEST, e.getMessage()), resp);
        } catch (ExchangeRateNotFoundException e) {
            ResponseUtils.sendError(ResponseError.of(SC_NOT_FOUND, e.getMessage()), resp);
        } catch (DaoException e) {
            ResponseUtils.sendError(ResponseError.of(SC_INTERNAL_SERVER_ERROR, e.getMessage()), resp);
        }
    }

    private ExchangeRate getUpdatedExchangeRate(CurrencyCodesPair currencyCodesPair, BigDecimal rate) {
        String firstCode = currencyCodesPair.firstCode();
        String secondCode = currencyCodesPair.secondCode();
        ExchangeRate exchangeRate = exchangeRatesDao.findByCurrencyCodes(firstCode, secondCode)
                .orElseThrow(ExchangeRateNotFoundException::new);
        exchangeRate.setRate(rate);
        return exchangeRate;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        if (method.equals("PATCH")) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }
}
