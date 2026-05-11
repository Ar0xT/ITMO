package server.commands;

import server.commandbase.CommandWithoutArgs;
import common.commandsabstraction.Command;
import common.commandsabstraction.CommandResult;
import common.commandsabstraction.CommandRequest;

import java.util.Map;

public class Help extends CommandWithoutArgs {
    private Map<String, Command> commands;

    public Help(Map<String, Command> commands) {
        this.commands = commands;
    }

    @Override
    protected CommandResult executeInternal() {
        StringBuilder help = new StringBuilder("Available commands:\n");
        for (Map.Entry<String, Command> entry : this.commands.entrySet()) {
            help.append(" ").append(entry.getKey())
                    .append(" - ").append(entry.getValue().getDescription())
                    .append("\n    Usage: ").append(entry.getValue().getUsage())
                    .append("\n");
        }
        return new CommandResult(help.toString(), true);
    }

    @Override
    public String getDescription() {
        return "Show all commands";
    }

    @Override
    public String getUsage() {
        return "help";
    }
}


