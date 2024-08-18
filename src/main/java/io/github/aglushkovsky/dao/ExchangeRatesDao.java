package io.github.aglushkovsky.dao;

import io.github.aglushkovsky.entity.Currency;
import io.github.aglushkovsky.entity.ExchangeRate;
import io.github.aglushkovsky.exception.DaoException;
import io.github.aglushkovsky.mapper.error.ExceptionFromDBErrorFactory;
import io.github.aglushkovsky.mapper.error.ExchangeRatesDaoExceptionFromSQLiteErrorFactory;
import io.github.aglushkovsky.util.ConnectionProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRatesDao {
    private static final ExchangeRatesDao INSTANCE = new ExchangeRatesDao();
    private static final ExceptionFromDBErrorFactory exceptionFromDBErrorFactory = ExchangeRatesDaoExceptionFromSQLiteErrorFactory.getInstance();

    private static final String FIND_ALL_SQL = """
            SELECT er.id AS id, er.rate AS rate,
                    c1.id AS base_currency_id, c1.code AS base_currency_code, c1.full_name AS base_currency_full_name, c1.sign AS base_currency_sign,
                    c2.id AS target_currency_id, c2.code AS target_currency_code, c2.full_name AS target_currency_full_name, c2.sign AS target_currency_sign
            FROM exchange_rates AS er
            JOIN currencies AS c1 ON er.base_currency_id = c1.id
            JOIN currencies AS c2 ON er.target_currency_id = c2.id
            """;

    private static final String FIND_BY_CURRENCY_CODES_SQL = FIND_ALL_SQL + """
            WHERE c1.code = ? AND c2.code = ?
            """;

    private static final String FIND_BY_ID = FIND_ALL_SQL + """
            WHERE er.id = ?
            """;

    private static final String SAVE_SQL = """
            INSERT INTO exchange_rates(base_currency_id, target_currency_id, rate)
            VALUES
            ((SELECT id FROM currencies WHERE code = ?),
             (SELECT id FROM currencies WHERE code = ?),
             ?)
            """;

    private static final String UPDATE_SQL = """
            UPDATE exchange_rates SET rate = ? WHERE base_currency_id = ? AND target_currency_id = ?
            """;

    public ExchangeRate save(ExchangeRate exchangeRate) {
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(SAVE_SQL)) {
            statement.setString(1, exchangeRate.getBaseCurrency().getCode());
            statement.setString(2, exchangeRate.getTargetCurrency().getCode());
            statement.setBigDecimal(3, exchangeRate.getRate());

            statement.executeUpdate();
            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) {
                int generatedId = keys.getInt(1);
                exchangeRate = findById(generatedId).get();
            }
        } catch (SQLException e) {
            exceptionFromDBErrorFactory.createAndThrow(e);
        }
        return exchangeRate;
    }

    public boolean update(ExchangeRate exchangeRate) {
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            statement.setBigDecimal(1, exchangeRate.getRate());
            statement.setInt(2, exchangeRate.getBaseCurrency().getId());
            statement.setInt(3, exchangeRate.getTargetCurrency().getId());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public List<ExchangeRate> findAll() {
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = statement.executeQuery();
            List<ExchangeRate> exchangeRates = new ArrayList<>();
            while (resultSet.next()) {
                exchangeRates.add(buildExchangeRate(resultSet));
            }
            return exchangeRates;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public Optional<ExchangeRate> findById(int id) {
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(buildExchangeRate(resultSet));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public Optional<ExchangeRate> findByCurrencyCodes(String firstCurrencyCode, String secondCurrencyCode) {
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_CURRENCY_CODES_SQL)) {
            statement.setString(1, firstCurrencyCode);
            statement.setString(2, secondCurrencyCode);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(buildExchangeRate(resultSet));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private ExchangeRate buildExchangeRate(ResultSet resultSet) throws SQLException {
        return new ExchangeRate(
                resultSet.getInt("id"),
                new Currency(resultSet.getInt("base_currency_id"),
                        resultSet.getString("base_currency_code"),
                        resultSet.getString("base_currency_full_name"),
                        resultSet.getString("base_currency_sign")),
                new Currency(resultSet.getInt("target_currency_id"),
                        resultSet.getString("target_currency_code"),
                        resultSet.getString("target_currency_full_name"),
                        resultSet.getString("target_currency_sign")),
                resultSet.getBigDecimal("rate")
        );
    }

    public static ExchangeRatesDao getInstance() {
        return INSTANCE;
    }

    private ExchangeRatesDao() {
    }
}
