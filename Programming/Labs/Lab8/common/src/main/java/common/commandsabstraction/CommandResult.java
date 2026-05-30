package common.commandsabstraction;

import java.io.Serializable;

/**
 * Holds the result after an execution of a command
 */
public class CommandResult implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String message;
    private final boolean success;
    private final Object data;

    public CommandResult(String message, boolean success) {
        this.message = message;
        this.success = success;
        this.data = null;
    }


    public CommandResult(String message, boolean success, Object data) {
        this.message = message;
        this.success = success;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }


    public boolean isSuccess() {
        return success;
    }

    public Object getData() {
        return data;
    }
}

