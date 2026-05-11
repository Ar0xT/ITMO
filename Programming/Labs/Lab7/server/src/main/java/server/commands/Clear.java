package server.commands;

import server.commandbase.CommandWithoutArgs;
import common.commandsabstraction.CommandRequest;
import common.commandsabstraction.CommandResult;
import common.models.MusicBand;
import server.utilities.CollectionManager;
import server.utilities.db.BandRepository;

import java.util.List;
import java.util.stream.Collectors;

public class Clear extends CommandWithoutArgs {
    private final BandRepository bandRepository;

    public Clear(CollectionManager manager, BandRepository bandRepository) {
        super(manager);
        this.bandRepository = bandRepository;
    }


    @Override
    public CommandResult execute(CommandRequest request) {
        String argument = request.getArgument();
        if (argument != null && !argument.isEmpty()) {
            return new CommandResult("This command doesn't require arguments", false);
        }

        String owner = request.getCredentials().getLogin();
        List<String> keysToRemove = manager.snapshotEntries().stream()
                .filter(entry -> {
                    MusicBand band = entry.getValue();
                    return band.getOwnerLogin() != null && band.getOwnerLogin().equals(owner);
                })
                .map(java.util.Map.Entry::getKey)
                .collect(Collectors.toList());

        if (keysToRemove.isEmpty()) {
            return new CommandResult("No bands owned by user to clear.", true);
        }

        try {
            int deleted = bandRepository.deleteByKeys(keysToRemove, owner);
            manager.removeKeys(keysToRemove);
            return new CommandResult("Removed " + deleted + " owned bands.", true);
        } catch (Exception e) {
            return new CommandResult("Failed to clear owned bands: " + e.getMessage(), false);
        }
    }

    @Override
    protected CommandResult executeInternal() {
        return new CommandResult("Authentication required.", false);
    }

    @Override
    public String getDescription() {
        return "Clears all elements of collection";
    }

    @Override
    public String getUsage() {
        return "clear";
    }
}
