package client.javafx.network;

import client.ui.AppState;
import common.commandsabstraction.CommandRequest;
import common.commandsabstraction.CommandResult;
import common.models.MusicBand;
import common.network.CommandPacket;
import common.network.ResponsePacket;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * High-level wrapper around the 13 server commands. Each method submits a
 * command on {@link AppState#getExecutor()} and returns a
 * {@link CompletableFuture} that completes with the server's
 * {@link CommandResult}. UI code can chain a success/failure handler without
 * having to deal with the raw TCP layer.
 */
public class BandService {

    private final AppState appState;
    private final CommandClient client;
    private final Executor executor;

    public BandService(AppState appState) {
        this.appState = appState;
        this.client = new CommandClient(appState);
        this.executor = appState.getExecutor();
    }

    private CompletableFuture<CommandResult> run(String commandName, CommandRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return client.send(commandName, request);
            } catch (Exception e) {
                return new CommandResult("Network error: " + e.getMessage(), false);
            }
        }, executor);
    }

    public CompletableFuture<CommandResult> show() {
        return run(CommandNames.SHOW, new CommandRequest("", credentials()));
    }

    public CompletableFuture<CommandResult> info() {
        return run(CommandNames.INFO, new CommandRequest("", credentials()));
    }

    public CompletableFuture<CommandResult> help() {
        return run(CommandNames.HELP, new CommandRequest("", credentials()));
    }

    public CompletableFuture<CommandResult> insert(String key, MusicBand band) {
        return run(CommandNames.INSERT, new CommandRequest(key, band, credentials()));
    }

    public CompletableFuture<CommandResult> update(long id, MusicBand band) {
        return run(CommandNames.UPDATE, new CommandRequest(String.valueOf(id), band, credentials()));
    }

    public CompletableFuture<CommandResult> removeKey(String key) {
        return run(CommandNames.REMOVE_KEY, new CommandRequest(key, credentials()));
    }

    public CompletableFuture<CommandResult> removeGreater(MusicBand reference) {
        return run(CommandNames.REMOVE_GREATER, new CommandRequest("", reference, credentials()));
    }

    public CompletableFuture<CommandResult> removeGreaterKey(String key) {
        return run(CommandNames.REMOVE_GREATER_KEY, new CommandRequest(key, credentials()));
    }

    public CompletableFuture<CommandResult> removeLower(MusicBand reference) {
        return run(CommandNames.REMOVE_LOWER, new CommandRequest("", reference, credentials()));
    }

    public CompletableFuture<CommandResult> clear() {
        return run(CommandNames.CLEAR, new CommandRequest("", credentials()));
    }

    public CompletableFuture<CommandResult> countByNumberOfParticipants(long n) {
        return run(CommandNames.COUNT_BY_PARTICIPANTS, new CommandRequest(String.valueOf(n), credentials()));
    }

    public CompletableFuture<CommandResult> groupCountingByName() {
        return run(CommandNames.GROUP_COUNTING_BY_NAME, new CommandRequest("", credentials()));
    }

    public CompletableFuture<CommandResult> printFieldDescendingSinglesCount() {
        return run(CommandNames.PRINT_FIELD_DESCENDING_SINGLES_COUNT, new CommandRequest("", credentials()));
    }

    private common.network.AuthCredentials credentials() {
        return appState.getCredentials();
    }
}
