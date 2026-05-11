package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionAcceptor {
    public Socket accept(ServerSocket serverSocket) throws IOException {
        return serverSocket.accept();
    }
}


