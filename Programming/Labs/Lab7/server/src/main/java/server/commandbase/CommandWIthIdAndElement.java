package server.commandbase;

import common.commandsabstraction.CommandRequest;
import common.commandsabstraction.CommandResult;
import common.models.MusicBand;
import server.utilities.CollectionManager;

public abstract class CommandWIthIdAndElement extends CollectionCommand {

    protected CommandWIthIdAndElement(CollectionManager manager) {
        super(manager);
    }

    @Override
    public CommandResult execute(CommandRequest request) {
        String argument = request.getArgument();
        if (argument != null) {
            argument = argument.trim();
        }
        if (argument == null || argument.isEmpty()) {
            return new CommandResult("Usage: update <id>", false);
        }

        long id;
        try {
            id = Long.parseLong(argument);
        } catch (NumberFormatException e) {
            return new CommandResult("ID must be a number.", false);
        }

        MusicBand existing = manager.getBandById(id);
        if (existing == null) {
            return new CommandResult("No band found with ID " + id, false);
        }

        if (manager.findKeyByBandId(id) == null) {
            return new CommandResult("Error: band found but key missing.", false);
        }

        MusicBand bandToUpdate = request.getBand();
        if (bandToUpdate == null) {
            return new CommandResult("No band data provided.", false);
        }

        return executeInternal(request);
    }

    protected abstract CommandResult executeInternal(CommandRequest request);
}
