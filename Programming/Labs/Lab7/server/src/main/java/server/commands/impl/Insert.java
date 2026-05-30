package server.commands.impl;

import server.commands.base.CommandWithKeyAndElement;
import common.commandsabstraction.CommandRequest;
import common.commandsabstraction.CommandResult;
import server.utilities.CollectionManager;
import server.utilities.PersistenceContext;
import java.sql.SQLException;

public class Insert extends CommandWithKeyAndElement {
    private final PersistenceContext persistenceContext;

    public Insert(CollectionManager manager, PersistenceContext persistenceContext) {
        super(manager);
        this.persistenceContext = persistenceContext;
    }

    @Override public boolean requiresBand() { return true; }
    @Override public boolean requiresArgument() { return true; }

    @Override
    protected CommandResult executeInternal(CommandRequest request) {
        String key = request.getArgument();
        String owner = request.getCredentials().getLogin();
        try {
            persistenceContext.insert(key, request.getBand(), owner);
            persistenceContext.flush();
            return new CommandResult("Band added successfully with key '" + key + "'.", true);
        } catch (IllegalStateException e) {
            persistenceContext.clear();
            return new CommandResult(e.getMessage(), false);
        } catch (SQLException e) {
            persistenceContext.clear();
            return new CommandResult("Failed to add band: " + e.getMessage(), false);
        }
    }

    @Override public String getDescription() { return "Add a new band with the specified key"; }
    @Override public String getUsage() { return "insert <key>"; }
}