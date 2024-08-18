package io.github.aglushkovsky.servlet.exchangerate;

import io.github.aglushkovsky.dao.ExchangeRatesDao;
import io.github.aglushkovsky.dto.exchangerate.ExchangeRateResponseDto;
import io.github.aglushkovsky.dto.exchangerate.ExchangeRateRequestDto;
import io.github.aglushkovsky.entity.ExchangeRate;
import io.github.aglushkovsky.error.ResponseError;
import io.github.aglushkovsky.exception.*;
import io.github.aglushkovsky.mapper.exchangerate.ExchangeRateMapper;
import io.github.aglushkovsky.mapper.exchangerate.ExchangeRateRequestMapper;
import io.github.aglushkovsky.util.ResponseUtils;
import io.github.aglushkovsky.validator.exchangerate.ExchangeRateRequestValidator;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final ExchangeRatesDao exchangeRatesDao = ExchangeRatesDao.getInstance();
    private final ExchangeRateRequestValidator exchangeRateRequestValidator = ExchangeRateRequestValidator.getInstance();
    private final ExchangeRateRequestMapper exchangeRateRequestMapper = ExchangeRateRequestMapper.getInstance();
    private final ExchangeRateMapper exchangeRateMapper = ExchangeRateMapper.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<ExchangeRateResponseDto> exchangeRateDtoList = getAllExchangeRateDto(resp);
        ResponseUtils.sendAllFromList(exchangeRateDtoList, resp);
    }

    private List<ExchangeRateResponseDto> getAllExchangeRateDto(HttpServletResponse resp) throws IOException {
        List<ExchangeRateResponseDto> exchangeRateResponseDtoList = new ArrayList<>();
        try {
            List<ExchangeRate> exchangeRates = exchangeRatesDao.findAll();
            exchangeRateResponseDtoList = exchangeRates.stream().map(exchangeRateMapper::mapFrom).toList();
        } catch (DaoException e) {
            ResponseUtils.sendError(ResponseError.of(SC_INTERNAL_SERVER_ERROR, e.getMessage()), resp);
        }
        return exchangeRateResponseDtoList;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ExchangeRateRequestDto exchangeRateRequestDto = new ExchangeRateRequestDto(
                req.getParameter("baseCurrencyCode"),
                req.getParameter("targetCurrencyCode"),
                req.getParameter("rate")
        );

        try {
            exchangeRateRequestValidator.validate(exchangeRateRequestDto);
        } catch (ValidationException e) {
            ResponseUtils.sendError(ResponseError.of(SC_BAD_REQUEST, e.getMessage()), resp);
            return;
        }

        try {
            ExchangeRate exchangeRate = exchangeRateRequestMapper.mapFrom(exchangeRateRequestDto);
            ExchangeRate savedExchangeRate = exchangeRatesDao.save(exchangeRate);
            ExchangeRateResponseDto exchangeRateResponseDto = exchangeRateMapper.mapFrom(savedExchangeRate);

            ResponseUtils.sendObject(exchangeRateResponseDto, SC_CREATED, resp);
        } catch (ParseException e) {
            ResponseUtils.sendError(ResponseError.of(SC_BAD_REQUEST, e.getMessage()), resp);
        } catch (NoSuchElementException e) {
            ResponseUtils.sendError(ResponseError.of(SC_NOT_FOUND, e.getMessage()), resp);
        } catch (ExchangeRateAlreadyExistsException | CurrencyInExchangeRateNotFoundException e) {
            ResponseUtils.sendError(ResponseError.of(SC_CONFLICT, e.getMessage()), resp);
        } catch (DaoException e) {
            ResponseUtils.sendError(ResponseError.of(SC_INTERNAL_SERVER_ERROR, e.getMessage()), resp);
        }
    }
}
