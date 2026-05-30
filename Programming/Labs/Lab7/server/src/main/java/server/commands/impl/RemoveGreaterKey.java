package server.commands.impl;

import server.commands.base.CommandWithKey;
import common.commandsabstraction.CommandRequest;
import common.commandsabstraction.CommandResult;
import server.utilities.CollectionManager;
import server.utilities.PersistenceContext;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class RemoveGreaterKey extends CommandWithKey {
    private final PersistenceContext persistenceContext;

    public RemoveGreaterKey(CollectionManager manager, PersistenceContext persistenceContext) {
        super(manager);
        this.persistenceContext = persistenceContext;
    }

    @Override
    protected CommandResult executeInternal(CommandRequest request) {
        String argument = request.getArgument();
        String owner = request.getCredentials().getLogin();
        List<String> keysToRemove = persistenceContext.getCollectionManager().snapshotEntries().stream()
                .filter(entry -> entry.getValue().getOwnerLogin() != null
                        && entry.getValue().getOwnerLogin().equals(owner)
                        && entry.getKey().compareTo(argument) > 0)
                .map(java.util.Map.Entry::getKey)
                .collect(Collectors.toList());
        if (keysToRemove.isEmpty()) {
            return new CommandResult("No owned elements with keys greater than '" + argument + "'.", true);
        }
        try {
            persistenceContext.deleteByKeys(keysToRemove, owner);
            persistenceContext.flush();
            return new CommandResult("Removed " + keysToRemove.size() + " owned elements with keys greater than '" + argument + "'.", true);
        } catch (SQLException e) {
            persistenceContext.clear();
            return new CommandResult("Failed to remove elements: " + e.getMessage(), false);
        }
    }

    @Override public String getDescription() { return "Remove all elements whose key is greater than the given one"; }
    @Override public String getUsage() { return "remove_greater_key <key>"; }
}