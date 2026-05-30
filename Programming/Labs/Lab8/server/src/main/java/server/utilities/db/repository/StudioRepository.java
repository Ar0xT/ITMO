package server.utilities.db.repository;

import java.sql.Connection;
import java.sql.SQLException;

public interface StudioRepository {
    Long findOrCreate(Connection connection, String studioName) throws SQLException;
}