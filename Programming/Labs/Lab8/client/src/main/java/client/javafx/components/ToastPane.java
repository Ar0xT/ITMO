package client.javafx.components;

import client.ui.i18n.UiText;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.Locale;

/**
 * Floating toast stack for the main view.  Add via {@link #show(String, Kind)}
 * or one of the kind-specific helpers.  Toasts auto-dismiss after
 * {@link #TOAST_LIFETIME_MILLIS} and can be clicked to dismiss early.
 * Stacks at the bottom-right of its parent.
 */
public class ToastPane extends VBox {

    public enum Kind { INFO, SUCCESS, WARNING, ERROR }

    public static final long TOAST_LIFETIME_MILLIS = 4000L;

    public ToastPane() {
        setSpacing(8);
        setAlignment(Pos.BOTTOM_RIGHT);
        setPickOnBounds(false);
        StackPane.setAlignment(this, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(this, new Insets(0, 20, 20, 0));
    }

    public void show(String message, Kind kind) {
        if (message == null || message.isBlank()) return;
        Platform.runLater(() -> {
            Node node = buildToastNode(message, kind == null ? Kind.INFO : kind);
            getChildren().add(node);

            node.setOpacity(0);
            node.setTranslateY(20);
            Transition fadeIn = new Transition() {
                { setCycleDuration(Duration.millis(220)); }
                @Override protected void interpolate(double frac) {
                    node.setOpacity(frac);
                    node.setTranslateY(20 * (1 - frac));
                }
            };
            fadeIn.play();

            new Thread(() -> {
                try { Thread.sleep(TOAST_LIFETIME_MILLIS); } catch (InterruptedException e) { return; }
                Platform.runLater(() -> dismiss(node));
            }, "toast-dismiss").start();
        });
    }

    public void info(String message)    { show(message, Kind.INFO); }
    public void success(String message) { show(message, Kind.SUCCESS); }
    public void warn(String message)    { show(message, Kind.WARNING); }
    public void error(String message)   { show(message, Kind.ERROR); }

    public void localized(Locale locale, UiText.Key key, String fallback, Kind kind) {
        String text = UiText.text(locale, key);
        show(text.isEmpty() ? fallback : text, kind);
    }

    private Node buildToastNode(String message, Kind kind) {
        Label label = new Label(message);
        label.setWrapText(true);
        label.getStyleClass().addAll("toast", "toast-" + kind.name().toLowerCase());
        label.setOnMouseClicked(e -> dismiss(label));
        return label;
    }

    private void dismiss(Node node) {
        if (!getChildren().contains(node)) return;
        Transition fadeOut = new Transition() {
            { setCycleDuration(Duration.millis(180)); }
            @Override protected void interpolate(double frac) {
                node.setOpacity(1 - frac);
                node.setTranslateY(20 * frac);
            }
        };
        fadeOut.setOnFinished(e -> getChildren().remove(node));
        fadeOut.play();
    }
}
