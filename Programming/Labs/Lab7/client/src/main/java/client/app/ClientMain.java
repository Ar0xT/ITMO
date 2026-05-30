package client.app;

import client.ui.ClientUI;
import client.ui.ConsoleClientUI;

public class ClientMain {
    public static void main(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = 5000;
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
                if (port <= 0 || port > 65535) throw new NumberFormatException("out of range");
            } catch (NumberFormatException e) {
                System.err.println("Invalid port: " + args[1] + ". Use value in range 1..65535.");
                return;
            }
        }
        ClientUI ui = new ConsoleClientUI();
        new ClientApplication(host, port, ui).start();
    }
}