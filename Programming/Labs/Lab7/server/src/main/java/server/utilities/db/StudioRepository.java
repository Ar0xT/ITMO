package server.utilities.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StudioRepository {
    public Long findOrCreate(Connection connection, String studioName) throws SQLException {
        if (studioName == null || studioName.isBlank()) {
            return null;
        }

        Long existing = findIdByName(connection, studioName);
        if (existing != null) {
            return existing;
        }

        try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO studios (name) VALUES (?) RETURNING id")) {
            stmt.setString(1, studioName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
            }
        }

        return findIdByName(connection, studioName);
    }

    private Long findIdByName(Connection connection, String studioName) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT id FROM studios WHERE lower(name) = lower(?)")) {
            stmt.setString(1, studioName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
            }
        }
        return null;
    }
}

