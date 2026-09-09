package client.javafx.components;

import client.ui.AppState;
import client.ui.i18n.UiText;
import common.models.Coordinates;
import common.models.MusicBand;
import common.models.MusicGenre;
import common.models.Studio;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

public class BandEditDialog extends Dialog<MusicBand> {

    private final AppState appState;
    private final MusicBand existing;
    private final TextField nameField = new TextField();
    private final TextField xField = new TextField();
    private final TextField yField = new TextField();
    private final TextField participantsField = new TextField();
    private final TextField singlesField = new TextField();
    private final DatePicker datePicker = new DatePicker();
    private final ComboBox<MusicGenre> genreCombo = new ComboBox<>();
    private final TextField studioField = new TextField();
    private final Label errorLabel = new Label();

    public BandEditDialog(AppState appState, MusicBand existing) {
        this.appState = appState;
        this.existing = existing;

        setTitle(existing == null ? "New Band" : "Edit Band");
        getDialogPane().getStyleClass().add("dialog-pane");
        getDialogPane().setPrefWidth(420);

        Locale locale = appState.getLocale();

        Button saveButton = new Button(UiText.text(locale, UiText.Key.SAVE));
        saveButton.getStyleClass().add("dialog-button-save");
        saveButton.setDefaultButton(true);

        Button cancelButton = new Button(UiText.text(locale, UiText.Key.CANCEL));
        cancelButton.getStyleClass().add("dialog-button-cancel");
        cancelButton.setCancelButton(true);

        getDialogPane().getButtonTypes().addAll(
                new javafx.scene.control.ButtonType("Save", javafx.scene.control.ButtonBar.ButtonData.OK_DONE),
                new javafx.scene.control.ButtonType("Cancel", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE)
        );
        getDialogPane().lookupButton(
                getDialogPane().getButtonTypes().get(0)).setVisible(false);
        getDialogPane().lookupButton(
                getDialogPane().getButtonTypes().get(1)).setVisible(false);

        VBox content = new VBox(12);
        content.getStyleClass().add("dialog-content");
        content.setPadding(new Insets(16));

        HBox header = new HBox(8);
        header.getStyleClass().add("dialog-header");
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label(existing == null
                ? UiText.text(locale, UiText.Key.ADD) : UiText.text(locale, UiText.Key.EDIT));
        title.getStyleClass().add("dialog-title");
        Button closeBtn = new Button("✕");
        closeBtn.getStyleClass().add("dialog-close-button");
        closeBtn.setOnAction(e -> hide());
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(title, spacer, closeBtn);
        getDialogPane().setHeader(null);

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(10);
        form.setPadding(new Insets(0));

        form.add(labelFor(UiText.text(locale, UiText.Key.FIELD_NAME)), 0, 0);
        form.add(nameField, 0, 1);
        GridPane.setHgrow(nameField, Priority.ALWAYS);

        form.add(labelFor(UiText.text(locale, UiText.Key.FIELD_X) + " ( > -431)"), 0, 2);
        form.add(xField, 0, 3);

        form.add(labelFor(UiText.text(locale, UiText.Key.FIELD_Y)), 0, 4);
        form.add(yField, 0, 5);

        form.add(labelFor(UiText.text(locale, UiText.Key.FIELD_PARTICIPANTS) + " ( > 0)"), 0, 6);
        form.add(participantsField, 0, 7);

        form.add(labelFor(UiText.text(locale, UiText.Key.FIELD_SINGLES) + " ( > 0)"), 0, 8);
        form.add(singlesField, 0, 9);

        form.add(labelFor(UiText.text(locale, UiText.Key.FIELD_DATE)), 0, 10);
        form.add(datePicker, 0, 11);

        form.add(labelFor(UiText.text(locale, UiText.Key.FIELD_GENRE)), 0, 12);
        form.add(genreCombo, 0, 13);

        form.add(labelFor(UiText.text(locale, UiText.Key.FIELD_STUDIO)), 0, 14);
        form.add(studioField, 0, 15);

        errorLabel.getStyleClass().add("form-error");
        errorLabel.setWrapText(true);

        HBox footer = new HBox(8);
        footer.getStyleClass().add("dialog-footer");
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.getChildren().addAll(cancelButton, saveButton);

        content.getChildren().addAll(header, form, errorLabel, footer);

        styleField(nameField);
        styleField(xField);
        styleField(yField);
        styleField(participantsField);
        styleField(singlesField);
        styleField(studioField);
        datePicker.getStyleClass().add("form-field");
        datePicker.setMaxWidth(Double.MAX_VALUE);
        genreCombo.getStyleClass().add("form-field");
        genreCombo.setItems(FXCollections.observableArrayList(MusicGenre.values()));
        genreCombo.setMaxWidth(Double.MAX_VALUE);

        getDialogPane().setContent(content);
        getDialogPane().setExpandableContent(null);

        if (existing != null) {
            nameField.setText(existing.getName());
            xField.setText(formatDecimal(existing.getCoordinates().getX()));
            yField.setText(String.valueOf(existing.getCoordinates().getY()));
            participantsField.setText(String.valueOf(existing.getNumberOfParticipants()));
            singlesField.setText(existing.getSinglesCount() == null ? "" : String.valueOf(existing.getSinglesCount()));
            datePicker.setValue(existing.getEstablishmentDate());
            genreCombo.setValue(existing.getGenre());
            studioField.setText(existing.getStudio() == null ? "" : existing.getStudio().getName());
        } else {
            xField.setText("0");
            yField.setText("0");
            participantsField.setText("1");
            singlesField.setText("1");
            genreCombo.setValue(MusicGenre.POST_ROCK);
        }

        cancelButton.setOnAction(e -> {
            hide();
            setResult(null);
        });
        saveButton.setOnAction(e -> {
            MusicBand result = tryBuild();
            if (result != null) {
                setResult(result);
                hide();
            }
        });

        setResultConverter(dialogButton -> {
            if (dialogButton == null) return null;
            if (dialogButton.getButtonData() == javafx.scene.control.ButtonBar.ButtonData.OK_DONE) {
                return tryBuild();
            }
            return null;
        });
    }

