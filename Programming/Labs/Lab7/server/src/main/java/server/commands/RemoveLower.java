package server.commands;

import server.commandbase.CommandWithElement;
import common.commandsabstraction.CommandRequest;
import common.commandsabstraction.CommandResult;
import common.models.MusicBand;
import server.utilities.CollectionManager;
import server.utilities.db.BandRepository;

import java.util.List;
import java.util.stream.Collectors;

public class RemoveLower extends CommandWithElement {
    private final BandRepository bandRepository;

    public RemoveLower(CollectionManager manager, BandRepository bandRepository) {
        super(manager);
        this.bandRepository = bandRepository;
    }

    @Override
    public boolean requiresBand() {
        return true;
    }


    @Override
    protected CommandResult executeInternal(CommandRequest request) {
        MusicBand referenceBand = request.getBand();
        String owner = request.getCredentials().getLogin();

        List<String> keysToRemove = manager.snapshotEntries().stream()
                .filter(entry -> {
                    MusicBand band = entry.getValue();
                    return band.getOwnerLogin() != null
                            && band.getOwnerLogin().equals(owner)
                            && band.compareTo(referenceBand) < 0;
                })
                .map(java.util.Map.Entry::getKey)
                .collect(Collectors.toList());

        if (keysToRemove.isEmpty()) {
            return new CommandResult("No owned bands lower than the given band.", true);
        }

        try {
            int deleted = bandRepository.deleteByKeys(keysToRemove, owner);
            manager.removeKeys(keysToRemove);
            return new CommandResult("Removed " + deleted + " owned bands lower than the given band.", true);
        } catch (Exception e) {
            return new CommandResult("Failed to remove bands: " + e.getMessage(), false);
        }
    }

    @Override
    public String getDescription() {
        return "Remove all bands lower than the specified band";
    }

    @Override
    public String getUsage() {
        return "remove_lower";
    }
}


