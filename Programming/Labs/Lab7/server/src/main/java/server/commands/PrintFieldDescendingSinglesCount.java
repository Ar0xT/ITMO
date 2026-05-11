package server.commands;

import server.commandbase.CommandWithoutArgs;
import common.commandsabstraction.CommandResult;
import server.utilities.CollectionManager;
import common.commandsabstraction.CommandRequest;

import java.util.List;

public class PrintFieldDescendingSinglesCount extends CommandWithoutArgs {

    public PrintFieldDescendingSinglesCount(CollectionManager manager) {
        super(manager);
    }


    @Override
    protected CommandResult executeInternal() {
        List<Integer> singles = manager.getAllSinglesCountDescending();
        if (singles.isEmpty()) {
            return new CommandResult("No singles count values found.", true);
        }
        StringBuilder result = new StringBuilder("Singles counts (descending):\n");
        for (Integer s : singles) {
            result.append(s).append("\n");
        }
        return new CommandResult(result.toString(), true, singles);
    }


    @Override
    public String getDescription() {
        return "Print all singlesCount values in descending order";
    }

    @Override
    public String getUsage() {
        return "print_field_descending_singles_count";
    }
}


