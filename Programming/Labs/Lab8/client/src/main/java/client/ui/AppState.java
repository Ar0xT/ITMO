package client.ui;

import common.network.AuthCredentials;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class AppState {
    private final String host;
    private final int port;
    private final ExecutorService executor;
    private final ObjectProperty<Locale> locale = new SimpleObjectProperty<>(Locale.ENGLISH);
    private AuthCredentials credentials;
    private String theme = "light";

    public AppState(String host, int port) {
        this.host = host;
        this.port = port;
        this.executor = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable);
            thread.setDaemon(true);
            thread.setName("client-ui-worker");
            return thread;
        });
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public AuthCredentials getCredentials() {
        return credentials;
    }

    public void setCredentials(AuthCredentials credentials) {
        this.credentials = credentials;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public Locale getLocale() {
        return locale.get();
    }

    public void setLocale(Locale locale) {
        this.locale.set(locale);
    }

    public ObjectProperty<Locale> localeProperty() {
        return locale;
    }

    public void shutdown() {
        executor.shutdownNow();
    }
}
