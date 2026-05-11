package server.commands;

import server.commandbase.CommandWithOtherArgs;
import common.commandsabstraction.CommandResult;
import server.utilities.CollectionManager;
import common.commandsabstraction.CommandRequest;

public class CountByNumberOfParticipants extends CommandWithOtherArgs {

    public CountByNumberOfParticipants(CollectionManager manager) {
        super(manager);
    }

    @Override
    public boolean requiresArgument() {
        return true;
    }


    @Override
    public CommandResult executeInternal(CommandRequest request) {
        String argument = request.getArgument();
        try {
            long count = Long.parseLong(argument);
            int size = manager.getBandsByParticipants(count).size();
            return new CommandResult("Bands with " + count + " participants: " + size, true);
        } catch (NumberFormatException e) {
            return new CommandResult("Argument must be a number.", false);
        }
    }

    @Override
    public String getDescription() {
        return "Count elements with the given number of participants";
    }

    @Override
    public String getUsage() {
        return "count_by_number_of_participants <count>";
    }
}


