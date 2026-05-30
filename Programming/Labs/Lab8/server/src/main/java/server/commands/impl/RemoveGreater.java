package server.commands.impl;

import server.commands.base.CommandWithElement;
import common.commandsabstraction.CommandRequest;
import common.commandsabstraction.CommandResult;
import common.models.MusicBand;
import server.utilities.CollectionManager;
import server.utilities.PersistenceContext;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class RemoveGreater extends CommandWithElement {
    private final PersistenceContext persistenceContext;

    public RemoveGreater(CollectionManager manager, PersistenceContext persistenceContext) {
        super(manager);
        this.persistenceContext = persistenceContext;
    }

    @Override public boolean requiresBand() { return true; }

    @Override
    protected CommandResult executeInternal(CommandRequest request) {
        MusicBand referenceBand = request.getBand();
        String owner = request.getCredentials().getLogin();
        List<String> keysToRemove = persistenceContext.getCollectionManager().snapshotEntries().stream()
                .filter(entry -> {
                    MusicBand band = entry.getValue();
                    return band.getOwnerLogin() != null
                            && band.getOwnerLogin().equals(owner)
                            && band.compareTo(referenceBand) > 0;
                })
                .map(java.util.Map.Entry::getKey)
                .collect(Collectors.toList());
        if (keysToRemove.isEmpty()) {
            return new CommandResult("No owned bands greater than the given band.", true);
        }
        try {
            persistenceContext.deleteByKeys(keysToRemove, owner);
            persistenceContext.flush();
            return new CommandResult("Removed " + keysToRemove.size() + " owned bands greater than the given band.", true);
        } catch (SQLException e) {
            persistenceContext.clear();
            return new CommandResult("Failed to remove bands: " + e.getMessage(), false);
        }
    }

    @Override public String getDescription() { return "Remove all bands greater than the specified band"; }
    @Override public String getUsage() { return "remove_greater"; }
}