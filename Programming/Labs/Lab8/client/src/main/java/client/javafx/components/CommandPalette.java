package client.javafx.components;

import client.javafx.DialogThemer;
import client.javafx.network.BandService;
import client.ui.AppState;
import client.ui.i18n.UiText;
import common.commandsabstraction.CommandResult;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Modal command palette exposing every server command to the user.
 * Selecting a command either runs it directly (if it takes no input)
 * or first asks for the required argument.
 */
public class CommandPalette extends Dialog<Void> {

    private record Entry(String commandName, UiText.Key labelKey) {}

    private static final List<Entry> ENTRIES = List.of(
            new Entry("help",                                  UiText.Key.COMMAND_HELP),
            new Entry("info",                                  UiText.Key.COMMAND_INFO),
            new Entry("show",                                  UiText.Key.COMMAND_SHOW),
            new Entry("clear",                                 UiText.Key.COMMAND_CLEAR),
            new Entry("remove_key",                            UiText.Key.COMMAND_REMOVE_KEY),
            new Entry("remove_greater_key",                    UiText.Key.COMMAND_REMOVE_GREATER_KEY),
            new Entry("count_by_number_of_participants",       UiText.Key.COMMAND_COUNT_BY_PARTICIPANTS),
            new Entry("group_counting_by_name",                UiText.Key.COMMAND_GROUP_BY_NAME),
            new Entry("print_field_descending_singles_count",  UiText.Key.COMMAND_PRINT_SINGLES_DESC)
    );

    private final AppState appState;
    private final BandService service;
    private final ListView<Entry> list;
    private final Consumer<CommandResult> onResult;

    public CommandPalette(AppState appState, BandService service, Consumer<CommandResult> onResult) {
        this.appState = appState;
        this.service = service;
        this.onResult = onResult;

        Locale locale = appState.getLocale();
        setTitle(UiText.text(locale, UiText.Key.COMMANDS));
        getDialogPane().getStyleClass().add("dialog-pane");
        getDialogPane().setPrefSize(520, 460);

        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        list = new ListView<>();
        ObservableList<Entry> items = FXCollections.observableArrayList(ENTRIES);
        list.setItems(items);
        list.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Entry item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(UiText.text(locale, item.labelKey));
                    getStyleClass().setAll("command-palette-row");
                }
            }
        });
        list.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Entry selected = list.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    runEntry(selected);
                }
            }
        });

        VBox content = new VBox(8);
        content.setPadding(new Insets(16));
        Label header = new Label(UiText.text(locale, UiText.Key.COMMANDS));
        header.getStyleClass().add("dialog-title");

        Button runButton = new Button(UiText.text(locale, UiText.Key.RUN));
        runButton.getStyleClass().add("dialog-button-save");
        runButton.setOnAction(e -> {
            Entry selected = list.getSelectionModel().getSelectedItem();
            if (selected != null) {
                runEntry(selected);
            }
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox footer = new HBox(8, spacer, runButton);
        footer.setAlignment(Pos.CENTER_RIGHT);

        content.getChildren().addAll(header, list, footer);
        VBox.setVgrow(list, Priority.ALWAYS);
        getDialogPane().setContent(content);

        list.getSelectionModel().selectFirst();
    }

    private void runEntry(Entry entry) {
        Locale locale = appState.getLocale();
        switch (entry.commandName) {
            case "help" -> dispatch(service.help());
            case "info" -> dispatch(service.info());
            case "show" -> dispatch(service.show());
            case "clear" -> confirmAndRun(locale, () -> dispatch(service.clear()));
            case "remove_key" -> promptAndRun(locale, UiText.Key.KEY_LABEL, key -> dispatch(service.removeKey(key)));
            case "remove_greater_key" -> promptAndRun(locale, UiText.Key.KEY_LABEL, key -> dispatch(service.removeGreaterKey(key)));
            case "count_by_number_of_participants" -> promptAndRun(locale, UiText.Key.PARTICIPANTS, value -> {
                try {
                    long n = Long.parseLong(value);
                    dispatch(service.countByNumberOfParticipants(n));
                } catch (NumberFormatException nfe) {
                    setStatus(UiText.text(locale, UiText.Key.OPERATION_FAILED) + "not a number", "error");
                }
            });
            case "group_counting_by_name" -> dispatch(service.groupCountingByName());
            case "print_field_descending_singles_count" -> dispatch(service.printFieldDescendingSinglesCount());
        }
    }

    private void confirmAndRun(Locale locale, Runnable action) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                UiText.text(locale, UiText.Key.CONFIRM_DELETE_MESSAGE),
                ButtonType.YES, ButtonType.NO);
        alert.setTitle(UiText.text(locale, UiText.Key.CONFIRM_DELETE_TITLE));
        DialogThemer.applyTheme(alert, appState.getTheme(), "/css/main.css");
        alert.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) action.run();
        });
    }

    private void promptAndRun(Locale locale, UiText.Key labelKey, Consumer<String> action) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(UiText.text(locale, UiText.Key.COMMANDS));
        dialog.setHeaderText(UiText.text(locale, labelKey));
        dialog.setContentText(UiText.text(locale, labelKey) + ":");
        DialogThemer.applyTheme(dialog, appState.getTheme(), "/css/main.css");
        dialog.showAndWait().ifPresent(action);
    }

    private void dispatch(java.util.concurrent.CompletableFuture<CommandResult> future) {
        future.whenComplete((result, ex) -> Platform.runLater(() -> {
            if (ex != null) {
                setStatus(UiText.text(appState.getLocale(), UiText.Key.OPERATION_FAILED) + ex.getMessage(), "error");
                return;
            }
            if (result == null) {
                setStatus(UiText.text(appState.getLocale(), UiText.Key.OPERATION_FAILED) + "no result", "error");
                return;
            }
            if (!result.isSuccess()) {
                setStatus(UiText.text(appState.getLocale(), UiText.Key.OPERATION_FAILED) + result.getMessage(), "error");
            } else {
                setStatus(UiText.text(appState.getLocale(), UiText.Key.OPERATION_SUCCESS) + " — " + result.getMessage(), "success");
            }
            if (onResult != null) onResult.accept(result);
            hide();
        }));
    }

    private void setStatus(String message, String kind) {
        appState.setStatus(message, kind);
    }
}
