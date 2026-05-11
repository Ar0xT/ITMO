package common.network;

import common.commandsabstraction.CommandRequest;

import java.io.Serializable;

public class CommandPacket implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String commandName;
    private final CommandRequest request;

    public CommandPacket(String commandName, CommandRequest request) {
        this.commandName = commandName;
        this.request = request;
    }

    public String getCommandName() {
        return commandName;
    }

    public CommandRequest getRequest() {
        return request;
    }
}


