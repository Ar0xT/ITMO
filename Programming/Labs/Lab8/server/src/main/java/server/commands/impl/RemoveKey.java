package server.commands.impl;

import server.commands.base.CommandWithKey;
import common.commandsabstraction.CommandRequest;
import common.commandsabstraction.CommandResult;
import server.utilities.CollectionManager;
import server.utilities.PersistenceContext;
import java.sql.SQLException;

public class RemoveKey extends CommandWithKey {
    private final PersistenceContext persistenceContext;

    public RemoveKey(CollectionManager manager, PersistenceContext persistenceContext) {
        super(manager);
        this.persistenceContext = persistenceContext;
    }

    @Override
    protected CommandResult executeInternal(CommandRequest request) {
        String key = request.getArgument();
        String owner = request.getCredentials().getLogin();
        try {
            persistenceContext.deleteByKey(key, owner);
            persistenceContext.flush();
            return new CommandResult("Band with key '" + key + "' removed.", true);
        } catch (IllegalStateException e) {
            persistenceContext.clear();
            return new CommandResult(e.getMessage(), false);
        } catch (SQLException e) {
            persistenceContext.clear();
            return new CommandResult("Failed to remove band: " + e.getMessage(), false);
        }
    }

    @Override public String getDescription() { return "Remove a band by its key"; }
    @Override public String getUsage() { return "remove_key <key>"; }
}