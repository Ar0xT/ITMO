package server.utilities;

import common.network.AuthCredentials;
import server.utilities.db.repository.UserRepository;

import java.sql.SQLException;

public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AuthResult authenticate(AuthCredentials credentials) throws SQLException {
        if (credentials == null
                || credentials.getLogin() == null
                || credentials.getLogin().isBlank()
                || credentials.getPasswordHash() == null
                || credentials.getPasswordHash().isBlank()) {
            return AuthResult.failure("Authentication required: provide login and password.");
        }

        String login = credentials.getLogin();
        String passwordHash = credentials.getPasswordHash();

        String existingHash = userRepository.findPasswordHash(login);
        if (existingHash == null) {
            boolean created = userRepository.createUser(login, passwordHash);
            if (!created) {
                return AuthResult.failure("Failed to register user.");
            }
            return AuthResult.created("User registered and authenticated.");
        }

        if (!passwordHash.equals(existingHash)) {
            return AuthResult.failure("Invalid login or password.");
        }

        return AuthResult.success("Authenticated.");
    }
}
