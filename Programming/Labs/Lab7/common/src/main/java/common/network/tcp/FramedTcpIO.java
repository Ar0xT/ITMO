package common.network.tcp;

import common.network.ProtocolConstants;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class FramedTcpIO {
    private FramedTcpIO() {
    }

    public static byte[] readFrame(InputStream inputStream, int maxBytes) throws IOException {
        DataInputStream input = new DataInputStream(inputStream);
        int length = input.readInt();
        if (length <= 0 || length > maxBytes) {
            throw new IOException("Invalid packet length: " + length);
        }

        byte[] payload = new byte[length];
        int offset = 0;
        while (offset < length) {
            int chunk = Math.min(ProtocolConstants.TCP_CHUNK_BYTES, length - offset);
            int read = input.read(payload, offset, chunk);
            if (read < 0) {
                throw new IOException("Connection closed while reading packet body.");
            }
            offset += read;
        }
        return payload;
    }

    public static void writeFrame(OutputStream outputStream, byte[] payload) throws IOException {
        DataOutputStream output = new DataOutputStream(outputStream);
        output.writeInt(payload.length);

        int offset = 0;
        while (offset < payload.length) {
            int chunk = Math.min(ProtocolConstants.TCP_CHUNK_BYTES, payload.length - offset);
            output.write(payload, offset, chunk);
            offset += chunk;
        }
        output.flush();
    }
}

