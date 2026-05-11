package server.commands;

import server.commandbase.CommandWithoutArgs;
import common.commandsabstraction.CommandResult;
import server.utilities.CollectionManager;
import common.commandsabstraction.CommandRequest;
import server.utilities.FileManager;

public class Save extends CommandWithoutArgs {
    private FileManager fileManager;

    public Save(FileManager fileManager, CollectionManager manager) {
        super(manager);
        this.fileManager = fileManager;
    }

    @Override
    protected CommandResult executeInternal() {
        try {
            fileManager.save(manager);
            return new CommandResult("Collection saved successfully.", true);
        } catch (Exception e) {
            return new CommandResult("Error saving: " + e.getMessage(), false);
        }
    }

    @Override
    public String getDescription() {
        return "Save collection to file";
    }

    @Override
    public String getUsage() {
        return "save";
    }
}


