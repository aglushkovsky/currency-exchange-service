package io.github.aglushkovsky.servlet.currency;

import io.github.aglushkovsky.dao.CurrencyDao;
import io.github.aglushkovsky.dto.currency.CurrencyResponseDto;
import io.github.aglushkovsky.dto.currency.CurrencyRequestDto;
import io.github.aglushkovsky.entity.Currency;
import io.github.aglushkovsky.error.ResponseError;
import io.github.aglushkovsky.exception.DaoException;
import io.github.aglushkovsky.exception.ValidationException;
import io.github.aglushkovsky.mapper.currency.CurrencyMapper;
import io.github.aglushkovsky.mapper.currency.CurrencyRequestMapper;
import io.github.aglushkovsky.util.ResponseUtils;
import io.github.aglushkovsky.validator.currency.CurrencyRequestValidator;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;

import static io.github.aglushkovsky.exception.DaoException.CONSTRAINT_VIOLATION_ERROR_CODE;
import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();
    private final CurrencyRequestValidator validator = CurrencyRequestValidator.getInstance();
    private final CurrencyRequestMapper currencyRequestMapper = CurrencyRequestMapper.getInstance();
    private final CurrencyMapper currencyMapper = CurrencyMapper.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<CurrencyResponseDto> currencyResponseDtoList = getAllCurrencyResponseDto(resp);
        ResponseUtils.sendAllFromList(currencyResponseDtoList, resp);
    }

    private List<CurrencyResponseDto> getAllCurrencyResponseDto(HttpServletResponse resp) throws IOException {
        List<CurrencyResponseDto> currencyResponseDtoList = new ArrayList<>();
        try {
            List<Currency> currencies = currencyDao.findAll();
            currencyResponseDtoList = currencies.stream().map(currencyMapper::mapFrom).toList();
        } catch (DaoException e) {
            ResponseUtils.sendError(ResponseError.of(SC_INTERNAL_SERVER_ERROR, e.getMessage()), resp);
        }
        return currencyResponseDtoList;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CurrencyRequestDto currencyRequestParametersDto = new CurrencyRequestDto(
                req.getParameter("name"),
                req.getParameter("code"),
                req.getParameter("sign")
        );

        try {
            validator.validate(currencyRequestParametersDto);
        } catch (ValidationException e) {
            ResponseUtils.sendError(ResponseError.of(SC_BAD_REQUEST, e.getMessage()), resp);
            return;
        }

        Currency currency = currencyRequestMapper.mapFrom(currencyRequestParametersDto);
        try {
            Currency savedCurrency = currencyDao.save(currency);
            CurrencyResponseDto savedCurrencyResponseDto = currencyMapper.mapFrom(savedCurrency);
            ResponseUtils.sendObject(savedCurrencyResponseDto, SC_CREATED, resp);
        } catch (DaoException e) {
            ResponseError error;

            if (e.getCause().getErrorCode() == CONSTRAINT_VIOLATION_ERROR_CODE) {
                error = ResponseError.of(SC_CONFLICT, e.getMessage());
            } else {
                error = ResponseError.of(SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }

            ResponseUtils.sendError(error, resp);
        }
    }
}
