package client.ui;

import client.commands.CommandDispatcher;
import client.utilities.AuthInput;
import client.utilities.MusicBandValidator;
import common.models.MusicBand;
import common.network.AuthCredentials;
import java.util.Scanner;

public class ConsoleClientUI implements ClientUI {
    private final Scanner scanner;

    public ConsoleClientUI() {
        this.scanner = new Scanner(System.in);
    }

    @Override public void println(String message) { System.out.println(message); }
    @Override public void print(String message) { System.out.print(message); }
    @Override public void printError(String message) { System.err.println(message); }
    @Override public String readLine() { return scanner.nextLine(); }

    @Override
    public AuthCredentials promptCredentials() {
        return AuthInput.promptCredentials(scanner);
    }

    @Override
    public MusicBand promptMusicBand() {
        return new MusicBandValidator(scanner).askMusicBand();
    }

    @Override
    public void startInteractiveLoop(CommandDispatcher dispatcher) {
        boolean running = true;
        while (running) {
            System.out.print("> ");
            if (!scanner.hasNextLine()) break;
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;
            String[] parts = input.split(" ", 2);
            String commandName = parts[0];
            String argument = parts.length > 1 ? parts[1].trim() : "";
            running = dispatcher.dispatch(commandName, argument, null);
        }
    }

    @Override
    public void close() {
        scanner.close();
    }
}