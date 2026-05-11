package server.commandbase;

import common.commandsabstraction.CommandRequest;
import common.commandsabstraction.CommandResult;
import server.utilities.CollectionManager;

public abstract class CommandWithKeyAndElement extends CollectionCommand {

    protected CommandWithKeyAndElement(CollectionManager manager) {
        super(manager);
    }

    @Override
    public CommandResult execute(CommandRequest request) {
        String key = request.getArgument();
        if (key == null || key.isEmpty()) {
            return new CommandResult("Usage: " + getUsage(), false);
        }

        if (manager.containsKey(key)) {
            return new CommandResult("Band with key " + key + " is already used. Use 'update' to modify", false);
        }

        if (request.getBand() == null) {
            return new CommandResult("No band data provided.", false);
        }

        return executeInternal(request);
    }

    protected abstract CommandResult executeInternal(CommandRequest request);

    @Override
    public boolean requiresArgument() {
        return true;
    }

    @Override
    public boolean requiresBand() {
        return true;
    }
}

