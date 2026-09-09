package client.javafx;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

/**
 * Theme propagation for {@link Dialog}s.  JavaFX renders each dialog in
 * its own stage and therefore loses the {@code theme-*} CSS class that
 * lives on the main scene's root.  Without it, the default modena look
 * bleeds through inside the dialog.  This helper copies the class over
 * to the dialog's root node and ensures the main stylesheet is loaded.
 */
public final class DialogThemer {

    private DialogThemer() {}

    public static void applyTheme(Dialog<?> dialog, String theme, String stylesheet) {
        if (dialog == null) return;
        DialogPane pane = dialog.getDialogPane();
        if (pane == null) return;
        if (stylesheet != null && !pane.getStylesheets().contains(stylesheet)) {
            pane.getStylesheets().add(stylesheet);
        }
        pane.getStyleClass().removeAll("theme-light", "theme-dark");
        pane.getStyleClass().add("theme-" + (theme == null ? "light" : theme));
        pane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                copyThemeToScene(newScene, theme);
                if (stylesheet != null && !newScene.getStylesheets().contains(stylesheet)) {
                    newScene.getStylesheets().add(stylesheet);
                }
            }
        });
        Scene current = pane.getScene();
        if (current != null) copyThemeToScene(current, theme);
    }

    private static void copyThemeToScene(Scene scene, String theme) {
        if (scene == null || scene.getRoot() == null) return;
        Node root = scene.getRoot();
        root.getStyleClass().removeAll("theme-light", "theme-dark");
        root.getStyleClass().add("theme-" + (theme == null ? "light" : theme));
    }
}
