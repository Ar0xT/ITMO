package client.app;

import client.ui.AppState;
import client.ui.LoginController;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class JavaFxApp extends Application {
    private AppState appState;

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
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginView.fxml"));
        Parent root = loader.load();
        LoginController controller = loader.getController();
        controller.init(appState);

        Scene scene = new Scene(root, 1024, 768);
        stage.setTitle("BandManager");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        if (appState != null) {
            appState.shutdown();
        }
    }
}

