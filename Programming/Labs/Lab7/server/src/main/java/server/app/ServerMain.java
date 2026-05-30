package server.app;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerMain {
    private static final Logger LOGGER = Logger.getLogger(ServerMain.class.getName());

    public static void main(String[] args) {
        int port = 5000;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
                if (port <= 0 || port > 65535) throw new NumberFormatException("out of range");
            } catch (NumberFormatException e) {
                LOGGER.log(Level.SEVERE, "Invalid port: " + args[0] + ". Use value in range 1..65535.");
                return;
            }
        }
        new ServerApplication(port).start();
    }
}