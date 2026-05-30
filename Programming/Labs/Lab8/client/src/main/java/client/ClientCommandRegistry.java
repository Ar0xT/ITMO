package client;

import common.network.CommandMeta;

import java.util.LinkedHashMap;
import java.util.Map;

public class ClientCommandRegistry {
    private final Map<String, CommandMeta> commands = new LinkedHashMap<>();

    public ClientCommandRegistry() {
        // Local commands that dont need server
        commands.put("execute_script", new CommandMeta("execute_script <file_name>", "Execute commands from script", true, false));
        commands.put("exit", new CommandMeta("exit", "Exit client application", false, false));
    }

    public void applyServerCommands(Map<String, CommandMeta> serverCommands) {
        commands.entrySet().removeIf(entry -> !isClientLocal(entry.getKey()));
        if (serverCommands != null) {
            serverCommands.forEach(commands::putIfAbsent);
        }
    }

    private boolean isClientLocal(String commandName) {
        return "execute_script".equals(commandName) || "exit".equals(commandName);
    }

    public CommandMeta get(String commandName) {
        return commands.get(commandName);
    }

    public String buildHelpText() {
        StringBuilder help = new StringBuilder("Available commands:\n");
        commands.forEach((name, meta) -> help.append(" ")
                .append(name)
                .append(" - ")
                .append(meta.description())
                .append("\n    Usage: ")
                .append(meta.usage())
                .append("\n"));
        return help.toString();
    }
}
