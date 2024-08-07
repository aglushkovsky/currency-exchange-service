package io.github.aglushkovsky.mapper.error;

import java.sql.SQLException;

public interface ExceptionFromDBErrorFactory {
    void createAndThrow(int errorCode, SQLException e);
}
