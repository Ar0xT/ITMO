package server.commandbase;

import common.commandsabstraction.CommandRequest;
import common.commandsabstraction.CommandResult;
import server.utilities.CollectionManager;

public abstract class CommandWithoutArgs extends CollectionCommand {

    protected CommandWithoutArgs(CollectionManager manager) {
        super(manager);
    }

    protected CommandWithoutArgs() {
        super();
    }

    @Override
    public CommandResult execute(CommandRequest request) {
        String argument = request.getArgument();
        if (argument != null && !argument.isEmpty()) {
            return new CommandResult("This command doesn't require arguments", false);
        }

        return executeInternal();
    }

    protected abstract CommandResult executeInternal();
}