    private Label labelFor(String text) {
        Label lbl = new Label(text);
        lbl.getStyleClass().add("form-label");
        return lbl;
    }

    private void styleField(TextField field) {
        field.getStyleClass().add("form-field");
        field.setMaxWidth(Double.MAX_VALUE);
        field.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                Platform.runLater(field::selectAll);
            }
        });
    }

    private String formatDecimal(double value) {
        return UiText.decimalFormat(appState.getLocale(), 0, 6).format(value);
    }

    private MusicBand tryBuild() {
        Locale locale = appState.getLocale();
        try {
            String name = nameField.getText() == null ? "" : nameField.getText().trim();
            if (name.isEmpty()) {
                errorLabel.setText(UiText.text(locale, UiText.Key.VALIDATION_NAME_REQUIRED));
                return null;
            }

            double x;
            try {
                x = Double.parseDouble(xField.getText().trim().replace(',', '.'));
            } catch (NumberFormatException e) {
                errorLabel.setText(UiText.text(locale, UiText.Key.VALIDATION_X_NOT_NUMBER));
                return null;
            }
            if (x <= -431) {
                errorLabel.setText(UiText.text(locale, UiText.Key.VALIDATION_X_RANGE));
                return null;
            }

            Integer y;
            try {
                y = Integer.parseInt(yField.getText().trim());
            } catch (NumberFormatException e) {
                errorLabel.setText(UiText.text(locale, UiText.Key.VALIDATION_Y_NOT_INTEGER));
                return null;
            }

            long participants;
            try {
                participants = Long.parseLong(participantsField.getText().trim());
            } catch (NumberFormatException e) {
                errorLabel.setText(UiText.text(locale, UiText.Key.VALIDATION_PARTICIPANTS_NOT_NUMBER));
                return null;
            }
            if (participants <= 0) {
                errorLabel.setText(UiText.text(locale, UiText.Key.VALIDATION_PARTICIPANTS_RANGE));
                return null;
            }

            Integer singles = null;
            String singlesText = singlesField.getText() == null ? "" : singlesField.getText().trim();
            if (!singlesText.isEmpty()) {
                try {
                    singles = Integer.parseInt(singlesText);
                } catch (NumberFormatException e) {
                    errorLabel.setText(UiText.text(locale, UiText.Key.VALIDATION_SINGLES_NOT_NUMBER));
                    return null;
                }
                if (singles <= 0) {
                    errorLabel.setText(UiText.text(locale, UiText.Key.VALIDATION_SINGLES_RANGE));
                    return null;
                }
            }

            LocalDate establishmentDate = datePicker.getValue();
            MusicGenre genre = genreCombo.getValue();
            String studioName = studioField.getText() == null ? "" : studioField.getText().trim();

            MusicBand band = new MusicBand(
                    name,
                    new Coordinates(x, y),
                    participants,
                    singles,
                    establishmentDate,
                    genre,
                    studioName.isEmpty() ? null : new Studio(studioName)
            );
            errorLabel.setText("");
            return band;
        } catch (Exception e) {
            errorLabel.setText(UiText.text(locale, UiText.Key.VALIDATION_GENERIC_ERROR) + e.getMessage());
            return null;
        }
    }
}
