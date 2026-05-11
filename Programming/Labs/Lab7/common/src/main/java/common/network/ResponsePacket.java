package common.network;

import common.commandsabstraction.CommandResult;

import java.io.Serializable;

public class ResponsePacket implements Serializable {
    private static final long serialVersionUID = 1L;

    private final CommandResult result;

    public ResponsePacket(CommandResult result) {
        this.result = result;
    }

    public CommandResult getResult() {
        return result;
    }
}


