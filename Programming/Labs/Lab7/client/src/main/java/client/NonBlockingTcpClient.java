package client;

import common.network.CommandPacket;
import common.network.PacketCodec;
import common.network.ProtocolConstants;
import common.network.ResponsePacket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * Non-blocking TCP client for sending commands to server.
 */
public class NonBlockingTcpClient {
    private static final int MAX_PACKET_BYTES = ProtocolConstants.MAX_PACKET_BYTES;

    private final String host;
    private final int port;

    public NonBlockingTcpClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Send a command packet and wait for response.
     */
    public ResponsePacket send(CommandPacket commandPacket, int timeoutMillis)
            throws IOException, ClassNotFoundException {

        try (SocketChannel channel = SocketChannel.open();
             Selector selector = Selector.open()) {

            connectToServer(channel, selector, timeoutMillis);
            sendCommand(channel, selector, commandPacket, timeoutMillis);
            return receiveResponse(channel, selector, timeoutMillis);
        }
    }

    private void connectToServer(SocketChannel channel, Selector selector, int timeoutMillis)
            throws IOException {
        channel.configureBlocking(false);
        channel.connect(new InetSocketAddress(host, port));
        channel.register(selector, SelectionKey.OP_CONNECT);
        waitForChannelReady(channel, selector, SelectionKey.OP_CONNECT, timeoutMillis, "connect");
    }

    private void sendCommand(SocketChannel channel, Selector selector, CommandPacket command, int timeoutMillis)
            throws IOException {
        byte[] payload = PacketCodec.serialize(command);

        ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
        lengthBuffer.putInt(payload.length).flip();
        ByteBuffer payloadBuffer = ByteBuffer.wrap(payload);

        channel.register(selector, SelectionKey.OP_WRITE);

        writeBufferCompletely(channel, selector, lengthBuffer, timeoutMillis, "write length");
        writeBufferCompletely(channel, selector, payloadBuffer, timeoutMillis, "write payload");
    }

    private void writeBufferCompletely(SocketChannel channel, Selector selector, ByteBuffer buffer,
                                       int timeoutMillis, String operationName) throws IOException {
        long deadline = System.currentTimeMillis() + timeoutMillis;

        while (buffer.hasRemaining()) {
            long remainingTime = deadline - System.currentTimeMillis();
            if (remainingTime <= 0 || !isChannelReady(selector, remainingTime, SelectionKey.OP_WRITE)) {
                throw new SocketTimeoutException("Timeout while " + operationName);
            }
            channel.write(buffer);
        }
    }

    private ResponsePacket receiveResponse(SocketChannel channel, Selector selector, int timeoutMillis)
            throws IOException, ClassNotFoundException {
        channel.register(selector, SelectionKey.OP_READ);

        ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
        readBufferCompletely(channel, selector, lengthBuffer, timeoutMillis, "read length");
        int payloadLength = lengthBuffer.flip().getInt();

        validatePayloadLength(payloadLength);

        ByteBuffer payloadBuffer = ByteBuffer.allocate(payloadLength);
        readBufferCompletely(channel, selector, payloadBuffer, timeoutMillis, "read payload");

        Object decoded = PacketCodec.deserialize(payloadBuffer.array());
        if (!(decoded instanceof ResponsePacket response)) {
            throw new IOException("Server sent unsupported packet type");
        }
        return response;
    }

    private void readBufferCompletely(SocketChannel channel, Selector selector, ByteBuffer buffer,
                                      int timeoutMillis, String operationName) throws IOException {
        long deadline = System.currentTimeMillis() + timeoutMillis;

        while (buffer.hasRemaining()) {
            long remainingTime = deadline - System.currentTimeMillis();
            if (remainingTime <= 0 || !isChannelReady(selector, remainingTime, SelectionKey.OP_READ)) {
                throw new SocketTimeoutException("Timeout while " + operationName);
            }
            int bytesRead = channel.read(buffer);
            if (bytesRead < 0) {
                throw new IOException("Server closed connection prematurely");
            }
        }
    }

    private void waitForChannelReady(SocketChannel channel, Selector selector, int interest,
                                     int timeoutMillis, String operationName) throws IOException {
        long deadline = System.currentTimeMillis() + timeoutMillis;

        while (true) {
            if (interest == SelectionKey.OP_CONNECT && channel.finishConnect()) {
                return;
            }

            long remainingTime = deadline - System.currentTimeMillis();
            if (remainingTime <= 0 || !isChannelReady(selector, remainingTime, interest)) {
                throw new SocketTimeoutException("Timeout while " + operationName);
            }
        }
    }

    private boolean isChannelReady(Selector selector, long waitMillis, int interest) throws IOException {
        int readyCount = selector.select(waitMillis);
        if (readyCount == 0) {
            return false;
        }

        Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
        while (keys.hasNext()) {
            SelectionKey key = keys.next();
            keys.remove();

            if (!key.isValid()) continue;

            boolean isReady = (interest == SelectionKey.OP_CONNECT && key.isConnectable())
                    || (interest == SelectionKey.OP_WRITE && key.isWritable())
                    || (interest == SelectionKey.OP_READ && key.isReadable());

            if (isReady) return true;
        }

        return false;
    }

    private void validatePayloadLength(int length) throws IOException {
        if (length <= 0 || length > MAX_PACKET_BYTES) {
            throw new IOException("Invalid payload length: " + length + " (max: " + MAX_PACKET_BYTES + ")");
        }
    }
}
