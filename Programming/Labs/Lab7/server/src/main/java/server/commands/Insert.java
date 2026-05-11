package server.commands;

import server.commandbase.CommandWithKeyAndElement;
import common.commandsabstraction.CommandRequest;
import common.commandsabstraction.CommandResult;
import common.models.MusicBand;
import server.utilities.CollectionManager;
import server.utilities.db.BandInsertResult;
import server.utilities.db.BandRepository;

public class Insert extends CommandWithKeyAndElement {
    private final BandRepository bandRepository;

    public Insert(CollectionManager manager, BandRepository bandRepository) {
        super(manager);
        this.bandRepository = bandRepository;
    }

    @Override
    public boolean requiresBand() {
        return true;
    }

    @Override
    public boolean requiresArgument() {
        return true;
    }


    @Override
    protected CommandResult executeInternal(CommandRequest request) {
        String key = request.getArgument();
        if (manager.containsKey(key)) {
            return new CommandResult("Key '" + key + "' already exists.", false);
        }

        MusicBand band = MusicBand.createServerManagedCopy(request.getBand());
        String owner = request.getCredentials().getLogin();

        try {
            BandInsertResult result = bandRepository.insert(key, band, owner);
            band.setId(result.id());
            band.setCreationDate(result.creationDate());
            band.setOwnerLogin(owner);
            manager.add(key, band);
            return new CommandResult("Band added successfully with key '" + key + "'.", true);
        } catch (Exception e) {
            return new CommandResult("Failed to add band: " + e.getMessage(), false);
        }
    }

    @Override
    public String getDescription() {
        return "Add a new band with the specified key";
    }

    @Override
    public String getUsage() {
        return "insert <key>";
    }
}


