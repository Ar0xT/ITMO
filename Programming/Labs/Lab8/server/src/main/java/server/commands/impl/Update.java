package server.commands.impl;

import server.commands.base.CommandWIthIdAndElement;
import common.commandsabstraction.CommandRequest;
import common.commandsabstraction.CommandResult;
import server.utilities.CollectionManager;
import server.utilities.PersistenceContext;
import java.sql.SQLException;

public class Update extends CommandWIthIdAndElement {
    private final PersistenceContext persistenceContext;

    public Update(CollectionManager manager, PersistenceContext persistenceContext) {
        super(manager);
        this.persistenceContext = persistenceContext;
    }

    @Override public boolean requiresBand() { return true; }
    @Override public boolean requiresArgument() { return true; }

    @Override
    protected CommandResult executeInternal(CommandRequest request) {
        long id = Long.parseLong(request.getArgument().trim());
        String owner = request.getCredentials().getLogin();
        try {
            persistenceContext.update(id, request.getBand(), owner);
            persistenceContext.flush();
            return new CommandResult("Band with ID " + id + " updated.", true);
        } catch (IllegalStateException e) {
            persistenceContext.clear();
            return new CommandResult(e.getMessage(), false);
        } catch (SQLException e) {
            persistenceContext.clear();
            return new CommandResult("Failed to update band: " + e.getMessage(), false);
        }
    }

    @Override public String getDescription() { return "Update a band by its ID"; }
    @Override public String getUsage() { return "update <id>"; }
}