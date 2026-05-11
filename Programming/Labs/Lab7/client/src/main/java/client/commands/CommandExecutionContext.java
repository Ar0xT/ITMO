package client.commands;

import client.ClientCommandRegistry;
import client.NonBlockingTcpClient;
import common.commandsabstraction.CommandRequest;
import common.models.MusicBand;
import common.network.AuthCredentials;
import common.network.CommandMeta;
import common.network.CommandPacket;
import common.network.ResponsePacket;

/**
 * Central context for command execution and server communication.
 */
public class CommandExecutionContext {
    private static final int SEND_TIMEOUT_MILLIS = 15000;
    private static final int RETRY_DELAY_MILLIS = 500;

    @FunctionalInterface
    public interface BandReader {
        MusicBand read() throws Exception;
    }

    @FunctionalInterface
    public interface ScriptRunner {
        boolean execute(String fileName);
    }

    private final ClientCommandRegistry commandRegistry;
    private final NonBlockingTcpClient tcpClient;
    private final BandReader bandReader;
    private final ScriptRunner scriptRunner;
    private final boolean validateRequiredArgument;
    private final boolean scriptMode;
    private final int maxRetries;

    private AuthCredentials credentials;

    public CommandExecutionContext(ClientCommandRegistry commandRegistry,
                                   NonBlockingTcpClient tcpClient,
                                   BandReader bandReader,
                                   ScriptRunner scriptRunner,
                                   boolean validateRequiredArgument,
                                   boolean scriptMode,
                                   int maxRetries) {
        this.commandRegistry = commandRegistry;
        this.tcpClient = tcpClient;
        this.bandReader = bandReader;
        this.scriptRunner = scriptRunner;
        this.validateRequiredArgument = validateRequiredArgument;
        this.scriptMode = scriptMode;
        this.maxRetries = Math.max(1, maxRetries);
    }

    public CommandMeta getCommandMeta(String commandName) {
        return commandRegistry.get(commandName);
    }

    public boolean shouldValidateRequiredArgument() {
        return validateRequiredArgument;
    }

    public void printUnknownCommand(String commandName, String sourceLabel) {
        if (scriptMode) {
            System.out.println("[" + sourceLabel + "] Unknown command: " + commandName);
        } else {
            System.out.println("Unknown command. Type 'help'.");
        }
    }


    public void printUsage(CommandMeta meta) {
        System.out.println("Usage: " + meta.usage());
    }

    public void printInvalidBandData(String sourceLabel, String details) {
        if (scriptMode) {
            System.out.println("[" + sourceLabel + "] Invalid band data: " + details);
        } else {
            System.out.println("Invalid band data: " + details);
        }
    }

    public CommandExecutionResult handleExit(String sourceLabel) {
        if (scriptMode) {
            System.out.println("[" + sourceLabel + "] Exit requested.");
        }
        return CommandExecutionResult.STOP;
    }

    public boolean executeScript(String fileName) {
        return scriptRunner.execute(fileName);
    }

    public MusicBand readBand() throws Exception {
        return bandReader.read();
    }

    /**
     * Forward a command to the server with retries.
     */
    public void forwardToServer(String commandName, String argument, MusicBand band) {
        CommandRequest request = new CommandRequest(argument, band, credentials);
        CommandPacket packet = new CommandPacket(commandName, request);

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                ResponsePacket response = tcpClient.send(packet, SEND_TIMEOUT_MILLIS);

                System.out.println(response.getResult().getMessage());

                if (response.getResult().getData() != null) {
                    System.out.println(response.getResult().getData());
                }
                return;
            } catch (Exception e) {
                handleSendFailure(commandName, attempt, e);
            }
        }
    }

    private void handleSendFailure(String commandName, int attempt, Exception error) {
        boolean hasRetriesLeft = attempt < maxRetries;

        if (hasRetriesLeft && !scriptMode) {
            System.out.println("Server temporarily unavailable, retrying...");
            try {
                Thread.sleep(RETRY_DELAY_MILLIS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return;
        }

        if (scriptMode) {
            System.out.println("Server unavailable while executing script command '" + commandName + "': "
                    + error.getMessage());
        } else {
            System.out.println("Failed to reach server: " + error.getMessage());
        }
    }

    public void setCredentials(AuthCredentials credentials) {
        this.credentials = credentials;
    }


    public boolean hasCredentials() {
        return credentials != null
                && credentials.getLogin() != null
                && !credentials.getLogin().isBlank()
                && credentials.getPasswordHash() != null
                && !credentials.getPasswordHash().isBlank();
    }

    public void printAuthRequired(String sourceLabel) {
        if (scriptMode) {
            System.out.println("[" + sourceLabel + "] Authentication required.");
        } else {
            System.out.println("Authentication required.");
        }
    }
}
