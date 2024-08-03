package io.github.aglushkovsky.servlet.currency;

import io.github.aglushkovsky.dao.CurrencyDao;
import io.github.aglushkovsky.dto.currency.CurrencyResponseDto;
import io.github.aglushkovsky.entity.Currency;
import io.github.aglushkovsky.error.ResponseError;
import io.github.aglushkovsky.exception.CurrencyNotFoundException;
import io.github.aglushkovsky.exception.DaoException;
import io.github.aglushkovsky.exception.ParseException;
import io.github.aglushkovsky.mapper.currency.CurrencyMapper;
import io.github.aglushkovsky.util.ParseUtils;
import io.github.aglushkovsky.util.ResponseUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static io.github.aglushkovsky.error.message.CurrencyErrorMessage.*;
import static jakarta.servlet.http.HttpServletResponse.*;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();
    private final CurrencyMapper currencyMapper = CurrencyMapper.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (!isCurrencyCodeSpecified(pathInfo)) {
            ResponseUtils.sendError(ResponseError.of(SC_BAD_REQUEST, CURRENCY_CODE_NOT_SPECIFIED), resp);
            return;
        }

        try {
            String currencyCode = ParseUtils.parseCurrencyCode(pathInfo);
            CurrencyResponseDto currencyDtoByCode = getCurrencyDtoByCode(currencyCode, resp);
            ResponseUtils.sendObject(currencyDtoByCode, resp);
        } catch (ParseException e) {
            ResponseUtils.sendError(ResponseError.of(SC_BAD_REQUEST, e.getMessage()), resp);
        } catch (CurrencyNotFoundException e) {
            ResponseUtils.sendError(ResponseError.of(SC_NOT_FOUND, e.getMessage()), resp);
        }
    }

    private boolean isCurrencyCodeSpecified(String pathInfo) {
        return pathInfo != null && !pathInfo.equals("/");
    }

    private CurrencyResponseDto getCurrencyDtoByCode(String currencyCode, HttpServletResponse resp) throws IOException {
        CurrencyResponseDto currencyResponseDto = null;
        try {
            Currency currency = currencyDao.findByCode(currencyCode).orElseThrow(CurrencyNotFoundException::new);
            currencyResponseDto = currencyMapper.mapFrom(currency);
        } catch (DaoException e) {
            ResponseUtils.sendError(ResponseError.of(SC_INTERNAL_SERVER_ERROR, e.getMessage()), resp);
        }
        return currencyResponseDto;
    }
}
