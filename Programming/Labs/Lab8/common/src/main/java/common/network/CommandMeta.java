package common.network;

import java.io.Serializable;

public record CommandMeta(String usage, String description, boolean requiresArgument,
                          boolean requiresBand) implements Serializable {
    private static final long serialVersionUID = 1L;

}

