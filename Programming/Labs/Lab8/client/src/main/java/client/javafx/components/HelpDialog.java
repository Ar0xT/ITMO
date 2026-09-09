package client.javafx.components;

import client.ui.AppState;
import client.ui.i18n.UiText;
import common.network.CommandMeta;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * About / Help dialog. Lists every public command with its description
 * and usage, plus a small "About" section.
 */
public class HelpDialog extends Dialog<Void> {

    public HelpDialog(AppState appState, Map<String, CommandMeta> commands) {
        Locale locale = appState.getLocale();

        setTitle(UiText.text(locale, UiText.Key.HELP_TITLE));
        getDialogPane().getStyleClass().add("dialog-pane");
        getDialogPane().setPrefWidth(560);
        getDialogPane().setPrefHeight(560);

        VBox content = new VBox(16);
        content.setPadding(new Insets(24, 24, 16, 24));
        content.getStyleClass().add("dialog-content");

        Label title = new Label(UiText.text(locale, UiText.Key.HELP_TITLE));
        title.getStyleClass().add("dialog-title");
        title.setFont(Font.font("Inter", FontWeight.SEMI_BOLD, 18));
        content.getChildren().add(title);

        Label subtitle = new Label(UiText.text(locale, UiText.Key.HELP_SUBTITLE));
        subtitle.getStyleClass().add("field-label");
        subtitle.setWrapText(true);
        content.getChildren().add(subtitle);

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(320);
        scroll.getStyleClass().add("help-scroll");
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        VBox commandsList = new VBox(6);
        for (Map.Entry<String, CommandMeta> entry : commands.entrySet()) {
            String name = entry.getKey();
            String description = entry.getValue() == null ? "" : safeString(entry.getValue().description());
            String usage = entry.getValue() == null ? name : safeString(entry.getValue().usage());
            if (name == null || name.startsWith("__")) continue;
            commandsList.getChildren().add(buildCommandRow(name, description, usage));
        }
        scroll.setContent(commandsList);

        content.getChildren().add(scroll);

        Label about = new Label(UiText.text(locale, UiText.Key.HELP_ABOUT));
        about.getStyleClass().add("field-label");
        about.setWrapText(true);
        content.getChildren().add(about);

        getDialogPane().setContent(content);
        getDialogPane().getButtonTypes().add(javafx.scene.control.ButtonType.CLOSE);
    }

    public HelpDialog(AppState appState, List<CommandMeta> commandMetas) {
        this(appState, toMap(commandMetas));
    }

    private static Map<String, CommandMeta> toMap(List<CommandMeta> commandMetas) {
        java.util.Map<String, CommandMeta> map = new java.util.LinkedHashMap<>();
        for (CommandMeta meta : commandMetas) {
            if (meta == null) continue;
            String name = meta.usage() == null ? "" : meta.usage().split(" ", 2)[0];
            map.put(name, meta);
        }
        return map;
    }

    private Node buildCommandRow(String name, String description, String usage) {
        VBox card = new VBox(4);
        card.getStyleClass().add("help-command-card");

        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("help-command-name");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label usageLabel = new Label(usage);
        usageLabel.getStyleClass().add("help-command-usage");

        header.getChildren().addAll(nameLabel, spacer, usageLabel);

        Label descLabel = new Label(description);
        descLabel.getStyleClass().add("help-command-desc");
        descLabel.setWrapText(true);

        card.getChildren().addAll(header, descLabel);
        return card;
    }

    private static String safeString(String s) {
        return s == null ? "" : s;
    }
}
