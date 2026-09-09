package client.javafx.components;

import client.javafx.model.BandColorManager;
import client.ui.AppState;
import client.ui.i18n.UiText;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.util.Locale;

public class SidebarPane extends VBox {

    private static final String[] LOCALES = {"en", "ru", "tr", "hr", "es-EC"};

    private final AppState appState;
    private final BandColorManager colorManager;

    private final VBox logoBox;
    private final Text logoIcon;
    private final Label brandLabel;
    private final VBox userCard;
    private final Circle userAvatar;
    private final Text userIcon;
    private final Label welcomeLabel;
    private final Label usernameLabel;
    private final VBox navBox;
    private final HBox navDashboard;
    private final Text navDashboardIcon;
    private final Label navDashboardLabel;
    private final Region spacer;
    private final VBox footerBox;
    private final HBox languageHeader;
    private final Text langIcon;
    private final Label langHeaderLabel;
    private final HBox langGrid;
    private final ToggleGroup langGroup;
    private final Button logoutButton;
    private final Text logoutIcon;
    private final Label logoutLabel;

    public SidebarPane(AppState appState) {
        this.appState = appState;
        this.colorManager = new BandColorManager(appState);

        getStyleClass().add("sidebar-pane");
        setSpacing(16);
        setPadding(new Insets(20));
        setAlignment(Pos.TOP_CENTER);

        logoBox = new VBox(4);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoIcon = new Text("🎵");
        logoIcon.getStyleClass().add("sidebar-logo-icon");
        brandLabel = new Label("BandManager");
        brandLabel.getStyleClass().add("sidebar-brand");
        HBox logoRow = new HBox(10, logoIcon, brandLabel);
        logoRow.setAlignment(Pos.CENTER_LEFT);
        logoBox.getChildren().add(logoRow);

        userCard = new VBox(2);
        userCard.getStyleClass().add("user-card");
        userCard.setAlignment(Pos.CENTER_LEFT);
        userAvatar = new Circle(20);
        userAvatar.getStyleClass().add("user-avatar");
        userIcon = new Text("👤");
        userIcon.getStyleClass().add("user-avatar-icon");
        StackPane avatarWrapper = new StackPane(userAvatar, userIcon);
        welcomeLabel = new Label();
        welcomeLabel.getStyleClass().add("user-welcome");
        usernameLabel = new Label();
        usernameLabel.getStyleClass().add("user-name");
        VBox userInfoBox = new VBox(0, welcomeLabel, usernameLabel);
        userInfoBox.setAlignment(Pos.CENTER_LEFT);
        HBox userCardRow = new HBox(12, avatarWrapper, userInfoBox);
        userCardRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(userInfoBox, Priority.ALWAYS);
        userCard.getChildren().add(userCardRow);

        navBox = new VBox(4);
        navDashboard = new HBox(10);
        navDashboard.getStyleClass().add("nav-item-active");
        navDashboard.setAlignment(Pos.CENTER_LEFT);
        navDashboard.setPadding(new Insets(10, 12, 10, 12));
        navDashboardIcon = new Text("📊");
        navDashboardLabel = new Label();
        navDashboardLabel.getStyleClass().add("nav-label");
        navDashboard.getChildren().addAll(navDashboardIcon, navDashboardLabel);
        navBox.getChildren().add(navDashboard);

        spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        footerBox = new VBox(12);
        languageHeader = new HBox(6);
        languageHeader.setAlignment(Pos.CENTER_LEFT);
        langIcon = new Text("🌐");
        langHeaderLabel = new Label();
        langHeaderLabel.getStyleClass().add("lang-header");
        languageHeader.getChildren().addAll(langIcon, langHeaderLabel);

        langGrid = new HBox(4);
        langGrid.setAlignment(Pos.CENTER);
        langGroup = new ToggleGroup();

        for (String lang : LOCALES) {
            ToggleButton btn = new ToggleButton(lang.split("-")[0].toUpperCase());
            btn.getStyleClass().add("lang-button");
            btn.setToggleGroup(langGroup);
            btn.setUserData(lang);
            btn.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(btn, Priority.ALWAYS);
            btn.setOnAction(e -> {
                if (btn.isSelected()) {
                    appState.setLocale(localeFromCode(lang));
                }
            });
            langGrid.getChildren().add(btn);
        }

        logoutButton = new Button();
        logoutButton.getStyleClass().add("logout-button");
        logoutButton.setMaxWidth(Double.MAX_VALUE);
        logoutButton.setAlignment(Pos.CENTER_LEFT);
        logoutIcon = new Text("⎋");
        logoutIcon.getStyleClass().add("logout-icon");
        logoutLabel = new Label();
        logoutLabel.getStyleClass().add("logout-label");
        HBox logoutContent = new HBox(8, logoutIcon, logoutLabel);
        logoutContent.setAlignment(Pos.CENTER_LEFT);
        logoutButton.setGraphic(logoutContent);

        footerBox.getChildren().addAll(languageHeader, langGrid, logoutButton);

        getChildren().addAll(logoBox, userCard, navBox, spacer, footerBox);

        applyLocale(appState.getLocale());
        applyTheme(appState.getTheme());
        updateUserAvatar();
    }

