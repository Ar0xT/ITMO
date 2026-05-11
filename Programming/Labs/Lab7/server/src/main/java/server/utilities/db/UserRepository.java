package server.utilities.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepository {
    private final DatabaseClientProxy db;

    public UserRepository(DatabaseClientProxy db) {
        this.db = db;
    }

    public String findPasswordHash(String login) throws SQLException {
        return db.execute(connection -> {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "SELECT password_hash FROM users WHERE login = ?")) {
                stmt.setString(1, login);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("password_hash");
                    }
                    return null;
                }
            }
        });
    }

    public boolean createUser(String login, String passwordHash) throws SQLException {
        return db.execute(connection -> {
            try (PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO users (login, password_hash) VALUES (?, ?)")) {
                stmt.setString(1, login);
                stmt.setString(2, passwordHash);
                return stmt.executeUpdate() == 1;
            }
        });
    }

}
