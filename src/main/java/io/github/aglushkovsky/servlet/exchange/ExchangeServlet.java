package io.github.aglushkovsky.servlet.exchange;

import io.github.aglushkovsky.dto.exchange.ExchangeRequestDto;
import io.github.aglushkovsky.dto.service.ExchangeServiceResponseDto;
import io.github.aglushkovsky.error.ResponseError;
import io.github.aglushkovsky.exception.ExchangeRateNotFoundException;
import io.github.aglushkovsky.exception.ExchangeServiceException;
import io.github.aglushkovsky.exception.ValidationException;
import io.github.aglushkovsky.service.ExchangeService;
import io.github.aglushkovsky.util.ResponseUtils;
import io.github.aglushkovsky.validator.exchange.ExchangeRequestValidator;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private final ExchangeRequestValidator exchangeRequestValidator = ExchangeRequestValidator.getInstance();
    private final ExchangeService exchangeService = ExchangeService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ExchangeRequestDto exchangeRequestParametersDto = new ExchangeRequestDto(
                req.getParameter("from"),
                req.getParameter("to"),
                req.getParameter("amount")
        );

        try {
            exchangeRequestValidator.validate(exchangeRequestParametersDto);
            ExchangeServiceResponseDto exchangeRate = exchangeService.getExchangeRate(exchangeRequestParametersDto);
            ResponseUtils.sendObject(exchangeRate, resp);
        } catch (ValidationException e) {
            ResponseUtils.sendError(ResponseError.of(SC_BAD_REQUEST, e.getMessage()), resp);
        } catch (ExchangeRateNotFoundException e) {
            ResponseUtils.sendError(ResponseError.of(SC_NOT_FOUND, e.getMessage()), resp);
        } catch (ExchangeServiceException e) {
            ResponseUtils.sendError(ResponseError.of(SC_BAD_REQUEST, e.getCause().getMessage()), resp);
        }
    }
}
