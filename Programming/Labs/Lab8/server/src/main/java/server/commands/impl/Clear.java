package server.commands.impl;

import server.commands.base.CommandWithoutArgs;
import common.commandsabstraction.CommandRequest;
import common.commandsabstraction.CommandResult;
import common.models.MusicBand;
import server.utilities.CollectionManager;
import server.utilities.PersistenceContext;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class Clear extends CommandWithoutArgs {
    private final PersistenceContext persistenceContext;

    public Clear(CollectionManager manager, PersistenceContext persistenceContext) {
        super(manager);
        this.persistenceContext = persistenceContext;
    }

    @Override
    public CommandResult execute(CommandRequest request) {
        String argument = request.getArgument();
        if (argument != null && !argument.isEmpty()) {
            return new CommandResult("This command doesn't require arguments", false);
        }
        String owner = request.getCredentials().getLogin();
        List<String> keysToRemove = persistenceContext.getCollectionManager().snapshotEntries().stream()
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
            persistenceContext.deleteByKeys(keysToRemove, owner);
            persistenceContext.flush();
            return new CommandResult("Removed " + keysToRemove.size() + " owned bands.", true);
        } catch (SQLException e) {
            persistenceContext.clear();
            return new CommandResult("Failed to clear owned bands: " + e.getMessage(), false);
        }
    }

    @Override protected CommandResult executeInternal() { return new CommandResult("Authentication required.", false); }
    @Override public String getDescription() { return "Clears all elements of collection"; }
    @Override public String getUsage() { return "clear"; }
}