package client.app;

import client.ClientCommandRegistry;
import client.NonBlockingTcpClient;
import client.ScriptExecutor;
import client.commands.CommandDispatcher;
import client.commands.CommandExecutionContext;
import client.ui.ClientUI;
import common.commandsabstraction.CommandRequest;
import common.commandsabstraction.CommandResult;
import common.network.AuthCredentials;
import common.network.CommandMeta;
import common.network.CommandPacket;
import common.network.ProtocolConstants;
import common.network.ResponsePacket;
import java.util.Map;

public class ClientApplication {
    private final String host;
    private final int port;
    private final ClientUI ui;

    public ClientApplication(String host, int port, ClientUI ui) {
        this.host = host;
        this.port = port;
        this.ui = ui;
    }

    public void start() {
        AuthCredentials credentials = ui.promptCredentials();
        if (credentials == null) return;

        ClientCommandRegistry commandRegistry = new ClientCommandRegistry();
        NonBlockingTcpClient tcpClient = new NonBlockingTcpClient(host, port);

        if (!bootstrapCommands(commandRegistry, tcpClient, credentials)) return;

        ui.println("Client mode. Server: " + host + ":" + port);
        ui.println("Type 'help' for commands.");

        ScriptExecutor scriptExecutor = new ScriptExecutor(commandRegistry, tcpClient, ui);
        CommandExecutionContext context = new CommandExecutionContext(
                commandRegistry, tcpClient,
                ui::promptMusicBand,
                scriptExecutor::executeScript,
                true, false, 3, ui
        );
        context.setCredentials(credentials);

        CommandDispatcher dispatcher = new CommandDispatcher(context);
        try {
            ui.startInteractiveLoop(dispatcher);
        } finally {
            ui.close();
        }
    }

    private boolean bootstrapCommands(ClientCommandRegistry commandRegistry,
                                      NonBlockingTcpClient tcpClient,
                                      AuthCredentials credentials) {
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                CommandPacket packet = new CommandPacket(
                        ProtocolConstants.GET_COMMANDS,
                        new CommandRequest("", credentials));
                ResponsePacket responsePacket = tcpClient.send(packet, 15000);
                CommandResult result = responsePacket.getResult();
                if (!result.isSuccess()) {
                    ui.printError("Failed to load command metadata: " + result.getMessage());
                    return false;
                }
                if (!(result.getData() instanceof Map<?, ?> rawMap)) {
                    ui.printError("Server returned invalid metadata format.");
                    return false;
                }
                @SuppressWarnings("unchecked")
                Map<String, CommandMeta> commandMap = (Map<String, CommandMeta>) rawMap;
                commandRegistry.applyServerCommands(commandMap);
                return true;
            } catch (Exception e) {
                if (attempt == 3) {
                    ui.printError("Cannot connect to server to load command metadata: " + e.getMessage());
                    return false;
                }
                ui.printError("Server temporarily unavailable while loading command metadata. Retrying...");
                try { Thread.sleep(500); } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }
        return false;
    }
}