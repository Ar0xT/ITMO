package client;

import client.commands.CommandDispatcher;
import client.commands.CommandExecutionContext;
import client.utilities.MusicBandValidator;
import common.network.AuthCredentials;

import java.util.Scanner;

/**
 * Interactive console for the client.
 */
public class ClientConsole {
    private final Scanner scanner;
    private final CommandDispatcher dispatcher;

    public ClientConsole(Scanner scanner,
                         MusicBandValidator validator,
                         ClientCommandRegistry commandRegistry,
                         NonBlockingTcpClient tcpClient,
                         AuthCredentials credentials) {
        this.scanner = scanner;

        ScriptExecutor scriptExecutor = new ScriptExecutor(commandRegistry, tcpClient);

        CommandExecutionContext context = new CommandExecutionContext(
                commandRegistry,
                tcpClient,
                validator::askMusicBand,
                scriptExecutor::executeScript,
                true,
                false,
                3
        );

        context.setCredentials(credentials);

        this.dispatcher = new CommandDispatcher(context);
    }

    /**
     * Run the interactive console loop.
     */
    public void run() {
        boolean running = true;

        while (running) {
            System.out.print("> ");
            if (!scanner.hasNextLine()) {
                break;
            }

            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                continue;
            }

            String[] parts = input.split(" ", 2);
            String commandName = parts[0];
            String argument = parts.length > 1 ? parts[1].trim() : "";

            running = dispatcher.dispatch(commandName, argument, null);
        }
    }
}
