package server.commandbase;

import common.commandsabstraction.CommandRequest;
import common.commandsabstraction.CommandResult;
import server.utilities.CollectionManager;

public abstract class CommandWithOtherArgs extends CollectionCommand {

    protected CommandWithOtherArgs(CollectionManager manager) {
        super(manager);
    }

    @Override
    public CommandResult execute(CommandRequest request) {
        if (request.getArgument() == null || request.getArgument().isEmpty()) {
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

