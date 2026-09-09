package client.ui;

import common.models.MusicBand;
import common.network.AuthCredentials;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppState {

    public enum ConnectionState { CONNECTING, CONNECTED, DISCONNECTED }

    public static final class SyncDiff {
        public final int added;
        public final int removed;
        public final int updated;
        public SyncDiff(int added, int removed, int updated) {
            this.added = added;
            this.removed = removed;
            this.updated = updated;
        }
        public boolean isEmpty() {
            return added == 0 && removed == 0 && updated == 0;
        }
    }

    private final String host;
    private final int port;
    private final ExecutorService executor;
    private final ObjectProperty<Locale> locale = new SimpleObjectProperty<>(Locale.ENGLISH);
    private final StringProperty theme = new SimpleStringProperty("light");
    private final ObjectProperty<AuthCredentials> credentialsProperty = new SimpleObjectProperty<>();
    private final ObservableList<MusicBand> items = FXCollections.observableArrayList();
    private final ObservableMap<String, String> userColorMap = FXCollections.observableHashMap();
    private final StringProperty statusMessage = new SimpleStringProperty("");
    private final StringProperty statusKind = new SimpleStringProperty("info");
    private final Map<Long, String> idToKey = new HashMap<>();
    private String currentUser;
    private final ObjectProperty<ConnectionState> connectionState = new SimpleObjectProperty<>(ConnectionState.CONNECTING);
    private final ObjectProperty<SyncDiff> lastSyncDiff = new SimpleObjectProperty<>(new SyncDiff(0, 0, 0));

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
        return credentialsProperty.get();
    }

    public void setCredentials(AuthCredentials credentials) {
        credentialsProperty.set(credentials);
        if (credentials != null) {
            this.currentUser = credentials.getLogin();
        } else {
            this.currentUser = null;
        }
    }

    public ObjectProperty<AuthCredentials> credentialsProperty() {
        return credentialsProperty;
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public String getTheme() {
        return theme.get();
    }

    public void setTheme(String theme) {
        this.theme.set(theme);
    }

    public StringProperty themeProperty() {
        return theme;
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

    public ObservableList<MusicBand> getItems() {
        return items;
    }

    public ObservableMap<String, String> getUserColorMap() {
        return userColorMap;
    }

    public String getKeyForId(long id) {
        return idToKey.get(id);
    }

    public void setIdToKey(Map<String, MusicBand> entries) {
        idToKey.clear();
        if (entries != null) {
            entries.forEach((key, band) -> idToKey.put(band.getId(), key));
        }
    }

    public StringProperty statusMessageProperty() {
        return statusMessage;
    }

    public String getStatusMessage() {
        return statusMessage.get();
    }

    public StringProperty statusKindProperty() {
        return statusKind;
    }

    public String getStatusKind() {
        return statusKind.get();
    }

    public void setStatus(String message, String kind) {
        statusMessage.set(message == null ? "" : message);
        statusKind.set(kind == null ? "info" : kind);
    }

    public ConnectionState getConnectionState() {
        return connectionState.get();
    }

    public void setConnectionState(ConnectionState state) {
        if (state != null) connectionState.set(state);
    }

    public ObjectProperty<ConnectionState> connectionStateProperty() {
        return connectionState;
    }

    public SyncDiff getLastSyncDiff() {
        return lastSyncDiff.get();
    }

    public void setLastSyncDiff(SyncDiff diff) {
        if (diff != null) lastSyncDiff.set(diff);
    }

    public ObjectProperty<SyncDiff> lastSyncDiffProperty() {
        return lastSyncDiff;
    }

    public void clearData() {
        items.clear();
        idToKey.clear();
        lastSyncDiff.set(new SyncDiff(0, 0, 0));
    }

    public void shutdown() {
        executor.shutdownNow();
    }
}
