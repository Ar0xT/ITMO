package server.commands;

import server.commandbase.CommandWithoutArgs;
import common.commandsabstraction.CommandResult;
import server.utilities.CollectionManager;


import java.util.Map;

public class GroupCountingByName extends CommandWithoutArgs {

    public GroupCountingByName(CollectionManager manager) {
        super(manager);
    }

    @Override
    protected CommandResult executeInternal() {
        Map<String, Long> groups = manager.countByName();
        if (groups.isEmpty()) {
            return new CommandResult("Collection is empty.", false);
        }
        StringBuilder result = new StringBuilder("Groups by name:\n");
        groups.forEach((name, count) -> result.append(name).append(": ").append(count).append("\n"));
        return new CommandResult(result.toString(), true, groups);
    }

    @Override
    public String getDescription() {
        return "Group elements by name and show count per group";
    }

    @Override
    public String getUsage() {
        return "group_counting_by_name";
    }
}


