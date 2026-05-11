package server;

import common.commandsabstraction.Command;
import common.network.CommandMeta;
import server.commands.*;
import server.utilities.AuthService;
import server.utilities.CollectionManager;
import server.utilities.db.BandRepository;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ServerCommandRegistry {
    private final Map<String, Command> publicCommands = new LinkedHashMap<>();
    private final Map<String, Command> serverOnlyCommands = new LinkedHashMap<>();

    public ServerCommandRegistry(CollectionManager manager,
                                 BandRepository bandRepository,
                                 AuthService authService) {
        publicCommands.put("help", new Help(publicCommands));
        publicCommands.put("info", new Info(manager));
        publicCommands.put("show", new Show(manager));
        publicCommands.put("clear", new Clear(manager, bandRepository));

        publicCommands.put("insert", new Insert(manager, bandRepository));
        publicCommands.put("update", new Update(manager, bandRepository));
        publicCommands.put("remove_greater", new RemoveGreater(manager, bandRepository));
        publicCommands.put("remove_lower", new RemoveLower(manager, bandRepository));

        publicCommands.put("remove_key", new RemoveKey(manager, bandRepository));
        publicCommands.put("remove_greater_key", new RemoveGreaterKey(manager, bandRepository));

        publicCommands.put("group_counting_by_name", new GroupCountingByName(manager));
        publicCommands.put("count_by_number_of_participants", new CountByNumberOfParticipants(manager));
        publicCommands.put("print_field_descending_singles_count", new PrintFieldDescendingSinglesCount(manager));

        // no server-only commands for DB-backed storage
    }

    public Command getPublicCommand(String commandName) {
        return publicCommands.get(commandName);
    }

    public Command getServerOnlyCommand(String commandName) {
        return serverOnlyCommands.get(commandName);
    }

    public Map<String, CommandMeta> exportPublicCommandMeta() {
        return publicCommands.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new CommandMeta(
                                entry.getValue().getUsage(),
                                entry.getValue().getDescription(),
                                entry.getValue().requiresArgument(),
                                entry.getValue().requiresBand()
                        ),
                        (left, right) -> left,
                        LinkedHashMap::new
                ));
    }
}
