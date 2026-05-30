package server.utilities.db;

public class DbConfig {
    private final String url;
    private final String user;
    private final String password;

    public DbConfig(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public static DbConfig fromEnvOrDefaults() {
        String host = getEnvOrDefault("DB_HOST", "localhost");
        String port = getEnvOrDefault("DB_PORT", "9000");
        String dbName = getEnvOrDefault("DB_NAME", "studs");
        String user = getEnvOrDefault("DB_USER", "s503266");
        String password = getEnvOrDefault("DB_PASSWORD", "vzMfDAaaCyghQx1q");
        String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;
        return new DbConfig(url, user, password);
    }

    private static String getEnvOrDefault(String key, String fallback) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? fallback : value;
    }
}

