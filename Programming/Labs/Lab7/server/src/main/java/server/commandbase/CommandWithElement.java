package server.commandbase;

import common.commandsabstraction.CommandRequest;
import common.commandsabstraction.CommandResult;
import common.models.MusicBand;
import server.utilities.CollectionManager;

public abstract class CommandWithElement extends CollectionCommand {

    protected CommandWithElement(CollectionManager manager) {
        super(manager);
    }

    @Override
    public CommandResult execute(CommandRequest request) {
        MusicBand referenceBand = request.getBand();
        if (referenceBand == null) {
            return new CommandResult("No reference band data provided.", false);
        }
        return executeInternal(request);
    }

    protected abstract CommandResult executeInternal(CommandRequest request);

    @Override
    public boolean requiresBand() {
        return true;
    }
}

