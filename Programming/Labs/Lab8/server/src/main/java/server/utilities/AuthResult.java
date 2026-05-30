package server.utilities;

public record AuthResult(boolean success, boolean created, String message) {
    public static AuthResult success(String message) {
        return new AuthResult(true, false, message);
    }

    public static AuthResult created(String message) {
        return new AuthResult(true, true, message);
    }

    public static AuthResult failure(String message) {
        return new AuthResult(false, false, message);
    }
}

