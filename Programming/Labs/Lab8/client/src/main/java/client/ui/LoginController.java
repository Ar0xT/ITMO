package client.ui;

import client.NonBlockingTcpClient;
import client.utilities.PasswordHasher;
import common.commandsabstraction.CommandRequest;
import common.commandsabstraction.CommandResult;
import common.network.AuthCredentials;
import common.network.CommandPacket;
import common.network.ProtocolConstants;
import common.network.ResponsePacket;
import client.ui.i18n.UiText;

import java.util.Locale;
import java.util.Set;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;

public class LoginController {
    private static final Set<String> THEME_CLASSES = Set.of("theme-light", "theme-dark");

    @FXML private StackPane root;
    @FXML private Label titleLabel;
    @FXML private Label subtitleLabel;
    @FXML private Label usernameLabel;
    @FXML private Label passwordLabel;
    @FXML private Label statusLabel;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button registerButton;
    @FXML private Button langEnButton;
    @FXML private Button langRuButton;
    @FXML private Button langTrButton;
    @FXML private Button langHrButton;
    @FXML private Button langEsEcButton;
    @FXML private ToggleButton themeLightButton;
    @FXML private ToggleButton themeDarkButton;

    private final ToggleGroup themeToggleGroup = new ToggleGroup();
    private AppState appState;

    public void init(AppState appState) {
        this.appState = appState;
        applyTheme(appState.getTheme());
        syncLocaleSelection();
        applyLocale(appState.getLocale());

        appState.themeProperty().addListener((obs, oldVal, newVal) ->
                Platform.runLater(() -> {
                    applyTheme(newVal);
                    syncThemeSelection();
                }));
        appState.localeProperty().addListener((obs, oldVal, newVal) ->
                Platform.runLater(() -> {
                    applyLocale(newVal);
                    syncLocaleSelection();
                }));
    }

    @FXML
    private void initialize() {
        themeLightButton.setToggleGroup(themeToggleGroup);
        themeDarkButton.setToggleGroup(themeToggleGroup);

        themeLightButton.setOnAction(event -> {
            if (themeLightButton.isSelected() && appState != null) {
                appState.setTheme("light");
            }
        });
        themeDarkButton.setOnAction(event -> {
            if (themeDarkButton.isSelected() && appState != null) {
                appState.setTheme("dark");
            }
        });

        loginButton.setOnAction(event -> attemptLogin());
        registerButton.setOnAction(event -> attemptLogin());
        passwordField.setOnAction(event -> attemptLogin());
        usernameField.setOnAction(event -> passwordField.requestFocus());

        langEnButton.setOnAction(e -> setLocale(UiText.localeEn()));
        langRuButton.setOnAction(e -> setLocale(UiText.localeRu()));
        langTrButton.setOnAction(e -> setLocale(UiText.localeTr()));
        langHrButton.setOnAction(e -> setLocale(UiText.localeHr()));
        langEsEcButton.setOnAction(e -> setLocale(UiText.localeEsEc()));
    }

    private void setLocale(Locale locale) {
        if (appState != null) {
            appState.setLocale(locale);
        }
    }

    private void attemptLogin() {
        if (appState == null) {
            updateStatus(UiText.text(Locale.ENGLISH, UiText.Key.APP_STATE_NOT_READY), false);
            return;
        }

        String login = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();
        if (login.isEmpty() || password.isEmpty()) {
            updateStatus(UiText.text(appState.getLocale(), UiText.Key.REQUIRED_FIELDS), false);
            return;
        }

        setBusy(true);

        Task<CommandResult> task = new Task<>() {
            @Override
            protected CommandResult call() throws Exception {
                AuthCredentials credentials = new AuthCredentials(login, PasswordHasher.hashToHex(password));
                CommandPacket packet = new CommandPacket(
                        ProtocolConstants.GET_COMMANDS,
                        new CommandRequest("", credentials));
                NonBlockingTcpClient tcpClient = new NonBlockingTcpClient(appState.getHost(), appState.getPort());
                ResponsePacket response = tcpClient.send(packet, 15000);
                CommandResult result = response.getResult();
                if (result.isSuccess()) {
                    appState.setCredentials(credentials);
                }
                return result;
            }
        };

        task.setOnSucceeded(event -> {
            CommandResult result = task.getValue();
            updateStatus(result.getMessage(), result.isSuccess());
            setBusy(false);
        });

        task.setOnFailed(event -> {
            Throwable error = task.getException();
            updateStatus(UiText.text(appState.getLocale(), UiText.Key.LOGIN_FAILED_PREFIX) +
                    (error == null ? "Unknown error" : error.getMessage()), false);
            setBusy(false);
        });

        appState.getExecutor().execute(task);
    }

    private void setBusy(boolean busy) {
        loginButton.setDisable(busy);
        registerButton.setDisable(busy);
        usernameField.setDisable(busy);
        passwordField.setDisable(busy);
    }

    private void updateStatus(String message, boolean success) {
        statusLabel.getStyleClass().removeAll("status-success", "status-error");
        statusLabel.getStyleClass().add(success ? "status-success" : "status-error");
        statusLabel.setText(message);
    }

    private void applyTheme(String theme) {
        if (root != null) {
            root.getStyleClass().removeAll(THEME_CLASSES);
            root.getStyleClass().add("theme-" + theme);
        }
    }

    private void syncThemeSelection() {
        Platform.runLater(() -> {
            String theme = appState == null ? "light" : appState.getTheme();
            switch (theme) {
                case "dark" -> themeDarkButton.setSelected(true);
                default -> themeLightButton.setSelected(true);
            }
        });
    }

    private void applyLocale(Locale locale) {
        if (titleLabel != null) titleLabel.setText(UiText.text(locale, UiText.Key.TITLE_ENTER));
        if (subtitleLabel != null) subtitleLabel.setText(UiText.text(locale, UiText.Key.SUBTITLE_SIGN_IN));
        if (usernameLabel != null) usernameLabel.setText(UiText.text(locale, UiText.Key.USERNAME));
        if (passwordLabel != null) passwordLabel.setText(UiText.text(locale, UiText.Key.PASSWORD));
        if (loginButton != null) loginButton.setText(UiText.text(locale, UiText.Key.LOGIN));
        if (registerButton != null) registerButton.setText(UiText.text(locale, UiText.Key.REGISTER));
        if (themeLightButton != null) themeLightButton.setText(UiText.text(locale, UiText.Key.THEME_LIGHT));
        if (themeDarkButton != null) themeDarkButton.setText(UiText.text(locale, UiText.Key.THEME_DARK));
    }

    private void syncLocaleSelection() {
        Platform.runLater(() -> {
            if (appState == null) return;
            String code = UiText.codeFromLocale(appState.getLocale());
            Button selected = switch (code) {
                case "ru" -> langRuButton;
                case "tr" -> langTrButton;
                case "hr" -> langHrButton;
                case "es-EC" -> langEsEcButton;
                default -> langEnButton;
            };
            if (langEnButton != null) langEnButton.getStyleClass().remove("lang-selected");
            if (langRuButton != null) langRuButton.getStyleClass().remove("lang-selected");
            if (langTrButton != null) langTrButton.getStyleClass().remove("lang-selected");
            if (langHrButton != null) langHrButton.getStyleClass().remove("lang-selected");
            if (langEsEcButton != null) langEsEcButton.getStyleClass().remove("lang-selected");
            if (selected != null) selected.getStyleClass().add("lang-selected");
        });
    }
}
