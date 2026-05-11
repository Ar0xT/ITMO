package server;

import common.network.CommandPacket;
import common.network.PacketCodec;
import common.network.ProtocolConstants;
import common.network.tcp.FramedTcpIO;

import java.io.IOException;
import java.net.Socket;

public class RequestReader {
    public CommandPacket read(Socket socket) throws IOException, ClassNotFoundException {
        byte[] payload = FramedTcpIO.readFrame(socket.getInputStream(), ProtocolConstants.MAX_PACKET_BYTES);
        Object decoded = PacketCodec.deserialize(payload);
        if (!(decoded instanceof CommandPacket commandPacket)) {
            throw new IOException("Received unsupported packet type.");
        }

        return commandPacket;
    }
}


