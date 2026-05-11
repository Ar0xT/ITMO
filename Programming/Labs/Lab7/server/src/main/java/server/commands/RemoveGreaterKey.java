package server.commands;

import server.commandbase.CommandWithKey;
import common.commandsabstraction.CommandRequest;
import common.commandsabstraction.CommandResult;
import common.models.MusicBand;
import server.utilities.CollectionManager;
import server.utilities.db.BandRepository;

import java.util.List;
import java.util.stream.Collectors;

public class RemoveGreaterKey extends CommandWithKey {
    private final BandRepository bandRepository;

    public RemoveGreaterKey(CollectionManager manager, BandRepository bandRepository) {
        super(manager);
        this.bandRepository = bandRepository;
    }


    @Override
    protected CommandResult executeInternal(CommandRequest request) {
        String argument = request.getArgument();
        String owner = request.getCredentials().getLogin();

        List<String> keysToRemove = manager.snapshotEntries().stream()
                .filter(entry -> {
                    MusicBand band = entry.getValue();
                    return band.getOwnerLogin() != null
                            && band.getOwnerLogin().equals(owner)
                            && entry.getKey().compareTo(argument) > 0;
                })
                .map(java.util.Map.Entry::getKey)
                .collect(Collectors.toList());

        if (keysToRemove.isEmpty()) {
            return new CommandResult("No owned elements with keys greater than '" + argument + "'.", true);
        }

        try {
            int deleted = bandRepository.deleteByKeys(keysToRemove, owner);
            manager.removeKeys(keysToRemove);
            return new CommandResult("Removed " + deleted + " owned elements with keys greater than '" + argument + "'.", true);
        } catch (Exception e) {
            return new CommandResult("Failed to remove elements: " + e.getMessage(), false);
        }
    }


    @Override
    public String getDescription() {
        return "Remove all elements whose key is greater than the given one";
    }

    @Override
    public String getUsage() {
        return "remove_greater_key <key>";
    }
}


