package server.utilities.db.repository;

import java.sql.SQLException;

public interface UserRepository {
    String findPasswordHash(String login) throws SQLException;
    boolean createUser(String login, String passwordHash) throws SQLException;
}