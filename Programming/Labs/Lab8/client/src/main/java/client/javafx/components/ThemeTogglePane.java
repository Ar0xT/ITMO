package client.javafx.components;

import client.ui.AppState;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

public class ThemeTogglePane extends HBox {

    private final AppState appState;
    private final ToggleGroup themeGroup;
    private final ToggleButton lightButton;
    private final ToggleButton darkButton;

    public ThemeTogglePane(AppState appState) {
        this.appState = appState;
        getStyleClass().add("theme-toggle-pane");
        setAlignment(javafx.geometry.Pos.CENTER);

        themeGroup = new ToggleGroup();

        lightButton = new ToggleButton("☀");
        lightButton.getStyleClass().add("theme-toggle-button");
        lightButton.setToggleGroup(themeGroup);
        lightButton.setUserData("light");
        lightButton.setOnAction(e -> {
            if (lightButton.isSelected()) {
                appState.setTheme("light");
            }
        });

        darkButton = new ToggleButton("🌙");
        darkButton.getStyleClass().add("theme-toggle-button");
        darkButton.setToggleGroup(themeGroup);
        darkButton.setUserData("dark");
        darkButton.setOnAction(e -> {
            if (darkButton.isSelected()) {
                appState.setTheme("dark");
            }
        });

        getChildren().addAll(lightButton, darkButton);

        syncToggle(appState.getTheme());

        appState.themeProperty().addListener((obs, oldVal, newVal) ->
                javafx.application.Platform.runLater(() -> syncToggle(newVal)));
    }

    private void syncToggle(String theme) {
        switch (theme) {
            case "dark" -> darkButton.setSelected(true);
            default -> lightButton.setSelected(true);
        }
    }
}
