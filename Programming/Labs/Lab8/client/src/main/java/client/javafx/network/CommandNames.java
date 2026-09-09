package client.javafx.network;

/**
 * Server command names exposed by {@code ServerCommandRegistry}.
 * Centralised so we don't sprinkle magic strings across the client.
 */
public final class CommandNames {
    public static final String HELP = "help";
    public static final String INFO = "info";
    public static final String SHOW = "show";
    public static final String INSERT = "insert";
    public static final String UPDATE = "update";
    public static final String REMOVE_KEY = "remove_key";
    public static final String REMOVE_GREATER = "remove_greater";
    public static final String REMOVE_GREATER_KEY = "remove_greater_key";
    public static final String REMOVE_LOWER = "remove_lower";
    public static final String CLEAR = "clear";
    public static final String GROUP_COUNTING_BY_NAME = "group_counting_by_name";
    public static final String COUNT_BY_PARTICIPANTS = "count_by_number_of_participants";
    public static final String PRINT_FIELD_DESCENDING_SINGLES_COUNT = "print_field_descending_singles_count";

    private CommandNames() {}
}
