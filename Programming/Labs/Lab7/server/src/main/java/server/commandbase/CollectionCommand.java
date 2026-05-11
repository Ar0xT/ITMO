package server.commandbase;

import common.commandsabstraction.Command;
import server.utilities.CollectionManager;

public abstract class CollectionCommand implements Command {
    protected final CollectionManager manager;

    protected CollectionCommand(CollectionManager manager) {
        this.manager = manager;
    }

    protected CollectionCommand() {
        this.manager = null;
    }
}
