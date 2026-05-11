package client.commands.handlers;

import client.commands.ClientCommandHandler;
import client.commands.CommandExecutionContext;
import client.commands.CommandExecutionResult;
import common.models.MusicBand;
import common.network.CommandMeta;

/**
 * All command handlers in one place.
 */
public final class CommandHandlers {

    public static class ExitHandler implements ClientCommandHandler {
        @Override
        public boolean supports(String commandName) {
            return "exit".equals(commandName);
        }

        @Override
        public CommandExecutionResult handle(CommandExecutionContext context,
                                             String commandName,
                                             String argument,
                                             CommandMeta meta,
                                             String sourceLabel) {
            return context.handleExit(sourceLabel);
        }
    }

    public static class ExecuteScriptHandler implements ClientCommandHandler {
        @Override
        public boolean supports(String commandName) {
            return "execute_script".equals(commandName);
        }

        @Override
        public CommandExecutionResult handle(CommandExecutionContext context,
                                             String commandName,
                                             String argument,
                                             CommandMeta meta,
                                             String sourceLabel) {
            if (argument.isBlank()) {
                context.printUsage(meta);
                return CommandExecutionResult.CONTINUE;
            }
            boolean success = context.executeScript(argument);
            return success ? CommandExecutionResult.CONTINUE : CommandExecutionResult.STOP;
        }
    }

    public static class ServerForwardingHandler implements ClientCommandHandler {
        @Override
        public boolean supports(String commandName) {
            return true;
        }

        @Override
        public CommandExecutionResult handle(CommandExecutionContext context,
                                             String commandName,
                                             String argument,
                                             CommandMeta meta,
                                             String sourceLabel) {
            if (!context.hasCredentials()) {
                context.printAuthRequired(sourceLabel);
                return CommandExecutionResult.CONTINUE;
            }

            MusicBand band = null;

            if (meta.requiresBand()) {
                try {
                    band = context.readBand();
                } catch (Exception e) {
                    context.printInvalidBandData(sourceLabel, e.getMessage());
                    return CommandExecutionResult.CONTINUE;
                }
            }

            context.forwardToServer(commandName, argument, band);
            return CommandExecutionResult.CONTINUE;
        }
    }

}
