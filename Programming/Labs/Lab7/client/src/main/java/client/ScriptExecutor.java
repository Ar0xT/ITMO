package client;

import client.commands.CommandDispatcher;
import client.commands.CommandExecutionContext;
import common.models.Coordinates;
import common.models.MusicBand;
import common.models.MusicGenre;
import common.models.Studio;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class ScriptExecutor {
    private final CommandDispatcher dispatcher;
    private final Set<String> runningScripts = new HashSet<>();
    private Scanner activeScanner;

    public ScriptExecutor(ClientCommandRegistry commandRegistry, NonBlockingTcpClient tcpClient) {
        CommandExecutionContext context = new CommandExecutionContext(
                commandRegistry,
                tcpClient,
                this::readBandFromActiveScript,
                this::executeScript,
                false,
                true,
                1
        );
        this.dispatcher = new CommandDispatcher(context);
    }

    public boolean executeScript(String fileName) {
        if (runningScripts.contains(fileName)) {
            System.out.println("Recursion detected for script: " + fileName);
            return true;
        }

        File scriptFile = new File(fileName);
        if (!scriptFile.exists() || !scriptFile.canRead()) {
            System.out.println("Script file is unavailable: " + fileName);
            return true;
        }

        runningScripts.add(fileName);
        try (Scanner scanner = new Scanner(scriptFile)) {
            int lineNo = 0;
            while (scanner.hasNextLine()) {
                lineNo++;
                String rawLine = scanner.nextLine().trim();
                if (rawLine.isEmpty() || rawLine.startsWith("#")) {
                    continue;
                }

                String[] parts = rawLine.split(" ", 2);
                String commandName = parts[0];
                String argument = parts.length > 1 ? parts[1].trim() : "";

                activeScanner = scanner;
                boolean shouldContinue = dispatcher.dispatch(commandName, argument, fileName + ":" + lineNo);
                if (!shouldContinue) {
                    return false;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Script file not found: " + fileName);
        } finally {
            activeScanner = null;
            runningScripts.remove(fileName);
        }

        return true;
    }

    private MusicBand readBandFromScript(Scanner scanner) {
        String name = scanner.nextLine().trim();
        double x = Double.parseDouble(scanner.nextLine().trim());
        int y = Integer.parseInt(scanner.nextLine().trim());
        long participants = Long.parseLong(scanner.nextLine().trim());

        String singlesLine = scanner.nextLine().trim();
        Integer singles = singlesLine.isEmpty() ? null : Integer.parseInt(singlesLine);

        String dateLine = scanner.nextLine().trim();
        LocalDate establishmentDate = dateLine.isEmpty() ? null : LocalDate.parse(dateLine);

        String genreLine = scanner.nextLine().trim();
        MusicGenre genre = genreLine.isEmpty() ? null : MusicGenre.valueOf(genreLine);

        String studioLine = scanner.nextLine().trim();
        Studio studio = studioLine.isEmpty() ? null : new Studio(studioLine);

        return new MusicBand(name, new Coordinates(x, y), participants, singles, establishmentDate, genre, studio);
    }

    private MusicBand readBandFromActiveScript() {
        if (activeScanner == null) {
            throw new IllegalStateException("Script scanner is not active.");
        }
        return readBandFromScript(activeScanner);
    }
}