    public void applyLocale(Locale locale) {
        welcomeLabel.setText(UiText.text(locale, UiText.Key.WELCOME));
        navDashboardLabel.setText(UiText.text(locale, UiText.Key.DASHBOARD));
        langHeaderLabel.setText(UiText.text(locale, UiText.Key.LANGUAGE).toUpperCase());
        logoutLabel.setText(UiText.text(locale, UiText.Key.LOGOUT));
        updateUsernameLabel(locale);
        syncLangToggle();
    }

    public void applyTheme(String theme) {
        getStyleClass().removeAll("theme-light", "theme-dark");
        getStyleClass().add("theme-" + theme);
        if (userCard != null) updateUserAvatar();
    }

    private void updateUsernameLabel(Locale locale) {
        String user = appState.getCurrentUser();
        usernameLabel.setText(user != null ? user : "—");
    }

    private void updateUserAvatar() {
        String user = appState.getCurrentUser();
        if (user == null || user.isBlank()) {
            userAvatar.setStyle("-fx-fill: rgba(148, 163, 184, 0.2); -fx-stroke: rgba(148, 163, 184, 0.4);");
        } else {
            String color = colorManager.getColorForUser(user);
            userAvatar.setStyle("-fx-fill: " + toRGBA(color, 0.2) + "; -fx-stroke: " + toRGBA(color, 0.4) + ";");
        }
    }

    private static String toRGBA(String hexColor, double alpha) {
        if (hexColor == null || !hexColor.startsWith("#") || hexColor.length() != 7) {
            return "rgba(148, 163, 184, " + alpha + ")";
        }
        try {
            int r = Integer.parseInt(hexColor.substring(1, 3), 16);
            int g = Integer.parseInt(hexColor.substring(3, 5), 16);
            int b = Integer.parseInt(hexColor.substring(5, 7), 16);
            return String.format("rgba(%d, %d, %d, %.2f)", r, g, b, alpha);
        } catch (NumberFormatException e) {
            return "rgba(148, 163, 184, " + alpha + ")";
        }
    }

    private void syncLangToggle() {
        String current = UiText.codeFromLocale(appState.getLocale());
        for (javafx.scene.Node node : langGrid.getChildren()) {
            if (node instanceof ToggleButton btn) {
                if (current.equals(btn.getUserData())) {
                    btn.setSelected(true);
                } else {
                    btn.setSelected(false);
                }
            }
        }
    }

    public static Locale localeFromCode(String code) {
        return UiText.localeFromCode(code);
    }

    public static String codeFromLocale(Locale locale) {
        return UiText.codeFromLocale(locale);
    }

    public Button getLogoutButton() {
        return logoutButton;
    }
}
