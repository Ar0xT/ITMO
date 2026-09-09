package client.javafx.components;

import client.javafx.model.BandColorManager;
import client.ui.AppState;
import client.ui.i18n.UiText;
import common.models.MusicBand;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class VisualizationCanvas extends Canvas {

    private final AppState appState;
    private final BandColorManager colorManager;
    private final VBox detailsPopup;
    private final VBox detailsContent;

    private final Map<Long, RenderState> renderStates = new HashMap<>();
    private final Map<Long, Long> lastSeenIds = new HashMap<>();
    private AnimationTimer animationTimer;

    private double mouseX = 0;
    private double mouseY = 0;
    private boolean mouseInside = false;
    private long hoveredBandId = -1L;
    private long selectedBandId = -1L;
    private double panelWidth = 0;
    private double panelHeight = 0;
    private double pulsePhase = 0;
    private double scale = 1.0;
    private double panX = 0;
    private double panY = 0;
    private double zoom = 1.0;
    private static final double MIN_ZOOM = 0.05;
    private static final double MAX_ZOOM = 20.0;
    private boolean isPanning = false;
    private double panStartX = 0;
    private double panStartY = 0;
    private double panOriginX = 0;
    private double panOriginY = 0;
    private boolean isDraggingBand = false;

    private final Button editButton = new Button();
    private final Button deleteButton = new Button();
    private MusicBand selectedBand;

    public VisualizationCanvas(AppState appState, VBox detailsPopup, VBox detailsContent) {
        this.appState = appState;
        this.colorManager = new BandColorManager(appState);
        this.detailsPopup = detailsPopup;
        this.detailsContent = detailsContent;

        getStyleClass().add("visualization-canvas");

        setOnMouseMoved(this::handleMouseMoved);
        setOnMouseEntered(e -> {
            mouseInside = true;
            setCursor(javafx.scene.Cursor.CROSSHAIR);
        });
        setOnMouseExited(e -> {
            mouseInside = false;
            mouseX = 0;
            mouseY = 0;
            hoveredBandId = -1L;
            setCursor(javafx.scene.Cursor.DEFAULT);
            updateCoordLabel();
        });

        // Drag to pan (left button on empty area, or middle button anywhere).
        // Released clicks on a band still fire setOnMouseClicked below.
        setOnMousePressed(e -> {
            if (e.getButton() == javafx.scene.input.MouseButton.MIDDLE
                    || (e.getButton() == javafx.scene.input.MouseButton.PRIMARY
                        && findBandAt(e.getX(), e.getY()) == -1L)) {
                isPanning = true;
                panStartX = e.getX();
                panStartY = e.getY();
                panOriginX = panX;
                panOriginY = panY;
                setCursor(javafx.scene.Cursor.MOVE);
            }
        });
        setOnMouseDragged(e -> {
            if (isPanning) {
                panX = panOriginX + (e.getX() - panStartX);
                panY = panOriginY + (e.getY() - panStartY);
            }
        });
        setOnMouseReleased(e -> {
            if (isPanning) {
                isPanning = false;
                setCursor(mouseInside
                        ? javafx.scene.Cursor.CROSSHAIR
                        : javafx.scene.Cursor.DEFAULT);
            }
        });

        // Scroll wheel to zoom, centered on the mouse position.
        setOnScroll(e -> {
            double factor = (e.getDeltaY() > 0) ? 1.15 : 1.0 / 1.15;
            double oldZoom = zoom;
            double newZoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, zoom * factor));
            if (newZoom == oldZoom) return;
            // Keep the world point under the mouse fixed on screen.
            double mx = e.getX() - panelWidth / 2.0;
            double my = e.getY() - panelHeight / 2.0;
            double worldXBefore = (mx - panX) / (scale * oldZoom);
            double worldYBefore = (my - panY) / (scale * oldZoom);
            zoom = newZoom;
            panX = mx - worldXBefore * scale * zoom;
            panY = my - worldYBefore * scale * zoom;
        });

        setOnMouseClicked(e -> {
            if (e.getButton() != javafx.scene.input.MouseButton.PRIMARY) return;
            if (e.getClickCount() == 2) {
                panX = 0;
                panY = 0;
                zoom = 1.0;
                return;
            }
            long clickedId = findBandAt(e.getX(), e.getY());
            if (clickedId != -1L) {
                selectedBandId = clickedId;
                selectedBand = findBandById(clickedId);
                showDetailsPopup(clickedId);
            } else {
                selectedBandId = -1L;
                selectedBand = null;
                hideDetailsPopup();
            }
        });

        editButton.getStyleClass().addAll("details-action-button", "edit-action");
        deleteButton.getStyleClass().addAll("details-action-button", "delete-action");
        editButton.setVisible(false);
        editButton.setManaged(false);
        deleteButton.setVisible(false);
        deleteButton.setManaged(false);

        startAnimation();
    }

    /**
     * Sync the canvas's drawing-surface size to the parent's layout size.
     * Capped at {@link #MAX_CANVAS_DIM} defensively.
     */
    public void bindToParentSize(javafx.scene.layout.Region parent) {
        Runnable sync = () -> {
            double w = parent.getWidth();
            double h = parent.getHeight();
            if (!Double.isFinite(w) || w <= 0) return;
            if (!Double.isFinite(h) || h <= 0) return;
            if (w > MAX_CANVAS_DIM) w = MAX_CANVAS_DIM;
            if (h > MAX_CANVAS_DIM) h = MAX_CANVAS_DIM;
            double curW = getWidth();
            double curH = getHeight();
            if (Math.abs(curW - w) > 0.5) setWidth(w);
            if (Math.abs(curH - h) > 0.5) setHeight(h);
        };
        parent.widthProperty().addListener((obs, o, n) -> sync.run());
        parent.heightProperty().addListener((obs, o, n) -> sync.run());
        javafx.application.Platform.runLater(sync);
    }

    public void applyTheme(String theme) {
        getStyleClass().removeAll("theme-light", "theme-dark");
        getStyleClass().add("theme-" + theme);
    }

    public void applyLocale(Locale locale) {
        editButton.setText(UiText.text(locale, UiText.Key.EDIT));
        deleteButton.setText(UiText.text(locale, UiText.Key.DELETE));
    }

    public void refresh() {
    }

    private void startAnimation() {
        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateAndRender();
            }
        };
        animationTimer.start();
    }

    public void stop() {
        if (animationTimer != null) animationTimer.stop();
    }

    private void handleMouseMoved(MouseEvent event) {
        mouseX = event.getX();
        mouseY = event.getY();
        hoveredBandId = findBandAt(mouseX, mouseY);
        updateCoordLabel();
    }

    private void handleMouseClicked(MouseEvent event) {
        long clickedId = findBandAt(event.getX(), event.getY());
        if (clickedId != -1L) {
            selectedBandId = clickedId;
            selectedBand = findBandById(clickedId);
            showDetailsPopup(clickedId);
        } else {
            selectedBandId = -1L;
            selectedBand = null;
            hideDetailsPopup();
        }
    }

    private long findBandAt(double screenX, double screenY) {
        double w = panelWidth > 0 ? panelWidth : getWidth();
        double h = panelHeight > 0 ? panelHeight : getHeight();
        if (w <= 0 || h <= 0) return -1L;
        double s = Double.isFinite(scale) && scale > 0 ? scale : 1.0;
        double effectiveScale = s * zoom;
        if (effectiveScale <= 0) effectiveScale = 1.0;
        double offsetX = w / 2.0;
        double offsetY = h / 2.0;
        double worldX = (screenX - offsetX - panX) / effectiveScale;
        double worldY = -((screenY - offsetY - panY) / effectiveScale);

        for (MusicBand band : appState.getItems()) {
            if (band == null || band.getCoordinates() == null) continue;
            double r = radiusFor(band);
            double dx = worldX - band.getCoordinates().getX();
            double dy = worldY - band.getCoordinates().getY();
            if (dx * dx + dy * dy <= r * r) {
                return band.getId();
            }
        }
        return -1L;
    }

    private double radiusFor(MusicBand band) {
        long p = band.getNumberOfParticipants() == null ? 1 : band.getNumberOfParticipants();
        return Math.max(15, Math.min(60, 10 + p * 3));
    }

    private double scaledRadius(MusicBand band) {
        return Math.max(8.0, radiusFor(band) * scale);
    }

    /** Auto-fit scale so all bands always sit inside the canvas, with padding. */
    private double computeScale() {
        double maxAbs = 1.0;
        int visible = 0;
        for (MusicBand band : appState.getItems()) {
            if (band == null || band.getCoordinates() == null) continue;
            double x = band.getCoordinates().getX();
            double y = band.getCoordinates().getY();
            if (!Double.isFinite(x) || !Double.isFinite(y)) continue;
            double ax = Math.abs(x);
            double ay = Math.abs(y);
            if (ax > maxAbs) maxAbs = ax;
            if (ay > maxAbs) maxAbs = ay;
            visible++;
        }
        if (visible == 0) return 1.0;
        double padding = 60.0;
        double available = Math.min(panelWidth, panelHeight) / 2.0 - padding;
        available -= 30.0;
        if (!Double.isFinite(available) || available <= 0) return 1.0;
        double raw = available / maxAbs;
        if (!Double.isFinite(raw)) return 1.0;
        return Math.max(0.02, Math.min(2.0, raw));
    }

    private void updateCoordLabel() {
    }

    private static final double MAX_CANVAS_DIM = 4000.0;

    private void updateAndRender() {
        double w = getWidth();
        double h = getHeight();
        if (w <= 0 || h <= 0) return;
        if (!Double.isFinite(w) || !Double.isFinite(h)) return;
        if (w > MAX_CANVAS_DIM) w = MAX_CANVAS_DIM;
        if (h > MAX_CANVAS_DIM) h = MAX_CANVAS_DIM;
        panelWidth = w;
        panelHeight = h;

        try {
            doUpdateAndRender();
        } catch (Throwable t) {
            System.err.println("[Viz] render error: " + t);
        }
    }

    private void doUpdateAndRender() {
        GraphicsContext gc = getGraphicsContext2D();
        boolean isDark = appState.getTheme().equals("dark");
        scale = computeScale();
        if (!Double.isFinite(scale) || scale <= 0) scale = 1.0;

        gc.clearRect(0, 0, panelWidth, panelHeight);

        drawBackgroundWash(gc, isDark);

        Map<Long, MusicBand> current = new HashMap<>();
        for (MusicBand b : appState.getItems()) {
            if (b != null) current.put(b.getId(), b);
        }
        for (MusicBand b : appState.getItems()) {
            if (b == null || b.getCoordinates() == null) continue;
            long id = b.getId();
            if (!renderStates.containsKey(id)) {
                double sx = b.getCoordinates().getX();
                double sy = b.getCoordinates().getY();
                if (!Double.isFinite(sx)) sx = 0;
                if (!Double.isFinite(sy)) sy = 0;
                renderStates.put(id, new RenderState(sx, sy));
            }
        }
        renderStates.keySet().removeIf(id -> !current.containsKey(id));
        lastSeenIds.keySet().retainAll(current.keySet());

        // pulse the hover ring on each frame
        pulsePhase += 0.06;

        for (MusicBand band : appState.getItems()) {
            if (band == null || band.getCoordinates() == null) continue;
            RenderState state = renderStates.get(band.getId());
            if (state == null) continue;
            double targetX = band.getCoordinates().getX();
            double targetY = band.getCoordinates().getY();
            if (!Double.isFinite(targetX)) targetX = 0;
            if (!Double.isFinite(targetY)) targetY = 0;
            if (!Double.isFinite(state.x))  state.x  = targetX;
            if (!Double.isFinite(state.y))  state.y  = targetY;
            state.scale += (1.0 - state.scale) * 0.18;
            state.opacity += (1.0 - state.opacity) * 0.18;
            state.x += (targetX - state.x) * 0.18;
            state.y += (targetY - state.y) * 0.18;

            drawBand(gc, band, state, isDark);
        }

        if (mouseInside) {
            drawCrosshair(gc, isDark);
            drawCoordTooltip(gc, isDark);
        }

        // empty-state hint
        if (current.isEmpty()) {
            drawEmptyState(gc, isDark);
        }
    }

    private void drawBackgroundWash(GraphicsContext gc, boolean isDark) {
        double cx = panelWidth / 2.0;
        double cy = panelHeight / 2.0;
        double maxR = Math.hypot(Math.max(cx, panelWidth - cx), Math.max(cy, panelHeight - cy));
        javafx.scene.paint.RadialGradient wash = new javafx.scene.paint.RadialGradient(
                0, 0, cx, cy, maxR, false, javafx.scene.paint.CycleMethod.NO_CYCLE,
                new javafx.scene.paint.Stop(0, isDark
                        ? Color.color(0.16, 0.18, 0.22, 0.6)
                        : Color.color(0.95, 0.96, 0.99, 0.6)),
                new javafx.scene.paint.Stop(1, isDark
                        ? Color.color(0.10, 0.10, 0.12, 0)
                        : Color.color(1, 1, 1, 0))
        );
        gc.setFill(wash);
        gc.fillRect(0, 0, panelWidth, panelHeight);
    }

    private void drawEmptyState(GraphicsContext gc, boolean isDark) {
        String text = "No bands yet — add one with the + button";
        Font font = Font.font("Inter", FontWeight.MEDIUM, 13);
        gc.setFont(font);
        double tw = textWidth(gc, text);
        double padX = 16, padY = 10;
        double boxW = tw + padX * 2;
        double boxH = 32;
        double x = (panelWidth - boxW) / 2.0;
        double y = (panelHeight - boxH) / 2.0 - 60;
        Color bg = isDark
                ? Color.color(0.10, 0.10, 0.12, 0.70)
                : Color.color(1, 1, 1, 0.78);
        Color border = isDark
                ? Color.color(1, 1, 1, 0.08)
                : Color.color(0, 0, 0, 0.06);
        Color fg = isDark
                ? Color.color(0.78, 0.78, 0.84, 1)
                : Color.color(0.45, 0.45, 0.50, 1);
        gc.setFill(bg);
        gc.fillRoundRect(x, y, boxW, boxH, 12, 12);
        gc.setStroke(border);
        gc.setLineWidth(1);
        gc.strokeRoundRect(x + 0.5, y + 0.5, boxW - 1, boxH - 1, 12, 12);
        gc.setFill(fg);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(text, x + boxW / 2.0, y + padY + 12);
    }

    private void drawAxisLabels(GraphicsContext gc, boolean isDark) {
        double offsetX = panelWidth / 2.0;
        double offsetY = panelHeight / 2.0;
        Color fg = isDark
                ? Color.color(0.55, 0.58, 0.66, 0.9)
                : Color.color(0.40, 0.40, 0.45, 0.85);
        gc.setFill(fg);
        gc.setFont(Font.font("Inter", FontWeight.SEMI_BOLD, 10));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("X", panelWidth - 12, offsetY - 6);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("Y", offsetX + 8, 16);
    }

    private void drawGrid(GraphicsContext gc, boolean isDark) {
        gc.setStroke(isDark ? Color.color(1, 1, 1, 0.05) : Color.color(0, 0, 0, 0.05));
        gc.setLineWidth(1);
        double gridSize = 50;
        double offsetX = panelWidth / 2.0;
        double offsetY = panelHeight / 2.0;

        for (double x = offsetX % gridSize; x < panelWidth; x += gridSize) {
            gc.strokeLine(x, 0, x, panelHeight);
        }
        for (double y = offsetY % gridSize; y < panelHeight; y += gridSize) {
            gc.strokeLine(0, y, panelWidth, y);
        }
    }

    private void drawAxes(GraphicsContext gc, boolean isDark) {
        gc.setStroke(isDark ? Color.color(1, 1, 1, 0.2) : Color.color(0, 0, 0, 0.2));
        gc.setLineWidth(2);
        double offsetX = panelWidth / 2.0;
        double offsetY = panelHeight / 2.0;
        gc.strokeLine(offsetX, 0, offsetX, panelHeight);
        gc.strokeLine(0, offsetY, panelWidth, offsetY);
    }

    private void drawCrosshair(GraphicsContext gc, boolean isDark) {
        Color line = isDark
                ? Color.color(1, 1, 1, 0.35)
                : Color.color(0.05, 0.05, 0.1, 0.40);
        gc.setStroke(line);
        gc.setLineWidth(1);
        gc.setLineDashes(4, 4);
        gc.strokeLine(mouseX, 0, mouseX, panelHeight);
        gc.strokeLine(0, mouseY, panelWidth, mouseY);
        gc.setLineDashes(null);

        double dotR = 3.5;
        Color dot = isDark ? Color.color(1, 1, 1, 0.7) : Color.color(0.05, 0.05, 0.1, 0.85);
        gc.setFill(dot);
        gc.fillOval(mouseX - dotR, mouseY - dotR, dotR * 2, dotR * 2);
    }

    private void drawCoordTooltip(GraphicsContext gc, boolean isDark) {
        double offsetX = panelWidth / 2.0;
        double offsetY = panelHeight / 2.0;
        double effectiveScale = scale * zoom;
        if (effectiveScale <= 0 || !Double.isFinite(effectiveScale)) effectiveScale = 1.0;
        double worldX = (mouseX - offsetX - panX) / effectiveScale;
        double worldY = -((mouseY - offsetY - panY) / effectiveScale);
        Locale locale = appState.getLocale();
        String xStr = UiText.decimalFormat(locale, 0, 1).format(worldX);
        String yStr = UiText.decimalFormat(locale, 0, 1).format(worldY);
        String label = "X " + xStr + "  ·  Y " + yStr;

        Font font = Font.font("Inter", FontWeight.MEDIUM, 11);
        gc.setFont(font);
        double padX = 10, padY = 6;
        double labelW = textWidth(gc, label);
        double boxW = labelW + padX * 2;
        double boxH = 22;

        double margin = 14;
        double boxX = mouseX + margin;
        double boxY = mouseY + margin;
        if (boxX + boxW > panelWidth - 4)  boxX = mouseX - margin - boxW;
        if (boxY + boxH > panelHeight - 4) boxY = mouseY - margin - boxH;
        if (boxX < 4) boxX = 4;
        if (boxY < 4) boxY = 4;

        Color bg = isDark
                ? Color.color(0.10, 0.10, 0.12, 0.92)
                : Color.color(1, 1, 1, 0.94);
        Color border = isDark
                ? Color.color(1, 1, 1, 0.10)
                : Color.color(0, 0, 0, 0.10);
        Color fg = isDark
                ? Color.color(0.96, 0.96, 0.98, 1)
                : Color.color(0.11, 0.11, 0.12, 1);

        gc.setFill(bg);
        gc.fillRoundRect(boxX, boxY, boxW, boxH, 8, 8);
        gc.setStroke(border);
        gc.setLineWidth(1);
        gc.strokeRoundRect(boxX + 0.5, boxY + 0.5, boxW - 1, boxH - 1, 8, 8);

        gc.setFill(fg);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText(label, boxX + padX, boxY + padY + 10);
    }

    private double textWidth(GraphicsContext gc, String s) {
        return gc.getFont().getSize() * s.length() * 0.55;
    }

    private void drawBand(GraphicsContext gc, MusicBand band, RenderState state, boolean isDark) {
        double offsetX = panelWidth / 2.0;
        double offsetY = panelHeight / 2.0;
        double effectiveScale = scale * zoom;
        double screenX = state.x * effectiveScale + offsetX + panX;
        double screenY = -state.y * effectiveScale + offsetY + panY;
        double r = scaledRadius(band) * zoom;

        if (screenX + r < -200 || screenX - r > panelWidth + 200) return;
        if (screenY + r < -200 || screenY - r > panelHeight + 200) return;

        boolean isHovered = hoveredBandId == band.getId();
        boolean isSelected = selectedBandId == band.getId();
        String color = colorManager.getColorForUser(band.getOwnerLogin());

        gc.save();
        gc.setGlobalAlpha(state.opacity);
        gc.translate(screenX, screenY);
        gc.scale(state.scale, state.scale);

        Color base = Color.web(color);

        // Soft outer glow ring
        double pulse = 0.5 + 0.5 * Math.sin(pulsePhase);
        double glowR = r + 6 + pulse * 4;
        gc.setFill(base.deriveColor(0, 1, 1, 0.18));
        gc.fillOval(-glowR, -glowR, glowR * 2, glowR * 2);

        // Colored drop shadow
        javafx.scene.effect.DropShadow ds = new javafx.scene.effect.DropShadow();
        ds.setColor(base.deriveColor(0, 1, 0.55, isHovered || isSelected ? 0.65 : 0.40));
        ds.setRadius(isHovered || isSelected ? 26 : 14);
        ds.setOffsetY(isHovered || isSelected ? 6 : 3);
        gc.setEffect(ds);

        // Glossy radial gradient
        double gradR = isHovered || isSelected ? r * 0.4 : r * 0.3;
        javafx.scene.paint.RadialGradient gradient = new javafx.scene.paint.RadialGradient(
                0, 0, -r * 0.35, -r * 0.35, r, false, javafx.scene.paint.CycleMethod.NO_CYCLE,
                new javafx.scene.paint.Stop(0, base.deriveColor(0, 1.1, 1.25, 1)),
                new javafx.scene.paint.Stop(gradR / r, base),
                new javafx.scene.paint.Stop(1, base.deriveColor(0, 0.85, 0.62, 1))
        );
        gc.setFill(gradient);
        gc.fillOval(-r, -r, r * 2, r * 2);

        if (isSelected) {
            gc.setEffect(null);
            gc.setStroke(base.brighter());
            gc.setLineWidth(3);
            double hr = r + 6;
            gc.strokeOval(-hr, -hr, hr * 2, hr * 2);
            gc.setLineWidth(1.5);
            double hr2 = r + 12;
            gc.setStroke(base.deriveColor(0, 1, 1, 0.5));
            gc.strokeOval(-hr2, -hr2, hr2 * 2, hr2 * 2);
        }

        // Rim
        gc.setEffect(null);
        gc.setStroke(isDark ? Color.color(1, 1, 1, 0.85) : Color.color(1, 1, 1, 0.95));
        gc.setLineWidth(isSelected ? 2 : (isHovered ? 2 : 1.2));
        gc.strokeOval(-r, -r, r * 2, r * 2);

        // Label
        if (r > 20 || isHovered) {
            javafx.scene.effect.DropShadow labelDs = new javafx.scene.effect.DropShadow();
            labelDs.setColor(Color.color(0, 0, 0, 0.55));
            labelDs.setRadius(3);
            labelDs.setOffsetY(1);
            gc.setEffect(labelDs);
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Inter", FontWeight.SEMI_BOLD, 11));
            gc.setTextAlign(TextAlignment.CENTER);
            String name = band.getName() == null ? "?" : band.getName();
            String shortName = name.length() > 10 ? name.substring(0, 8) + "…" : name;
            gc.fillText(shortName, 0, 4);
            gc.setEffect(null);
        }

        String owner = band.getOwnerLogin();
        String currentUser = appState.getCurrentUser();
        if (owner != null && owner.equals(currentUser)) {
            double dotR = 5;
            double dotX = r * 0.65;
            double dotY = -r * 0.85;
            gc.setFill(Color.WHITE);
            gc.fillOval(dotX - dotR, dotY - dotR, dotR * 2, dotR * 2);
            gc.setFill(Color.web("#007AFF"));
            double inner = 2.5;
            gc.fillOval(dotX - inner, dotY - inner, inner * 2, inner * 2);
        }

        gc.restore();
    }

    private void showDetailsPopup(long bandId) {
        MusicBand band = findBandById(bandId);
        if (band == null) return;

        detailsContent.getChildren().clear();
        Locale locale = appState.getLocale();
        String color = colorManager.getColorForUser(band.getOwnerLogin());

        addDetailRow(UiText.text(locale, UiText.Key.FIELD_ID), String.valueOf(band.getId()), true);
        addDetailRow(UiText.text(locale, UiText.Key.FIELD_NAME), band.getName(), false);
        addDetailRowWithColor(UiText.text(locale, UiText.Key.FIELD_OWNER),
                band.getOwnerLogin() == null ? "—" : band.getOwnerLogin(), color, false);
        addDetailRow(UiText.text(locale, UiText.Key.FIELD_GENRE),
                band.getGenre() == null ? "—" : band.getGenre().name(), false);
        addDetailRow(UiText.text(locale, UiText.Key.FIELD_PARTICIPANTS),
                UiText.integerFormat(locale).format(band.getNumberOfParticipants()), false);
        addDetailRow(UiText.text(locale, UiText.Key.COORDINATES),
                UiText.decimalFormat(locale, 1, 1).format(band.getCoordinates().getX()) + ", "
                        + UiText.decimalFormat(locale, 1, 1).format(band.getCoordinates().getY()),
                true);
        if (band.getStudio() != null) {
            addDetailRow(UiText.text(locale, UiText.Key.FIELD_STUDIO), band.getStudio().getName(), false);
        }
        if (band.getSinglesCount() != null) {
            addDetailRow(UiText.text(locale, UiText.Key.FIELD_SINGLES),
                    UiText.integerFormat(locale).format(band.getSinglesCount()), false);
        }
        if (band.getEstablishmentDate() != null) {
            addDetailRow(UiText.text(locale, UiText.Key.FIELD_DATE),
                    UiText.dateFormat(locale).format(
                            java.util.Date.from(band.getEstablishmentDate()
                                    .atStartOfDay(java.time.ZoneId.systemDefault()).toInstant())),
                    false);
        }
        if (band.getCreationDate() != null) {
            addDetailRow(UiText.text(locale, UiText.Key.FIELD_CREATION),
                    UiText.dateTimeFormat(locale).format(band.getCreationDate()), false);
        }

        boolean isOwner = band.getOwnerLogin() != null
                && band.getOwnerLogin().equals(appState.getCurrentUser());
        editButton.setVisible(isOwner);
        editButton.setManaged(isOwner);
        deleteButton.setVisible(isOwner);
        deleteButton.setManaged(isOwner);
        if (isOwner) {
            applyLocale(locale);
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            HBox actions = new HBox(8, spacer, editButton, deleteButton);
            actions.getStyleClass().add("details-actions");
            actions.setAlignment(Pos.CENTER_RIGHT);
            detailsContent.getChildren().add(actions);
        }

        detailsPopup.setVisible(true);
        detailsPopup.setManaged(true);
    }

    private void hideDetailsPopup() {
        detailsPopup.setVisible(false);
        detailsPopup.setManaged(false);
    }

    private void addDetailRow(String label, String value, boolean monospace) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("details-row");
        Label lblLabel = new Label(label);
        lblLabel.getStyleClass().add("detail-label");
        lblLabel.setPrefWidth(110);
        Label lblValue = new Label(value);
        if (monospace) {
            lblValue.getStyleClass().addAll("detail-value", "value-mono");
        } else {
            lblValue.getStyleClass().add("detail-value");
        }
        row.getChildren().addAll(lblLabel, lblValue);
        detailsContent.getChildren().add(row);
    }

    private void addDetailRowWithColor(String label, String value, String color, boolean monospace) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("details-row");
        Label lblLabel = new Label(label);
        lblLabel.getStyleClass().add("detail-label");
        lblLabel.setPrefWidth(110);
        javafx.scene.shape.Circle dot = new javafx.scene.shape.Circle(5);
        dot.setFill(Color.web(color));
        dot.getStyleClass().add("owner-color-dot");
        Label lblValue = new Label(value);
        lblValue.getStyleClass().add("detail-value");
        row.getChildren().addAll(lblLabel, dot, lblValue);
        detailsContent.getChildren().add(row);
    }

    private MusicBand findBandById(long id) {
        for (MusicBand b : appState.getItems()) {
            if (b != null && b.getId() == id) return b;
        }
        return null;
    }

    public void setSelectedBand(MusicBand band) {
        if (band == null) {
            selectedBandId = -1L;
            selectedBand = null;
            hideDetailsPopup();
        } else {
            selectedBandId = band.getId();
            selectedBand = band;
            showDetailsPopup(band.getId());
        }
    }

    public MusicBand getSelectedBand() {
        return selectedBand;
    }

    public Button getEditButton() {
        return editButton;
    }

    public Button getDeleteButton() {
        return deleteButton;
    }

    private static class RenderState {
        double x;
        double y;
        double scale = 0;
        double opacity = 0;

        RenderState(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}
