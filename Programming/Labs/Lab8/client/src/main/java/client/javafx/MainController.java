package client.javafx;

import client.ClientCommandRegistry;
import client.javafx.components.BandEditDialog;
import client.javafx.components.BandTableView;
import client.javafx.components.CommandPalette;
import client.javafx.components.HelpDialog;
import client.javafx.components.SidebarPane;
import client.javafx.components.ToastPane;
import client.javafx.components.TopBarPane;
import client.javafx.components.VisualizationCanvas;
import client.javafx.network.BandService;
import client.javafx.sync.SyncService;
import client.ui.AppState;
import client.ui.i18n.UiText;
import common.commandsabstraction.CommandResult;
import common.network.CommandMeta;
import common.models.MusicBand;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class MainController {

    @FXML private BorderPane root;
    @FXML private SplitPane centerSplitPane;
    @FXML private VBox sidebarPane;
    @FXML private HBox topBarPane;
    @FXML private VBox tableContainer;
    @FXML private Label tableTitle;
    @FXML private TextField filterField;
    @FXML private Label vizTitle;
    @FXML private VBox detailsPopup;
    @FXML private Label detailsTitle;
    @FXML private VBox detailsContent;
    @FXML private StackPane vizStackPane;
    @FXML private Label statusLabel;
    @FXML private VBox toastPane;

    private AppState appState;
    private SidebarPane sidebar;
    private TopBarPane topBar;
    private BandTableView bandTableView;
    private VisualizationCanvas visualizationCanvas;
    private ToastPane toasts;

    private BandService bandService;
    private SyncService syncService;

    private AppState.SyncDiff lastReportedDiff = new AppState.SyncDiff(0, 0, 0);

    public void init(AppState appState) {
        this.appState = appState;
        this.bandService = new BandService(appState);

        buildSidebar();
        buildTopBar();
        buildTable();
        buildVisualization();
        wireSplitPane();
        buildToasts();
        wireActions();
        installKeyboardShortcuts();
        applyLocale(appState.getLocale());
        applyTheme(appState.getTheme());
        bindStatusBar();
        startSync();
        updateTableTitle();

        root.setOpacity(0);
        root.setTranslateY(8);
        javafx.animation.FadeTransition fade = new javafx.animation.FadeTransition(
                javafx.util.Duration.millis(420), root);
        fade.setFromValue(0);
        fade.setToValue(1);
        javafx.animation.TranslateTransition slide = new javafx.animation.TranslateTransition(
                javafx.util.Duration.millis(420), root);
        slide.setFromY(8);
        slide.setToY(0);
        javafx.animation.Interpolator ease = javafx.animation.Interpolator.EASE_OUT;
        fade.setInterpolator(ease);
        slide.setInterpolator(ease);
        fade.play();
        slide.play();

        appState.getItems().addListener((javafx.collections.ListChangeListener<common.models.MusicBand>) c ->
                Platform.runLater(this::updateTableTitle));
        appState.localeProperty().addListener((obs, oldVal, newVal) ->
                Platform.runLater(() -> applyLocale(newVal)));
        appState.themeProperty().addListener((obs, oldVal, newVal) ->
                Platform.runLater(() -> applyTheme(newVal)));
        appState.lastSyncDiffProperty().addListener((obs, oldVal, newVal) ->
                Platform.runLater(() -> onSyncDiff(newVal)));
    }

    @FXML
    private void initialize() {
    }

    private void buildSidebar() {
        sidebar = new SidebarPane(appState);
        sidebarPane.getChildren().setAll(sidebar);
    }

    private void buildTopBar() {
        topBar = new TopBarPane(appState);
        topBarPane.getChildren().setAll(topBar);
    }

    private void buildTable() {
        bandTableView = new BandTableView(appState, filterField);
        VBox.setVgrow(bandTableView, javafx.scene.layout.Priority.ALWAYS);
        tableContainer.getChildren().add(bandTableView);
    }

    private void buildVisualization() {
        visualizationCanvas = new VisualizationCanvas(appState, detailsPopup, detailsContent);
        vizStackPane.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);
        vizStackPane.setMinSize(0, 0);
        vizStackPane.getChildren().add(0, visualizationCanvas);
        visualizationCanvas.bindToParentSize(vizStackPane);
    }

    private void wireSplitPane() {
        if (centerSplitPane == null) return;
        for (javafx.scene.Node pane : centerSplitPane.getItems()) {
            SplitPane.setResizableWithParent(pane, true);
        }
        Runnable fixDividers = () -> {
            if (centerSplitPane.getScene() == null) return;
            for (javafx.scene.Node n : centerSplitPane.getScene().getRoot().lookupAll(".split-pane-divider")) {
                n.setPickOnBounds(true);
            }
        };
        centerSplitPane.sceneProperty().addListener((obs, o, n) -> {
            if (n != null) {
                Platform.runLater(fixDividers);
            }
        });
        Platform.runLater(fixDividers);
    }

    private void buildToasts() {
        toasts = new ToastPane();
        if (toastPane != null) {
            toastPane.getChildren().add(toasts);
        }
    }

    private void wireActions() {
        if (topBar != null) {
            topBar.getAddButton().setOnAction(e -> openCreateDialog());
            topBar.getCommandsButton().setOnAction(e -> openCommandPalette());
        }
        if (sidebar != null) {
            sidebar.getLogoutButton().setOnAction(e -> handleLogout());
        }
        if (bandTableView != null) {
            bandTableView.addEventHandler(BandTableView.BandEditEvent.EDIT, this::onBandEditEvent);
            bandTableView.addEventHandler(BandTableView.BandDeleteEvent.DELETE, this::onBandDeleteEvent);
            bandTableView.addEventHandler(BandTableView.BandRemoveGreaterEvent.REMOVE_GREATER, this::onBandRemoveGreater);
            bandTableView.addEventHandler(BandTableView.BandRemoveLowerEvent.REMOVE_LOWER, this::onBandRemoveLower);
        }
        if (visualizationCanvas != null) {
            visualizationCanvas.getEditButton().setOnAction(e -> onCanvasEdit());
            visualizationCanvas.getDeleteButton().setOnAction(e -> onCanvasDelete());
        }
    }

    private void installKeyboardShortcuts() {
        Scene scene = root.getScene();
        if (scene == null) {
            root.sceneProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) attachSceneHandlers(newVal);
            });
        } else {
            attachSceneHandlers(scene);
        }
    }

    private void attachSceneHandlers(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, this::onKeyPressed);
    }

    private void onKeyPressed(KeyEvent event) {
        if (event.isConsumed()) return;
        boolean ctrl = event.isControlDown() || event.isMetaDown();

        if (ctrl && event.getCode() == KeyCode.N) {
            event.consume();
            openCreateDialog();
            return;
        }
        if (ctrl && event.getCode() == KeyCode.F) {
            event.consume();
            if (filterField != null) filterField.requestFocus();
            return;
        }
        if (ctrl && event.getCode() == KeyCode.K) {
            event.consume();
            openCommandPalette();
            return;
        }
        if (ctrl && event.getCode() == KeyCode.L) {
            event.consume();
            handleLogout();
            return;
        }
        if (event.getCode() == KeyCode.F1) {
            event.consume();
            openHelpDialog();
            return;
        }
        if (event.getCode() == KeyCode.DELETE && isTableFocused()) {
            MusicBand selected = bandTableView == null ? null : bandTableView.getSelectedBand();
            if (selected != null) {
                event.consume();
                confirmAndDelete(selected);
            }
        }
    }

    private boolean isTableFocused() {
        javafx.scene.Node focus = root.getScene() == null ? null : root.getScene().getFocusOwner();
        if (focus == null || bandTableView == null) return false;
        if (focus == bandTableView) return true;
        javafx.scene.Node n = focus.getParent();
        while (n != null) {
            if (n == bandTableView) return true;
            n = n.getParent();
        }
        return false;
    }

    private void bindStatusBar() {
        if (statusLabel == null) return;
        statusLabel.textProperty().bind(appState.statusMessageProperty());
        appState.statusKindProperty().addListener((obs, oldVal, newVal) -> updateStatusKindClass());
        updateStatusKindClass();
    }

    private void updateStatusKindClass() {
        if (statusLabel == null) return;
        statusLabel.getStyleClass().removeAll("status-info", "status-success", "status-error");
        String kind = appState.getStatusKind();
        if (kind != null && !kind.isBlank()) {
            statusLabel.getStyleClass().add("status-" + kind);
        }
    }

    private void startSync() {
        syncService = new SyncService(appState, new ClientCommandRegistry(), root);
        syncService.start();
    }

    public void shutdown() {
        if (syncService != null) syncService.stop();
    }

    public BandTableView getBandTableView() {
        return bandTableView;
    }

    public VisualizationCanvas getVisualizationCanvas() {
        return visualizationCanvas;
    }

    private void applyLocale(Locale locale) {
        updateTableTitle();
        if (vizTitle != null) vizTitle.setText(UiText.text(locale, UiText.Key.VISUALIZATION));
        if (filterField != null) filterField.setPromptText(UiText.text(locale, UiText.Key.FILTER));
        if (detailsTitle != null) detailsTitle.setText(UiText.text(locale, UiText.Key.DETAILS));
        if (sidebar != null) sidebar.applyLocale(locale);
        if (topBar != null) topBar.applyLocale(locale);
        if (visualizationCanvas != null) visualizationCanvas.applyLocale(locale);
        if (bandTableView != null) bandTableView.applyLocale();
    }

    private void updateTableTitle() {
        if (tableTitle == null) return;
        int count = appState.getItems().size();
        String base = UiText.text(appState.getLocale(), UiText.Key.ITEMS);
        tableTitle.setText(base + "  ·  " + count);
    }

    private void applyTheme(String theme) {
        root.getStyleClass().removeAll("theme-light", "theme-dark");
        root.getStyleClass().add("theme-" + theme);
        if (bandTableView != null) bandTableView.applyTheme(theme);
        if (visualizationCanvas != null) visualizationCanvas.applyTheme(theme);
        if (sidebar != null) sidebar.applyTheme(theme);
        if (topBar != null) topBar.applyTheme(theme);
    }

    public void refreshData() {
        if (bandTableView != null) bandTableView.refresh();
        if (visualizationCanvas != null) visualizationCanvas.refresh();
    }

    // ------------------------------------------------------------------
    // Action handlers
    // ------------------------------------------------------------------

    private void onSyncDiff(AppState.SyncDiff diff) {
        if (diff == null || toasts == null) return;
        Locale locale = appState.getLocale();
        if (diff.added > 0) {
            String msg = String.format(
                    locale, UiText.text(locale, UiText.Key.TOAST_BANDS_ADDED), diff.added);
            toasts.show(msg, ToastPane.Kind.INFO);
        }
        if (diff.removed > 0) {
            String msg = String.format(
                    locale, UiText.text(locale, UiText.Key.TOAST_BANDS_REMOVED), diff.removed);
            toasts.show(msg, ToastPane.Kind.WARNING);
        }
        lastReportedDiff = diff;
    }

    private void openCreateDialog() {
        BandEditDialog dialog = new BandEditDialog(appState, null);
        DialogThemer.applyTheme(dialog, appState.getTheme(), "/css/main.css");
        MusicBand result = dialog.showAndWait().orElse(null);
        if (result == null) return;
        String key = generateKey();
        bandService.insert(key, result)
                .whenComplete((res, ex) -> Platform.runLater(() -> reportResult(res, ex)));
    }

    private void onBandEditEvent(BandTableView.BandEditEvent event) {
        openEditDialog(event.getBand());
    }

    private void onCanvasEdit() {
        MusicBand band = visualizationCanvas == null ? null : visualizationCanvas.getSelectedBand();
        if (band != null) openEditDialog(band);
    }

    private void openEditDialog(MusicBand existing) {
        BandEditDialog dialog = new BandEditDialog(appState, existing);
        DialogThemer.applyTheme(dialog, appState.getTheme(), "/css/main.css");
        MusicBand updated = dialog.showAndWait().orElse(null);
        if (updated == null) return;
        bandService.update(existing.getId(), updated)
                .whenComplete((res, ex) -> Platform.runLater(() -> reportResult(res, ex)));
    }

    private void onBandDeleteEvent(BandTableView.BandDeleteEvent event) {
        confirmAndDelete(event.getBand());
    }

    private void onCanvasDelete() {
        MusicBand band = visualizationCanvas == null ? null : visualizationCanvas.getSelectedBand();
        if (band != null) confirmAndDelete(band);
    }

    private void confirmAndDelete(MusicBand band) {
        Locale locale = appState.getLocale();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                UiText.text(locale, UiText.Key.CONFIRM_DELETE_MESSAGE) + "\n\n" + band.getName(),
                ButtonType.YES, ButtonType.NO);
        alert.setTitle(UiText.text(locale, UiText.Key.CONFIRM_DELETE_TITLE));
        DialogThemer.applyTheme(alert, appState.getTheme(), "/css/main.css");
        alert.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                String key = appState.getKeyForId(band.getId());
                if (key == null) {
                    appState.setStatus(
                            UiText.text(locale, UiText.Key.OPERATION_FAILED) + "key not found",
                            "error");
                    return;
                }
                bandService.removeKey(key)
                        .whenComplete((res, ex) -> Platform.runLater(() -> reportResult(res, ex)));
            }
        });
    }

    private void onBandRemoveGreater(BandTableView.BandRemoveGreaterEvent event) {
        runReferenceCommand(event.getBand(), true);
    }

    private void onBandRemoveLower(BandTableView.BandRemoveLowerEvent event) {
        runReferenceCommand(event.getBand(), false);
    }

    private void runReferenceCommand(MusicBand reference, boolean greater) {
        Locale locale = appState.getLocale();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                (greater
                        ? UiText.text(locale, UiText.Key.COMMAND_REMOVE_GREATER)
                        : UiText.text(locale, UiText.Key.COMMAND_REMOVE_LOWER))
                        + " — " + reference.getName(),
                ButtonType.YES, ButtonType.NO);
        alert.setTitle(UiText.text(locale, UiText.Key.COMMANDS));
        DialogThemer.applyTheme(alert, appState.getTheme(), "/css/main.css");
        alert.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.YES) return;
            var future = greater
                    ? bandService.removeGreater(reference)
                    : bandService.removeLower(reference);
            future.whenComplete((res, ex) -> Platform.runLater(() -> reportResult(res, ex)));
        });
    }

    private void openCommandPalette() {
        CommandPalette palette = new CommandPalette(appState, bandService, this::onPaletteResult);
        DialogThemer.applyTheme(palette, appState.getTheme(), "/css/main.css");
        Scene scene = root.getScene();
        if (scene != null && scene.getWindow() instanceof Stage stage) {
            palette.initOwner(stage);
        }
        palette.show();
    }

    private void openHelpDialog() {
        List<CommandMeta> commands = new ClientCommandRegistry().exportPublicCommandMeta();
        HelpDialog dialog = new HelpDialog(appState, commands);
        DialogThemer.applyTheme(dialog, appState.getTheme(), "/css/main.css");
        Scene scene = root.getScene();
        if (scene != null && scene.getWindow() instanceof Stage stage) {
            dialog.initOwner(stage);
        }
        dialog.showAndWait();
    }

    private void onPaletteResult(CommandResult result) {
        reportResult(result, null);
    }

    private void reportResult(CommandResult res, Throwable ex) {
        Locale locale = appState.getLocale();
        if (ex != null) {
            String msg = UiText.text(locale, UiText.Key.OPERATION_FAILED) + ex.getMessage();
            showResult(locale, msg, "error");
            return;
        }
        if (res == null) {
            String msg = UiText.text(locale, UiText.Key.OPERATION_FAILED) + "no result";
            showResult(locale, msg, "error");
            return;
        }
        if (!res.isSuccess()) {
            String msg = UiText.text(locale, UiText.Key.OPERATION_FAILED) + res.getMessage();
            showResult(locale, msg, "error");
        } else {
            String msg = UiText.text(locale, UiText.Key.OPERATION_SUCCESS) + " — " + res.getMessage();
            showResult(locale, msg, "success");
        }
    }

    /**
     * Show a command result.  Short messages go in the status bar + toast;
     * long messages (help text, info dump, group-by-name map, etc.) are
     * routed to a scrollable Alert dialog so the main view doesn't grow.
     */
    private void showResult(Locale locale, String message, String kind) {
        if (message == null) message = "";
        String shortMsg = message;
        final int SOFT_LIMIT = 180;
        if (shortMsg.length() > SOFT_LIMIT) {
            shortMsg = shortMsg.substring(0, SOFT_LIMIT - 1) + "…";
        }
        appState.setStatus(shortMsg, kind);
        if (toasts != null) toasts.show(shortMsg, ToastPane.Kind.valueOf(kind.toUpperCase()));

        if (message.length() > SOFT_LIMIT) {
            showLongResultDialog(locale, message, kind);
        }
    }

    private void showLongResultDialog(Locale locale, String message, String kind) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(UiText.text(locale, UiText.Key.COMMANDS));
        alert.setHeaderText(kind.equals("error")
                ? UiText.text(locale, UiText.Key.OPERATION_FAILED)
                : UiText.text(locale, UiText.Key.OPERATION_SUCCESS));
        javafx.scene.control.TextArea area = new javafx.scene.control.TextArea(message);
        area.setEditable(false);
        area.setWrapText(true);
        area.setPrefSize(640, 360);
        area.getStyleClass().add("result-text-area");
        alert.getDialogPane().setContent(area);
        alert.getDialogPane().setPrefSize(720, 460);
        DialogThemer.applyTheme(alert, appState.getTheme(), "/css/main.css");
        Scene scene = root.getScene();
        if (scene != null && scene.getWindow() instanceof Stage stage) {
            alert.initOwner(stage);
        }
        alert.showAndWait();
    }

    private void handleLogout() {
        Locale locale = appState.getLocale();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                UiText.text(locale, UiText.Key.LOGOUT) + "?",
                ButtonType.YES, ButtonType.NO);
        alert.setTitle(UiText.text(locale, UiText.Key.LOGOUT));
        Button yes = (Button) alert.getDialogPane().lookupButton(ButtonType.YES);
        yes.setText(UiText.text(locale, UiText.Key.LOGOUT));
        DialogThemer.applyTheme(alert, appState.getTheme(), "/css/main.css");
        alert.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                shutdown();
                appState.clearData();
                appState.setCredentials(null);
                returnToLogin();
            }
        });
    }

    private void returnToLogin() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/fxml/LoginView.fxml"));
            javafx.scene.Parent loginRoot = loader.load();
            client.ui.LoginController controller = loader.getController();
            controller.init(appState);
            Stage stage = (Stage) root.getScene().getWindow();
            Scene scene = new Scene(loginRoot, stage.getWidth(), stage.getHeight());
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String generateKey() {
        return "k-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
