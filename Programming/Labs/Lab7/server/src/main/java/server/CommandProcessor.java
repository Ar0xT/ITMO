package server;

import common.commandsabstraction.Command;
import common.commandsabstraction.CommandResult;
import common.network.CommandPacket;
import common.network.ProtocolConstants;
import server.utilities.AuthResult;
import server.utilities.AuthService;

public class CommandProcessor {
    private final ServerCommandRegistry commandRegistry;
    private final AuthService authService;

    public CommandProcessor(ServerCommandRegistry commandRegistry, AuthService authService) {
        this.commandRegistry = commandRegistry;
        this.authService = authService;
    }

    public CommandResult processClientCommand(CommandPacket commandPacket) {
        String commandName = commandPacket.getCommandName();

        AuthResult authResult;
        try {
            authResult = authService.authenticate(commandPacket.getRequest().getCredentials());
        } catch (Exception e) {
            return new CommandResult("Authentication failed: " + e.getMessage(), false);
        }
        if (!authResult.success()) {
            return new CommandResult(authResult.message(), false);
        }

        if (ProtocolConstants.GET_COMMANDS.equals(commandName)) {
            String message = authResult.created()
                    ? "User registered. Command metadata loaded."
                    : "Command metadata loaded.";
            return new CommandResult(message, true, commandRegistry.exportPublicCommandMeta());
        }

        if ("save".equals(commandName)) {
            return new CommandResult("Command 'save' is available only on the server side.", false);
        }

        if ("exit".equals(commandName)) {
            return new CommandResult("Exit is handled on the client side.", true);
        }

        Command command = commandRegistry.getPublicCommand(commandName);
        if (command == null) {
            return new CommandResult("Unknown command. Type 'help' for available commands.", false);
        }

        return command.execute(commandPacket.getRequest());
    }

    public CommandResult processServerCommand(String commandName) {
        Command command = commandRegistry.getServerOnlyCommand(commandName);
        if (command == null) {
            return new CommandResult("Unknown server command: " + commandName, false);
        }

        return command.execute(new common.commandsabstraction.CommandRequest(""));
    }
}
