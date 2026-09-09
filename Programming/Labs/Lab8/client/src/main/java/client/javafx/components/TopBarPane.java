package client.javafx.components;

import client.ui.AppState;
import client.ui.i18n.UiText;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Locale;

public class TopBarPane extends HBox {

    private final AppState appState;

    private final HBox syncBadge;
    private final Circle syncPulse;
    private final Label syncLabel;
    private final Region spacer;
    private final ThemeTogglePane themeToggle;
    private final Button commandsButton;
    private final Text commandsIcon;
    private final Label commandsLabel;
    private final Button addButton;
    private final Text addIcon;
    private final Label addLabel;

    private Timeline pulseAnim;
    private AppState.ConnectionState currentState = AppState.ConnectionState.CONNECTING;

    public TopBarPane(AppState appState) {
        this.appState = appState;
        getStyleClass().add("top-bar-pane");
        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(0, 20, 0, 20));

        syncBadge = new HBox(6);
        syncBadge.getStyleClass().add("sync-badge");
        syncBadge.setAlignment(Pos.CENTER_LEFT);
        syncPulse = new Circle(4);
        syncPulse.getStyleClass().add("sync-pulse");
        syncLabel = new Label();
        syncLabel.getStyleClass().add("sync-label");
        syncBadge.getChildren().addAll(syncPulse, syncLabel);

        spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        themeToggle = new ThemeTogglePane(appState);

        commandsButton = new Button();
        commandsButton.getStyleClass().add("commands-button");
        commandsIcon = new Text("⌘");
        commandsIcon.getStyleClass().add("commands-button-icon");
        commandsLabel = new Label();
        commandsLabel.getStyleClass().add("commands-button-label");
        HBox commandsContent = new HBox(6, commandsIcon, commandsLabel);
        commandsContent.setAlignment(Pos.CENTER_LEFT);
        commandsButton.setGraphic(commandsContent);

        addButton = new Button();
        addButton.getStyleClass().add("add-button");
        addIcon = new Text("+");
        addIcon.getStyleClass().add("add-button-icon");
        addLabel = new Label();
        addLabel.getStyleClass().add("add-button-label");
        HBox addContent = new HBox(6, addIcon, addLabel);
        addContent.setAlignment(Pos.CENTER_LEFT);
        addButton.setGraphic(addContent);

        getChildren().addAll(syncBadge, spacer, themeToggle, commandsButton, addButton);

        applyLocale(appState.getLocale());
        applyTheme(appState.getTheme());

        appState.localeProperty().addListener((obs, oldVal, newVal) ->
                javafx.application.Platform.runLater(() -> applyLocale(newVal)));
        appState.themeProperty().addListener((obs, oldVal, newVal) ->
                javafx.application.Platform.runLater(() -> applyTheme(newVal)));
        appState.connectionStateProperty().addListener((obs, oldVal, newVal) ->
                javafx.application.Platform.runLater(this::updateConnectionLabel));
    }

    public void applyLocale(Locale locale) {
        addLabel.setText(UiText.text(locale, UiText.Key.ADD));
        commandsLabel.setText(UiText.text(locale, UiText.Key.COMMANDS));
        updateConnectionLabel();
    }

    public void updateConnectionLabel() {
        Locale locale = appState.getLocale();
        AppState.ConnectionState state = appState.getConnectionState();
        String text = switch (state) {
            case CONNECTED    -> UiText.text(locale, UiText.Key.CONN_CONNECTED);
            case CONNECTING   -> UiText.text(locale, UiText.Key.CONN_CONNECTING);
            case DISCONNECTED -> UiText.text(locale, UiText.Key.CONN_DISCONNECTED);
        };
        syncLabel.setText(text);
        syncBadge.getStyleClass().removeAll("connected", "connecting", "disconnected");
        syncBadge.getStyleClass().add(state.name().toLowerCase());
        currentState = state;
        updatePulseAnimation();
    }

    private void updatePulseAnimation() {
        if (pulseAnim != null) {
            pulseAnim.stop();
            pulseAnim = null;
        }
        if (currentState == AppState.ConnectionState.CONNECTED) {
            pulseAnim = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(syncPulse.scaleXProperty(), 1.0), new KeyValue(syncPulse.scaleYProperty(), 1.0)),
                    new KeyFrame(Duration.millis(900), new KeyValue(syncPulse.scaleXProperty(), 1.35), new KeyValue(syncPulse.scaleYProperty(), 1.35)),
                    new KeyFrame(Duration.millis(1800), new KeyValue(syncPulse.scaleXProperty(), 1.0), new KeyValue(syncPulse.scaleYProperty(), 1.0))
            );
            pulseAnim.setCycleCount(Timeline.INDEFINITE);
            pulseAnim.setAutoReverse(false);
            pulseAnim.play();
        } else if (currentState == AppState.ConnectionState.CONNECTING) {
            pulseAnim = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(syncPulse.opacityProperty(), 0.4), new KeyValue(syncPulse.scaleXProperty(), 0.85), new KeyValue(syncPulse.scaleYProperty(), 0.85)),
                    new KeyFrame(Duration.millis(600), new KeyValue(syncPulse.opacityProperty(), 1.0), new KeyValue(syncPulse.scaleXProperty(), 1.15), new KeyValue(syncPulse.scaleYProperty(), 1.15)),
                    new KeyFrame(Duration.millis(1200), new KeyValue(syncPulse.opacityProperty(), 0.4), new KeyValue(syncPulse.scaleXProperty(), 0.85), new KeyValue(syncPulse.scaleYProperty(), 0.85))
            );
            pulseAnim.setCycleCount(Timeline.INDEFINITE);
            pulseAnim.play();
        } else {
            syncPulse.setScaleX(1);
            syncPulse.setScaleY(1);
            syncPulse.setOpacity(0.6);
        }
    }

    public void applyTheme(String theme) {
        getStyleClass().removeAll("theme-light", "theme-dark");
        getStyleClass().add("theme-" + theme);
    }

    public Button getAddButton() {
        return addButton;
    }

    public Button getCommandsButton() {
        return commandsButton;
    }
}
