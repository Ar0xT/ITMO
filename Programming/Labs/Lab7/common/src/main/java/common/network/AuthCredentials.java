package common.network;

import java.io.Serializable;
import java.util.Objects;

public class AuthCredentials implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String login;
    private final String passwordHash;

    public AuthCredentials(String login, String passwordHash) {
        this.login = login;
        this.passwordHash = passwordHash;
    }

    public String getLogin() {
        return login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuthCredentials that)) return false;
        return Objects.equals(login, that.login)
                && Objects.equals(passwordHash, that.passwordHash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(login, passwordHash);
    }
}

