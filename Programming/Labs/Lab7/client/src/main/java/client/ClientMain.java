package client;

import client.utilities.AuthInput;
import client.utilities.MusicBandValidator;
import common.commandsabstraction.CommandRequest;
import common.commandsabstraction.CommandResult;
import common.network.AuthCredentials;
import common.network.CommandMeta;
import common.network.CommandPacket;
import common.network.ProtocolConstants;
import common.network.ResponsePacket;

import java.util.Map;
import java.util.Scanner;

public class ClientMain {
    public static void main(String[] args) {
        String host;
        if (args.length > 0) {
            host = args[0];
        } else {
            host = "localhost";
        }
        int port = 5000;
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
                if (port <= 0 || port > 65535) {
                    throw new NumberFormatException("out of range");
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid port: " + args[1] + ". Use value in range 1..65535.");
                return;
            }
        }

        Scanner scanner = new Scanner(System.in);
        MusicBandValidator validator = new MusicBandValidator(scanner);

        AuthCredentials credentials = AuthInput.promptCredentials(scanner);

        ClientCommandRegistry commandRegistry = new ClientCommandRegistry();
        NonBlockingTcpClient tcpClient = new NonBlockingTcpClient(host, port);

        if (!bootstrapCommands(commandRegistry, tcpClient, credentials)) {
            scanner.close();
            return;
        }

        System.out.println("Client mode. Server: " + host + ":" + port);
        System.out.println("Type 'help' for commands.");

        new ClientConsole(scanner, validator, commandRegistry, tcpClient, credentials).run();
        scanner.close();
    }

    private static boolean bootstrapCommands(ClientCommandRegistry commandRegistry,
                                             NonBlockingTcpClient tcpClient,
                                             AuthCredentials credentials) {
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                CommandPacket packet = new CommandPacket(
                        ProtocolConstants.GET_COMMANDS,
                        new CommandRequest("", credentials)
                );
                ResponsePacket responsePacket = tcpClient.send(packet, 15000);
                CommandResult result = responsePacket.getResult();
                if (!result.isSuccess()) {
                    System.err.println("Failed to load command metadata: " + result.getMessage());
                    return false;
                }

                if (!(result.getData() instanceof Map<?, ?> rawMap)) {
                    System.err.println("Server returned invalid metadata format.");
                    return false;
                }

                Map<String, CommandMeta> commandMap = (Map<String, CommandMeta>) rawMap;
                commandRegistry.applyServerCommands(commandMap);
                return true;
            } catch (Exception e) {
                if (attempt == 3) {
                    System.err.println("Cannot connect to server to load command metadata: " + e.getMessage());
                    return false;
                }
                System.err.println("Server temporarily unavailable while loading command metadata. Retrying...");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }
        return false;
    }
}
