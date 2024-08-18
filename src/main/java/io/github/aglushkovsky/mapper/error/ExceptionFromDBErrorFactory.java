package io.github.aglushkovsky.mapper.error;

import java.sql.SQLException;

public interface ExceptionFromDBErrorFactory {
    void createAndThrow(SQLException e);
}
