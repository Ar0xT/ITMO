package server.commands.impl;

import server.commands.base.CommandWithoutArgs;
import common.commandsabstraction.CommandResult;
import server.utilities.CollectionManager;

public class Info extends CommandWithoutArgs {

    public Info(CollectionManager manager) {
        super(manager);
    }


    @Override
    protected CommandResult executeInternal() {
        String info = "Collection type: " + manager.getCollectionType() + "\n"
                + "Initialization date: " + manager.getInitializationDate() + "\n"
                + "Number of elements: " + manager.size();
        return new CommandResult(info, true);
    }

    @Override
    public String getDescription() {
        return "Shows information about the collection";
    }

    @Override
    public String getUsage() {
        return "info";
    }
}


