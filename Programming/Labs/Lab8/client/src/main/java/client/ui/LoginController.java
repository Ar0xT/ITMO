package client.ui;

import client.NonBlockingTcpClient;
import client.utilities.PasswordHasher;
import common.commandsabstraction.CommandRequest;
import common.commandsabstraction.CommandResult;
import common.network.AuthCredentials;
import common.network.CommandPacket;
import common.network.ProtocolConstants;
import common.network.ResponsePacket;
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
    private static final Set<String> THEME_CLASSES = Set.of("theme-light", "theme-dark", "theme-system");

    @FXML private StackPane root;
    @FXML private Label titleLabel;
    @FXML private Label usernameLabel;
    @FXML private Label passwordLabel;
    @FXML private Label statusLabel;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button langEnButton;
    @FXML private Button langRuButton;
    @FXML private Button langTrButton;
    @FXML private Button langHrButton;
    @FXML private Button langEsEcButton;
    @FXML private ToggleButton themeLightButton;
    @FXML private ToggleButton themeSystemButton;
    @FXML private ToggleButton themeDarkButton;

    private final ToggleGroup themeToggleGroup = new ToggleGroup();
    private AppState appState;

    public void init(AppState appState) {
        this.appState = appState;
        applyTheme(appState.getTheme());
    }

    @FXML
    private void initialize() {
        themeLightButton.setToggleGroup(themeToggleGroup);
        themeSystemButton.setToggleGroup(themeToggleGroup);
        themeDarkButton.setToggleGroup(themeToggleGroup);

        themeLightButton.setOnAction(event -> applyTheme("light"));
        themeSystemButton.setOnAction(event -> applyTheme("system"));
        themeDarkButton.setOnAction(event -> applyTheme("dark"));

        loginButton.setOnAction(event -> attemptLogin());
    }

    private void attemptLogin() {
        if (appState == null) {
            updateStatus("Application state is not ready.", false);
            return;
        }

        String login = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = passwordField.getText() == null ? "" : passwordField.getText();
        if (login.isEmpty() || password.isEmpty()) {
            updateStatus("Login and password are required.", false);
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
            updateStatus("Login failed: " + (error == null ? "Unknown error" : error.getMessage()), false);
            setBusy(false);
        });

        appState.getExecutor().execute(task);
    }

    private void setBusy(boolean busy) {
        loginButton.setDisable(busy);
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
        if (appState != null) {
            appState.setTheme(theme);
        }
        Platform.runLater(() -> {
            if ("dark".equals(theme)) {
                themeDarkButton.setSelected(true);
            } else if ("system".equals(theme)) {
                themeSystemButton.setSelected(true);
            } else {
                themeLightButton.setSelected(true);
            }
        });
    }
}

