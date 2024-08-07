package io.github.aglushkovsky.dao;

import io.github.aglushkovsky.entity.Currency;
import io.github.aglushkovsky.exception.DaoException;
import io.github.aglushkovsky.mapper.error.CurrencyDaoExceptionFromSQLiteErrorFactory;
import io.github.aglushkovsky.mapper.error.ExceptionFromDBErrorFactory;
import io.github.aglushkovsky.util.ConnectionProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.sql.Statement.*;

public class CurrencyDao {
    private static final CurrencyDao INSTANCE = new CurrencyDao();
    private final ExceptionFromDBErrorFactory exceptionFromDBErrorFactory = CurrencyDaoExceptionFromSQLiteErrorFactory.getInstance();

    private static final String FIND_ALL_SQL = """
            SELECT id, code, full_name, sign
            FROM currencies
            """;

    private static final String FIND_ALL_BY_CODE_SQL = FIND_ALL_SQL + """
            WHERE code = ?
            """;

    private static final String SAVE_SQL = """
            INSERT INTO currencies(code, full_name, sign) VALUES
            (?, ?, ?)
            """;

    public List<Currency> findAll() {
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = statement.executeQuery();
            List<Currency> currencies = new ArrayList<>();
            while (resultSet.next()) {
                currencies.add(buildCurrency(resultSet));
            }
            return currencies;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public Optional<Currency> findByCode(String code) {
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_BY_CODE_SQL)) {
            statement.setString(1, code);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return Optional.of(buildCurrency(resultSet));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public Currency save(Currency currency) {
        try (Connection connection = ConnectionProvider.getConnection();
             PreparedStatement statement = connection.prepareStatement(SAVE_SQL, RETURN_GENERATED_KEYS)) {
            statement.setString(1, currency.getCode());
            statement.setString(2, currency.getFullName());
            statement.setString(3, currency.getSign());

            statement.executeUpdate();
            ResultSet key = statement.getGeneratedKeys();
            if (key.next()) {
                currency.setId(key.getInt(1));
            }
        } catch (SQLException e) {
            exceptionFromDBErrorFactory.createAndThrow(e.getErrorCode(), e);
        }
        return currency;
    }

    private Currency buildCurrency(ResultSet resultSet) throws SQLException {
        return new Currency(
                resultSet.getInt("id"),
                resultSet.getString("code"),
                resultSet.getString("full_name"),
                resultSet.getString("sign")
        );
    }

    public static CurrencyDao getInstance() {
        return INSTANCE;
    }

    private CurrencyDao() {
    }
}
