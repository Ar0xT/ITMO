package server.commands;

import server.commandbase.CommandWIthIdAndElement;
import common.commandsabstraction.CommandRequest;
import common.commandsabstraction.CommandResult;
import common.models.MusicBand;
import server.utilities.CollectionManager;
import server.utilities.db.BandRepository;

public class Update extends CommandWIthIdAndElement {
    private final BandRepository bandRepository;

    public Update(CollectionManager manager, BandRepository bandRepository) {
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
        long id = Long.parseLong(request.getArgument().trim());
        MusicBand existing = manager.getBandById(id);
        if (existing == null) {
            return new CommandResult("No band found with ID " + id, false);
        }

        String owner = request.getCredentials().getLogin();
        if (existing.getOwnerLogin() == null || !existing.getOwnerLogin().equals(owner)) {
            return new CommandResult("Permission denied: you do not own band " + id + ".", false);
        }

        MusicBand replacement = MusicBand.createServerManagedCopy(request.getBand());

        try {
            boolean updated = bandRepository.update(id, replacement, owner);
            if (!updated) {
                return new CommandResult("Permission denied or band not found in DB.", false);
            }
        } catch (Exception e) {
            return new CommandResult("Failed to update band: " + e.getMessage(), false);
        }

        boolean updated = manager.replaceBandById(id, replacement);
        if (!updated) {
            return new CommandResult("Band updated in DB but missing in memory.", false);
        }

        return new CommandResult("Band with ID " + id + " updated.", true);

    }

    @Override
    public String getDescription() {
        return "Update a band by its ID";
    }

    @Override
    public String getUsage() {
        return "update <id>";
    }
}


