package server.commandbase;

import common.commandsabstraction.CommandRequest;
import common.commandsabstraction.CommandResult;
import server.utilities.CollectionManager;

public abstract class CommandWithId extends CollectionCommand {

    protected CommandWithId(CollectionManager manager) {
        super(manager);
    }

    @Override
    public CommandResult execute(CommandRequest request) {
        String argument = request.getArgument();
        if (argument == null || argument.isEmpty()) {
            return new CommandResult("Usage: " + getUsage(), false);
        }
        return executeInternal(request);
    }

    protected abstract CommandResult executeInternal(CommandRequest request);

    @Override
    public boolean requiresArgument() {
        return true;
    }
}

