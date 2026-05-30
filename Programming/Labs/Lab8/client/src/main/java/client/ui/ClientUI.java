package client.ui;

import client.commands.CommandDispatcher;
import common.models.MusicBand;
import common.network.AuthCredentials;

public interface ClientUI {
    void println(String message);
    void print(String message);
    void printError(String message);
    String readLine();
    AuthCredentials promptCredentials();
    MusicBand promptMusicBand();
    void startInteractiveLoop(CommandDispatcher dispatcher);
    default void close() {}
}