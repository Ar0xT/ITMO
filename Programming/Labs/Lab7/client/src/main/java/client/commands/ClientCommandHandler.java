package client.commands;

import common.network.CommandMeta;

public interface ClientCommandHandler {
    boolean supports(String commandName);

    CommandExecutionResult handle(CommandExecutionContext context,
                                  String commandName,
                                  String argument,
                                  CommandMeta meta,
                                  String sourceLabel);
}

