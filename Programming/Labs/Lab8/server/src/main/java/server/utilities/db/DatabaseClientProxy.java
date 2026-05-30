package server.utilities.db;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseClientProxy {
    private final ConnectionProvider connectionProvider;

    public DatabaseClientProxy(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public <T> T execute(SqlFunction<Connection, T> action) throws SQLException {
        try (Connection connection = connectionProvider.openConnection()) {
            return action.apply(connection);
        }
    }


    public <T> T executeInTransaction(SqlFunction<Connection, T> action) throws SQLException {
        try (Connection connection = connectionProvider.openConnection()) {
            boolean previousAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            try {
                T result = action.apply(connection);
                connection.commit();
                return result;
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(previousAutoCommit);
            }
        }
    }
}

