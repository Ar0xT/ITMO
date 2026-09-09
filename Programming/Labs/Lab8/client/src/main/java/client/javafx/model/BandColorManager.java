package client.javafx.model;

import client.ui.AppState;
import javafx.collections.ObservableMap;

public class BandColorManager {

    private static final String[] PREDEFINED_COLORS = {
            "#0052CC",
            "#00875A",
            "#DE350B",
            "#FF991F",
            "#6554C0",
            "#00B8D9",
            "#36B37E"
    };

    private final AppState appState;

    public BandColorManager(AppState appState) {
        this.appState = appState;
    }

    public String getColorForUser(String login) {
        if (login == null || login.isBlank()) {
            return "#94A3B8";
        }
        ObservableMap<String, String> map = appState.getUserColorMap();
        if (map.containsKey(login)) {
            return map.get(login);
        }
        int colorIndex = map.size() % PREDEFINED_COLORS.length;
        String color = PREDEFINED_COLORS[colorIndex];
        map.put(login, color);
        return color;
    }

    public static String[] getAllColors() {
        return PREDEFINED_COLORS.clone();
    }
}
