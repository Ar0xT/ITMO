package server.utilities.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresConnectionProvider implements ConnectionProvider {
    private final DbConfig config;

    public PostgresConnectionProvider(DbConfig config) {
        this.config = config;
    }

    @Override
    public Connection openConnection() throws SQLException {
        return DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPassword());
    }
}

