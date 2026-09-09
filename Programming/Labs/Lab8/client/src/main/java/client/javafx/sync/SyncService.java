package client.javafx.sync;

import client.ClientCommandRegistry;
import client.NonBlockingTcpClient;
import client.javafx.network.CommandNames;
import client.ui.AppState;
import client.ui.AppState.ConnectionState;
import client.ui.AppState.SyncDiff;
import common.commandsabstraction.CommandRequest;
import common.commandsabstraction.CommandResult;
import common.models.MusicBand;
import common.network.CommandMeta;
import common.network.CommandPacket;
import common.network.ProtocolConstants;
import common.network.ResponsePacket;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.layout.BorderPane;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Background service that periodically polls the server:
 *   1. {@code __get_commands__} refreshes the command metadata in ClientCommandRegistry
 *   2. {@code show}             refreshes the actual band collection into AppState.Items
 *
 * Also tracks connection state and the diff between successive polls so the UI can show
 * real-time "X added, Y removed" toasts and a connection status indicator.
 */
public class SyncService {
    private static final Logger LOGGER = Logger.getLogger(SyncService.class.getName());
    private static final int POLL_INTERVAL_MILLIS = 3000;
    private static final int METADATA_TIMEOUT_MILLIS = 5000;
    private static final int DATA_TIMEOUT_MILLIS = 5000;
    private static final int RECONNECT_FAIL_THRESHOLD = 2;

    private final AppState appState;
    private final ClientCommandRegistry commandRegistry;
    private final BorderPane root;
    private final Runnable onConnected;
    private final AtomicBoolean lastDataPollSucceeded = new AtomicBoolean(false);
    private final AtomicBoolean lastMetadataPollSucceeded = new AtomicBoolean(false);
    private final Set<Long> lastSeenIds = new HashSet<>();
    private int consecutiveFailures = 0;
    private volatile boolean running = true;
    private Thread worker;

    public SyncService(AppState appState, ClientCommandRegistry commandRegistry, BorderPane root) {
        this(appState, commandRegistry, root, null);
    }

    public SyncService(AppState appState, ClientCommandRegistry commandRegistry,
                       BorderPane root, Runnable onConnected) {
        this.appState = appState;
        this.commandRegistry = commandRegistry;
        this.root = root;
        this.onConnected = onConnected;
    }

    public void start() {
        if (worker != null) return;
        consecutiveFailures = 0;
        lastSeenIds.clear();
        lastDataPollSucceeded.set(false);
        lastMetadataPollSucceeded.set(false);
        Platform.runLater(() -> appState.setConnectionState(ConnectionState.CONNECTING));
        worker = new Thread(this::runLoop, "client-sync-worker");
        worker.setDaemon(true);
        worker.start();
    }

    public void stop() {
        running = false;
        if (worker != null) worker.interrupt();
    }

    private void runLoop() {
        boolean firstPoll = true;
        while (running) {
            try {
                if (appState.getCredentials() != null) {
                    pollMetadata();
                    pollData();
                    boolean ok = lastDataPollSucceeded.get() || lastMetadataPollSucceeded.get();
                    if (ok) {
                        if (consecutiveFailures >= RECONNECT_FAIL_THRESHOLD) {
                            Platform.runLater(() -> appState.setConnectionState(ConnectionState.CONNECTED));
                        }
                        consecutiveFailures = 0;
                        if (firstPoll) {
                            firstPoll = false;
                            Platform.runLater(() -> appState.setConnectionState(ConnectionState.CONNECTED));
                            if (onConnected != null) {
                                Platform.runLater(onConnected);
                            }
                        }
                    } else {
                        consecutiveFailures++;
                        if (consecutiveFailures == RECONNECT_FAIL_THRESHOLD) {
                            Platform.runLater(() -> appState.setConnectionState(ConnectionState.DISCONNECTED));
                        }
                    }
                }
            } catch (Exception e) {
                if (running) {
                    LOGGER.log(Level.WARNING, "Sync error", e);
                }
            }
            try {
                Thread.sleep(POLL_INTERVAL_MILLIS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private void pollMetadata() {
        try {
            NonBlockingTcpClient client = new NonBlockingTcpClient(appState.getHost(), appState.getPort());
            CommandPacket packet = new CommandPacket(
                    ProtocolConstants.GET_COMMANDS,
                    new CommandRequest("", appState.getCredentials()));
            ResponsePacket response = client.send(packet, METADATA_TIMEOUT_MILLIS);
            CommandResult result = response.getResult();
            lastMetadataPollSucceeded.set(result.isSuccess());
            if (result.isSuccess() && result.getData() instanceof Map<?, ?> raw) {
                @SuppressWarnings("unchecked")
                Map<String, CommandMeta> meta = (Map<String, CommandMeta>) raw;
                Platform.runLater(() -> commandRegistry.applyServerCommands(meta));
            }
        } catch (Exception e) {
            lastMetadataPollSucceeded.set(false);
            LOGGER.log(Level.FINE, "Metadata poll failed", e);
        }
    }

    private void pollData() {
        try {
            NonBlockingTcpClient client = new NonBlockingTcpClient(appState.getHost(), appState.getPort());
            CommandPacket packet = new CommandPacket(
                    CommandNames.SHOW,
                    new CommandRequest("", appState.getCredentials()));
            ResponsePacket response = client.send(packet, DATA_TIMEOUT_MILLIS);
            CommandResult result = response.getResult();
            lastDataPollSucceeded.set(result.isSuccess());
            if (result.isSuccess() && result.getData() instanceof Map<?, ?> raw) {
                @SuppressWarnings("unchecked")
                Map<String, MusicBand> entries = (Map<String, MusicBand>) raw;
                Platform.runLater(() -> updateItems(entries));
            }
        } catch (Exception e) {
            lastDataPollSucceeded.set(false);
            LOGGER.log(Level.FINE, "Data poll failed", e);
        }
    }

    private void updateItems(Map<String, MusicBand> entries) {
        appState.setIdToKey(entries);

        Set<Long> newIds = new HashSet<>();
        int added = 0;
        for (MusicBand b : entries.values()) {
            if (b != null) {
                long id = b.getId();
                newIds.add(id);
                if (!lastSeenIds.contains(id)) {
                    added++;
                }
            }
        }
        int removed = 0;
        for (Long oldId : lastSeenIds) {
            if (!newIds.contains(oldId)) removed++;
        }
        lastSeenIds.clear();
        lastSeenIds.addAll(newIds);

        ObservableList<MusicBand> target = appState.getItems();
        target.setAll(entries.values());

        SyncDiff diff = new SyncDiff(added, removed, 0);
        appState.setLastSyncDiff(diff);
    }
}
