package client.app;

import client.javafx.MainController;
import client.ui.AppState;
import client.ui.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class JavaFxApp extends Application {
    private AppState appState;
    private Stage primaryStage;

    @Override
    public void init() {
        String host = "localhost";
        int port = 5000;
        if (!getParameters().getRaw().isEmpty()) {
            host = getParameters().getRaw().get(0);
        }
        if (getParameters().getRaw().size() > 1) {
            try {
                port = Integer.parseInt(getParameters().getRaw().get(1));
                if (port <= 0 || port > 65535) throw new NumberFormatException("out of range");
            } catch (NumberFormatException ignored) {
                port = 5000;
            }
        }
        this.appState = new AppState(host, port);
    }

    @Override
    public void start(Stage stage) throws IOException {
        this.primaryStage = stage;
        showLogin();
        stage.setTitle("BandManager");
        stage.show();
    }

    private void showLogin() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
        Parent root = loader.load();
        LoginController controller = loader.getController();
        controller.init(appState);

        Scene scene = new Scene(root, 1024, 768);
        primaryStage.setScene(scene);

        appState.credentialsProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && oldVal == null) {
                javafx.application.Platform.runLater(() -> {
                    try {
                        showMain();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }

    private void showMain() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
        Parent root = loader.load();
        MainController controller = loader.getController();
        controller.init(appState);

        Scene scene = new Scene(root, 1280, 800);
        primaryStage.setScene(scene);
    }

    @Override
    public void stop() {
        if (appState != null) {
            appState.shutdown();
        }
    }
}
