package client.commands;

import client.commands.handlers.CommandHandlers;
import common.network.CommandMeta;

import java.util.List;

/**
 * Routes commands to appropriate handlers.
 */
public class CommandDispatcher {
    private final CommandExecutionContext context;
    private final List<ClientCommandHandler> localHandlers;

    public CommandDispatcher(CommandExecutionContext context) {
        this.context = context;
        this.localHandlers = List.of(
                new CommandHandlers.ExitHandler(),
                new CommandHandlers.ExecuteScriptHandler()
        );
    }

    /**
     * Dispatch a command to the appropriate handler.
     */
    public boolean dispatch(String commandName, String argument, String sourceLabel) {
        CommandMeta meta = context.getCommandMeta(commandName);
        if (meta == null) {
            context.printUnknownCommand(commandName, sourceLabel);
            return true;
        }

        if (context.shouldValidateRequiredArgument() && meta.requiresArgument() && argument.isBlank()) {
            context.printUsage(meta);
            return true;
        }

        for (ClientCommandHandler handler : localHandlers) {
            if (handler.supports(commandName)) {
                CommandExecutionResult result = handler.handle(context, commandName, argument, meta, sourceLabel);
                return result == CommandExecutionResult.CONTINUE;
            }
        }

        ClientCommandHandler serverHandler = new CommandHandlers.ServerForwardingHandler();
        CommandExecutionResult result = serverHandler.handle(context, commandName, argument, meta, sourceLabel);
        return result == CommandExecutionResult.CONTINUE;
    }
}
