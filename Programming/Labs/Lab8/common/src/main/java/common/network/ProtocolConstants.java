package common.network;

public final class ProtocolConstants {
    public static final String GET_COMMANDS = "__get_commands__";
    public static final int MAX_PACKET_BYTES = 512 * 1024 * 1024;
    public static final int TCP_CHUNK_BYTES = 8192;

    private ProtocolConstants() {
    }
}

