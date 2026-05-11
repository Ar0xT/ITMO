package server;

import common.network.PacketCodec;
import common.network.ResponsePacket;
import common.network.tcp.FramedTcpIO;

import java.io.IOException;
import java.net.Socket;

public class ResponseSender {
    public void send(Socket socket, ResponsePacket responsePacket) throws IOException {
        byte[] payload = PacketCodec.serialize(responsePacket);
        FramedTcpIO.writeFrame(socket.getOutputStream(), payload);
    }
}


