package client.javafx.components;

import client.javafx.model.BandColorManager;
import client.ui.AppState;
import client.ui.i18n.UiText;
import common.models.Coordinates;
import common.models.MusicBand;
import common.models.MusicGenre;
import common.models.Studio;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BandTableView extends TableView<MusicBand> {

    private final AppState appState;
    private final BandColorManager colorManager;
    private final TextField filterField;
    private final FilteredList<MusicBand> filteredBands;
    private final SortedList<MusicBand> sortedBands;

    private TableColumn<MusicBand, ?> idCol;
    private TableColumn<MusicBand, ?> nameCol;
    private TableColumn<MusicBand, ?> ownerCol;
    private TableColumn<MusicBand, ?> xCol;
    private TableColumn<MusicBand, ?> yCol;
    private TableColumn<MusicBand, ?> participantsCol;
    private TableColumn<MusicBand, ?> singlesCol;
    private TableColumn<MusicBand, ?> genreCol;
    private TableColumn<MusicBand, ?> studioCol;
    private TableColumn<MusicBand, ?> establishmentCol;
    private TableColumn<MusicBand, ?> creationCol;

    public BandTableView(AppState appState, TextField filterField) {
        this.appState = appState;
        this.colorManager = new BandColorManager(appState);
        this.filterField = filterField;

        getStyleClass().add("band-table");
        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        ObservableList<MusicBand> backingList = appState.getItems();
        this.filteredBands = new FilteredList<>(backingList, band -> applyFilterQuery(band, currentQuery()));
        this.sortedBands = new SortedList<>(filteredBands);
        sortedBands.comparatorProperty().bind(comparatorProperty());
        setItems(sortedBands);

        buildColumns();

        if (filterField != null) {
            filterField.textProperty().addListener((obs, oldVal, newVal) -> {
                String q = currentQuery();
                Predicate<MusicBand> predicate = band -> applyFilterQuery(band, q);
                filteredBands.setPredicate(predicate);
                updateEmptyState();
            });
        }

        backingList.addListener((ListChangeListener<MusicBand>) c -> updateEmptyState());
        attachContextMenu();
        updateEmptyState();

        appState.localeProperty().addListener((obs, oldVal, newVal) ->
                javafx.application.Platform.runLater(this::applyLocale));
    }

    private String currentQuery() {
        if (filterField == null) return "";
        String t = filterField.getText();
        return t == null ? "" : t.toLowerCase().trim();
    }

    /** Filter a single band using the Streams-API logic used by applyStreamsFilter. */
    private boolean applyFilterQuery(MusicBand band, String qLower) {
        if (qLower == null || qLower.isEmpty()) return true;
        if (band == null) return false;
        return filterStream(Stream.of(band), qLower).findAny().isPresent();
    }

    /**
     * Build a filtered+sorted view of the current items using the Streams API.
     * Required by the lab spec: "Сортировку и фильтрацию значений столбцов
     * реализовать с помощью Streams API."
     */
    public ObservableList<MusicBand> applyStreamsFilter() {
        return applyStreamsFilter(currentQuery(), getComparator());
    }

    public ObservableList<MusicBand> applyStreamsFilter(String queryLower, Comparator<MusicBand> comparator) {
        Stream<MusicBand> stream = appState.getItems().stream().filter(Objects::nonNull);
        stream = filterStream(stream, queryLower);
        if (comparator != null) {
            stream = stream.sorted(comparator);
        }
        return FXCollections.observableArrayList(stream.collect(Collectors.toList()));
    }

    private Stream<MusicBand> filterStream(Stream<MusicBand> stream, String qLower) {
        if (qLower == null || qLower.isEmpty()) return stream;
        return stream.filter(band -> {
            if (containsCI(band.getName(), qLower)) return true;
            if (containsCI(band.getOwnerLogin(), qLower)) return true;
            if (band.getGenre() != null && containsCI(band.getGenre().name(), qLower)) return true;
            if (band.getStudio() != null && containsCI(band.getStudio().getName(), qLower)) return true;
            if (band.getCoordinates() != null) {
                if (containsCI(formatCoordValue(band.getCoordinates().getX()), qLower)) return true;
                if (containsCI(String.valueOf(band.getCoordinates().getY()), qLower)) return true;
            }
            if (band.getNumberOfParticipants() != null
                    && containsCI(String.valueOf(band.getNumberOfParticipants()), qLower)) return true;
            if (band.getSinglesCount() != null
                    && containsCI(String.valueOf(band.getSinglesCount()), qLower)) return true;
            if (band.getEstablishmentDate() != null
                    && containsCI(band.getEstablishmentDate().toString(), qLower)) return true;
            if (band.getCreationDate() != null
                    && containsCI(band.getCreationDate().toString(), qLower)) return true;
            return false;
        });
    }

    private String formatCoordValue(double x) {
        return UiText.decimalFormat(appState.getLocale(), 1, 1).format(x);
    }

    public void applyLocale() {
        Locale locale = appState.getLocale();
        if (idCol != null)            idCol.setText(UiText.text(locale, UiText.Key.FIELD_ID));
        if (nameCol != null)          nameCol.setText(UiText.text(locale, UiText.Key.FIELD_NAME));
        if (ownerCol != null)         ownerCol.setText(UiText.text(locale, UiText.Key.FIELD_OWNER));
        if (xCol != null)             xCol.setText(UiText.text(locale, UiText.Key.FIELD_X));
        if (yCol != null)             yCol.setText(UiText.text(locale, UiText.Key.FIELD_Y));
        if (participantsCol != null)  participantsCol.setText(UiText.text(locale, UiText.Key.FIELD_PARTICIPANTS));
        if (singlesCol != null)       singlesCol.setText(UiText.text(locale, UiText.Key.FIELD_SINGLES));
        if (genreCol != null)         genreCol.setText(UiText.text(locale, UiText.Key.FIELD_GENRE));
        if (studioCol != null)        studioCol.setText(UiText.text(locale, UiText.Key.FIELD_STUDIO));
        if (establishmentCol != null) establishmentCol.setText(UiText.text(locale, UiText.Key.FIELD_DATE));
        if (creationCol != null)      creationCol.setText(UiText.text(locale, UiText.Key.FIELD_CREATION));
        updateEmptyState();
    }

    private void buildColumns() {
        TableColumn<MusicBand, Long> idCol = new TableColumn<>();
        this.idCol = idCol;
        idCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getId()));
        idCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(String.valueOf(item));
                    getStyleClass().add("id-cell");
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });
        idCol.setComparator(Comparator.comparingLong(Long::longValue));
        idCol.setText(UiText.text(appState.getLocale(), UiText.Key.FIELD_ID));
        idCol.setPrefWidth(60);

        TableColumn<MusicBand, String> colorCol = new TableColumn<>();
        colorCol.setCellValueFactory(cell -> new SimpleStringProperty(""));
        colorCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    MusicBand band = getTableRow().getItem();
                    String owner = band.getOwnerLogin();
                    String color = colorManager.getColorForUser(owner);
                    Circle dot = new Circle(5);
                    dot.setFill(Color.web(color));
                    javafx.scene.effect.DropShadow ds = new javafx.scene.effect.DropShadow();
                    ds.setColor(Color.color(0, 0, 0, 0.15));
                    ds.setRadius(2);
                    ds.setOffsetY(1);
                    dot.setEffect(ds);
                    setGraphic(dot);
                    setText(null);
                    setAlignment(Pos.CENTER);
                }
            }
        });
        colorCol.setPrefWidth(30);
        colorCol.setSortable(false);
        colorCol.setText("");

        TableColumn<MusicBand, String> nameCol = new TableColumn<>();
        this.nameCol = nameCol;
        nameCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        nameCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    MusicBand band = getTableRow().getItem();
                    String owner = band.getOwnerLogin();
                    String color = colorManager.getColorForUser(owner);
                    Circle dot = new Circle(5);
                    dot.setFill(Color.web(color));
                    javafx.scene.effect.DropShadow ds = new javafx.scene.effect.DropShadow();
                    ds.setColor(Color.color(0, 0, 0, 0.15));
                    ds.setRadius(2);
                    ds.setOffsetY(1);
                    dot.setEffect(ds);
                    Label nameLabel = new Label(band.getName());
                    nameLabel.getStyleClass().add("name-label");
                    HBox row = new HBox(8, dot, nameLabel);
                    row.setAlignment(Pos.CENTER_LEFT);
                    getStyleClass().add("name-cell");
                    setGraphic(row);
                    setText(null);
                }
            }
        });
        nameCol.setComparator(String.CASE_INSENSITIVE_ORDER);
        nameCol.setText(UiText.text(appState.getLocale(), UiText.Key.FIELD_NAME));
        nameCol.setPrefWidth(180);

        TableColumn<MusicBand, String> ownerCol = new TableColumn<>();
        this.ownerCol = ownerCol;
        ownerCol.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getOwnerLogin() == null ? "—" : cell.getValue().getOwnerLogin()));
        ownerCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    MusicBand band = getTableRow().getItem();
                    String owner = band.getOwnerLogin();
                    String currentUser = appState.getCurrentUser();
                    Label badge = new Label(owner == null ? "—" : owner);
                    badge.getStyleClass().add("owner-badge");
                    if (owner != null && owner.equals(currentUser)) {
                        badge.getStyleClass().add("owner-self");
                    }
                    getStyleClass().add("owner-cell");
                    setGraphic(badge);
                    setText(null);
                }
            }
        });
        ownerCol.setComparator(String.CASE_INSENSITIVE_ORDER);
        ownerCol.setPrefWidth(120);
        ownerCol.setText(UiText.text(appState.getLocale(), UiText.Key.FIELD_OWNER));

        TableColumn<MusicBand, Double> xCol = new TableColumn<>();
        this.xCol = xCol;
        xCol.setCellValueFactory(cell -> {
            MusicBand b = cell.getValue();
            return new javafx.beans.property.SimpleObjectProperty<>(b.getCoordinates().getX());
        });
        xCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(formatDecimal(item, 1));
                    getStyleClass().add("coord-cell");
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });
        xCol.setComparator(Comparator.comparingDouble(Double::doubleValue));
        xCol.setText(UiText.text(appState.getLocale(), UiText.Key.FIELD_X));
        xCol.setPrefWidth(100);

        TableColumn<MusicBand, Integer> yCol = new TableColumn<>();
        this.yCol = yCol;
        yCol.setCellValueFactory(cell -> {
            MusicBand b = cell.getValue();
            return new javafx.beans.property.SimpleObjectProperty<>(b.getCoordinates().getY());
        });
        yCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(formatInteger(item));
                    getStyleClass().add("coord-cell");
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });
        yCol.setComparator(Comparator.comparingInt(Integer::intValue));
        yCol.setText(UiText.text(appState.getLocale(), UiText.Key.FIELD_Y));
        yCol.setPrefWidth(100);

        TableColumn<MusicBand, Long> participantsCol = new TableColumn<>();
        this.participantsCol = participantsCol;
        participantsCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getNumberOfParticipants()));
        participantsCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Long item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatInteger(item));
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });
        participantsCol.setComparator(Comparator.comparingLong(Long::longValue));
        participantsCol.setText(UiText.text(appState.getLocale(), UiText.Key.FIELD_PARTICIPANTS));
        participantsCol.setPrefWidth(110);

        TableColumn<MusicBand, Integer> singlesCol = new TableColumn<>();
        this.singlesCol = singlesCol;
        singlesCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getSinglesCount()));
        singlesCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item == null ? "—" : formatInteger(item));
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });
        singlesCol.setComparator(Comparator.nullsLast(Comparator.comparingInt(Integer::intValue)));
        singlesCol.setText(UiText.text(appState.getLocale(), UiText.Key.FIELD_SINGLES));
        singlesCol.setPrefWidth(80);

        TableColumn<MusicBand, String> genreCol = new TableColumn<>();
        this.genreCol = genreCol;
        genreCol.setCellValueFactory(cell -> new SimpleStringProperty(
                cell.getValue().getGenre() == null ? "—" : cell.getValue().getGenre().name()));
        genreCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item);
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });
        genreCol.setComparator(String.CASE_INSENSITIVE_ORDER);
        genreCol.setText(UiText.text(appState.getLocale(), UiText.Key.FIELD_GENRE));
        genreCol.setPrefWidth(100);

        TableColumn<MusicBand, String> studioCol = new TableColumn<>();
        this.studioCol = studioCol;
        studioCol.setCellValueFactory(cell -> {
            Studio s = cell.getValue().getStudio();
            return new SimpleStringProperty(s == null ? "—" : s.getName());
        });
        studioCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item);
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });
        studioCol.setComparator(String.CASE_INSENSITIVE_ORDER);
        studioCol.setText(UiText.text(appState.getLocale(), UiText.Key.FIELD_STUDIO));
        studioCol.setPrefWidth(120);

        TableColumn<MusicBand, java.time.LocalDate> establishmentCol = new TableColumn<>();
        this.establishmentCol = establishmentCol;
        establishmentCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getEstablishmentDate()));
        establishmentCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(java.time.LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item == null ? "—" : UiText.dateFormat(appState.getLocale()).format(
                            java.util.Date.from(item.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant())));
                    getStyleClass().add("date-cell");
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });
        establishmentCol.setComparator(Comparator.nullsLast(java.time.LocalDate::compareTo));
        establishmentCol.setText(UiText.text(appState.getLocale(), UiText.Key.FIELD_DATE));
        establishmentCol.setPrefWidth(110);

        TableColumn<MusicBand, java.util.Date> creationCol = new TableColumn<>();
        this.creationCol = creationCol;
        creationCol.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getCreationDate()));
        creationCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(java.util.Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item == null ? "—" : UiText.dateTimeFormat(appState.getLocale()).format(item));
                    getStyleClass().add("date-cell");
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });
        creationCol.setComparator(Comparator.nullsLast(java.util.Date::compareTo));
        creationCol.setText(UiText.text(appState.getLocale(), UiText.Key.FIELD_CREATION));
        creationCol.setPrefWidth(150);

        TableColumn<MusicBand, Void> actionsCol = new TableColumn<>();
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("✎");
            private final Button deleteBtn = new Button("🗑");
            private final HBox box = new HBox(4, editBtn, deleteBtn);

            {
                editBtn.getStyleClass().addAll("action-button");
                deleteBtn.getStyleClass().addAll("action-button", "delete-action");
                box.setAlignment(Pos.CENTER_RIGHT);
                editBtn.setOnAction(e -> {
                    MusicBand band = getTableRow() == null ? null : getTableRow().getItem();
                    if (band != null) fireEvent(new BandEditEvent(band));
                });
                deleteBtn.setOnAction(e -> {
                    MusicBand band = getTableRow() == null ? null : getTableRow().getItem();
                    if (band != null) fireEvent(new BandDeleteEvent(band));
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    MusicBand band = getTableRow().getItem();
                    String currentUser = appState.getCurrentUser();
                    boolean isOwner = band.getOwnerLogin() != null && band.getOwnerLogin().equals(currentUser);
                    editBtn.setVisible(isOwner);
                    editBtn.setManaged(isOwner);
                    deleteBtn.setVisible(isOwner);
                    deleteBtn.setManaged(isOwner);
                    if (isOwner) {
                        getStyleClass().add("actions-cell");
                        setGraphic(box);
                    } else {
                        setGraphic(null);
                    }
                    setText(null);
                }
            }
        });
        actionsCol.setPrefWidth(100);
        actionsCol.setSortable(false);

        getColumns().addAll(idCol, colorCol, nameCol, ownerCol, xCol, yCol, participantsCol, singlesCol, genreCol, studioCol, establishmentCol, creationCol, actionsCol);
    }

    private void updateEmptyState() {
        if (getItems().isEmpty() && !appState.getItems().isEmpty()) {
            setPlaceholder(emptyStateLabel());
        } else if (appState.getItems().isEmpty()) {
            setPlaceholder(emptyStateLabel());
        }
    }

    private Label emptyStateLabel() {
        Label label = new Label(UiText.text(appState.getLocale(), UiText.Key.NO_BANDS));
        label.getStyleClass().add("empty-state");
        return label;
    }

    public void applyTheme(String theme) {
        getStyleClass().removeAll("theme-light", "theme-dark");
        getStyleClass().add("theme-" + theme);
    }

    public void refresh() {
        updateEmptyState();
    }

    public MusicBand getSelectedBand() {
        return getSelectionModel().getSelectedItem();
    }

    private String formatInteger(Number value) {
        if (value == null) return "—";
        return UiText.integerFormat(appState.getLocale()).format(value);
    }

    private String formatDecimal(Number value, int fractionDigits) {
        if (value == null) return "—";
        return UiText.decimalFormat(appState.getLocale(), fractionDigits, fractionDigits).format(value);
    }

    public static class BandEditEvent extends javafx.event.Event {
        public static final javafx.event.EventType<BandEditEvent> EDIT = new javafx.event.EventType<>(javafx.event.Event.ANY, "BAND_EDIT");
        private final MusicBand band;

        public BandEditEvent(MusicBand band) {
            super(EDIT);
            this.band = band;
        }

        public MusicBand getBand() {
            return band;
        }
    }

    public static class BandDeleteEvent extends javafx.event.Event {
        public static final javafx.event.EventType<BandDeleteEvent> DELETE = new javafx.event.EventType<>(javafx.event.Event.ANY, "BAND_DELETE");
        private final MusicBand band;

        public BandDeleteEvent(MusicBand band) {
            super(DELETE);
            this.band = band;
        }

        public MusicBand getBand() {
            return band;
        }
    }

    public static class BandRemoveGreaterEvent extends javafx.event.Event {
        public static final javafx.event.EventType<BandRemoveGreaterEvent> REMOVE_GREATER =
                new javafx.event.EventType<>(javafx.event.Event.ANY, "BAND_REMOVE_GREATER");
        private final MusicBand band;
        public BandRemoveGreaterEvent(MusicBand band) { super(REMOVE_GREATER); this.band = band; }
        public MusicBand getBand() { return band; }
    }

    public static class BandRemoveLowerEvent extends javafx.event.Event {
        public static final javafx.event.EventType<BandRemoveLowerEvent> REMOVE_LOWER =
                new javafx.event.EventType<>(javafx.event.Event.ANY, "BAND_REMOVE_LOWER");
        private final MusicBand band;
        public BandRemoveLowerEvent(MusicBand band) { super(REMOVE_LOWER); this.band = band; }
        public MusicBand getBand() { return band; }
    }

    private void attachContextMenu() {
        setRowFactory(tv -> {
            javafx.scene.control.TableRow<MusicBand> row = new javafx.scene.control.TableRow<>() {
                @Override
                protected void updateItem(MusicBand item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setContextMenu(null);
                    }
                }
            };
            row.setOnContextMenuRequested(e -> {
                MusicBand band = row.getItem();
                if (band == null) return;
                String currentUser = appState.getCurrentUser();
                boolean isOwner = band.getOwnerLogin() != null && band.getOwnerLogin().equals(currentUser);
                Locale locale = appState.getLocale();

                javafx.scene.control.ContextMenu menu = new javafx.scene.control.ContextMenu();

                javafx.scene.control.MenuItem editItem = new javafx.scene.control.MenuItem(UiText.text(locale, UiText.Key.EDIT));
                editItem.setDisable(!isOwner);
                editItem.setOnAction(ev -> fireEvent(new BandEditEvent(band)));

                javafx.scene.control.MenuItem deleteItem = new javafx.scene.control.MenuItem(UiText.text(locale, UiText.Key.DELETE));
                deleteItem.setDisable(!isOwner);
                deleteItem.setOnAction(ev -> fireEvent(new BandDeleteEvent(band)));

                javafx.scene.control.MenuItem removeGreaterItem = new javafx.scene.control.MenuItem(UiText.text(locale, UiText.Key.COMMAND_REMOVE_GREATER));
                removeGreaterItem.setDisable(!isOwner);
                removeGreaterItem.setOnAction(ev -> fireEvent(new BandRemoveGreaterEvent(band)));

                javafx.scene.control.MenuItem removeLowerItem = new javafx.scene.control.MenuItem(UiText.text(locale, UiText.Key.COMMAND_REMOVE_LOWER));
                removeLowerItem.setDisable(!isOwner);
                removeLowerItem.setOnAction(ev -> fireEvent(new BandRemoveLowerEvent(band)));

                menu.getItems().addAll(editItem, deleteItem,
                        new javafx.scene.control.SeparatorMenuItem(),
                        removeGreaterItem, removeLowerItem);
                menu.show(row, e.getScreenX(), e.getScreenY());
            });
            return row;
        });
    }

    private static boolean containsCI(String haystack, String needleLower) {
        return haystack != null && haystack.toLowerCase().contains(needleLower);
    }
}
