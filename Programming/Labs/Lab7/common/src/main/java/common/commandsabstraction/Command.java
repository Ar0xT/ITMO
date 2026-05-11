package common.commandsabstraction;

/**
 * All commands must implement this interface.
 */
public interface Command {
    /**
     * Execute a command.
     * @param request the request containing argument and optional data.
     * @return the result after executing the command.
     */
    CommandResult execute(CommandRequest request);

    /**
     * @return a short description of a command.
     */
    String getDescription();

    /**
     * @return an example of the usage.
     */
    String getUsage();

    /**
     * @return true if this command needs a MusicBand object in the request.
     */
    default boolean requiresBand() {
        return false;
    }

    /**
     * @return true if this command requires an argument.
     */
    default boolean requiresArgument() {
        return false;
    }
}

