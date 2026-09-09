package client.javafx.network;

import client.NonBlockingTcpClient;
import client.ui.AppState;
import common.commandsabstraction.CommandRequest;
import common.commandsabstraction.CommandResult;
import common.network.CommandPacket;
import common.network.ResponsePacket;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * Sends a single command packet to the server and returns the result.
 * Should be invoked on a background thread (e.g. {@code AppState.getExecutor()}).
 */
public final class CommandClient {

    private final AppState appState;
    private final int timeoutMillis;

    public CommandClient(AppState appState) {
        this(appState, 5000);
    }

    public CommandClient(AppState appState, int timeoutMillis) {
        this.appState = appState;
        this.timeoutMillis = timeoutMillis;
    }

    public CommandResult send(String commandName, CommandRequest request) throws IOException, ClassNotFoundException {
        if (request.getCredentials() == null && appState.getCredentials() != null) {
            request = new CommandRequest(
                    request.getArgument(),
                    request.getBand(),
                    appState.getCredentials()
            );
        }
        CommandPacket packet = new CommandPacket(commandName, request);
        NonBlockingTcpClient client = new NonBlockingTcpClient(appState.getHost(), appState.getPort());
        ResponsePacket response = client.send(packet, timeoutMillis);
        return response.getResult();
    }

    public Callable<CommandResult> asCallable(String commandName, CommandRequest request) {
        return () -> send(commandName, request);
    }
}
