package server.commands;

import server.commandbase.CommandWithKey;
import common.commandsabstraction.CommandRequest;
import common.commandsabstraction.CommandResult;
import common.models.MusicBand;
import server.utilities.CollectionManager;
import server.utilities.db.BandRepository;

public class RemoveKey extends CommandWithKey {
    private final BandRepository bandRepository;

    public RemoveKey(CollectionManager manager, BandRepository bandRepository) {
        super(manager);
        this.bandRepository = bandRepository;
    }

    @Override
    protected CommandResult executeInternal(CommandRequest request) {
        String key = request.getArgument();
        MusicBand band = manager.get(key);
        if (band == null) {
            return new CommandResult("No element found with key '" + key + "'.", false);
        }

        String owner = request.getCredentials().getLogin();
        if (band.getOwnerLogin() == null || !band.getOwnerLogin().equals(owner)) {
            return new CommandResult("Permission denied: you do not own key '" + key + "'.", false);
        }

        try {
            int deleted = bandRepository.deleteByKey(key, owner);
            if (deleted == 0) {
                return new CommandResult("Permission denied or band not found in DB.", false);
            }
            manager.remove(key);
            return new CommandResult("Band with key '" + key + "' removed.", true);
        } catch (Exception e) {
            return new CommandResult("Failed to remove band: " + e.getMessage(), false);
        }
    }


    @Override
    public String getDescription() {
        return "Remove a band by its key";
    }

    @Override
    public String getUsage() {
        return "remove_key <key>";
    }
}


