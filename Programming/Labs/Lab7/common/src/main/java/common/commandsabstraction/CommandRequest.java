package common.commandsabstraction;

import common.models.MusicBand;
import common.network.AuthCredentials;

import java.io.Serializable;

/**
 * This class contains the necessary data for all commands to work
 */
public class CommandRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String argument;
    private final MusicBand band;
    private final AuthCredentials credentials;

    public CommandRequest(String argument, MusicBand band, AuthCredentials credentials) {
        this.argument = argument;
        this.band = band;
        this.credentials = credentials;
    }

    public CommandRequest(String argument, AuthCredentials credentials) {
        this(argument, null, credentials);
    }

    public CommandRequest(String argument) {
        this(argument, null, null);
    }

    public String getArgument() {
        return this.argument;
    }

    public MusicBand getBand() {
        return this.band;
    }

    public AuthCredentials getCredentials() {
        return credentials;
    }
}
