package client.ui.i18n;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class UiText {
    public enum Key {
        TITLE_ENTER,
        SUBTITLE_SIGN_IN,
        USERNAME,
        PASSWORD,
        LOGIN,
        REGISTER,
        THEME_LIGHT,
        THEME_DARK,
        REQUIRED_FIELDS,
        APP_STATE_NOT_READY,
        LOGIN_FAILED_PREFIX,
        ITEMS,
        VISUALIZATION,
        FILTER,
        DETAILS,
        WELCOME,
        LOGOUT,
        LANGUAGE,
        DASHBOARD,
        SETTINGS,
        ADD,
        EDIT,
        DELETE,
        NAME,
        OWNER,
        COORD_X,
        COORD_Y,
        PARTICIPANTS,
        SINGLES_COUNT,
        ESTABLISHMENT_DATE,
        GENRE,
        STUDIO,
        CREATION_DATE,
        SAVE,
        RUN,
        CANCEL,
        SYNCING,
        OWN,
        ID,
        FIELD_ID,
        FIELD_NAME,
        FIELD_OWNER,
        FIELD_X,
        FIELD_Y,
        FIELD_PARTICIPANTS,
        FIELD_SINGLES,
        FIELD_DATE,
        FIELD_GENRE,
        FIELD_STUDIO,
        FIELD_CREATION,
        COORDINATES,
        NO_BANDS,
        COMMANDS,
        COMMAND_HELP,
        COMMAND_INFO,
        COMMAND_SHOW,
        COMMAND_CLEAR,
        COMMAND_INSERT,
        COMMAND_UPDATE,
        COMMAND_REMOVE_KEY,
        COMMAND_REMOVE_GREATER,
        COMMAND_REMOVE_GREATER_KEY,
        COMMAND_REMOVE_LOWER,
        COMMAND_COUNT_BY_PARTICIPANTS,
        COMMAND_GROUP_BY_NAME,
        COMMAND_PRINT_SINGLES_DESC,
        KEY_LABEL,
        VALUE_LABEL,
        CONFIRM_DELETE_TITLE,
        CONFIRM_DELETE_MESSAGE,
        LOGIN_REQUIRED,
        OPERATION_FAILED,
        OPERATION_SUCCESS,
        VALIDATION_NAME_REQUIRED,
        VALIDATION_X_NOT_NUMBER,
        VALIDATION_X_RANGE,
        VALIDATION_Y_NOT_INTEGER,
        VALIDATION_PARTICIPANTS_NOT_NUMBER,
        VALIDATION_PARTICIPANTS_RANGE,
        VALIDATION_SINGLES_NOT_NUMBER,
        VALIDATION_SINGLES_RANGE,
        VALIDATION_GENERIC_ERROR,
        HELP_TITLE,
        HELP_SUBTITLE,
        HELP_ABOUT,
        CONN_CONNECTED,
        CONN_CONNECTING,
        CONN_DISCONNECTED,
        TOAST_BANDS_ADDED,
        TOAST_BANDS_REMOVED,
        TOAST_SYNC_OK
    }

    private static final Locale LOCALE_EN = Locale.ENGLISH;
    private static final Locale LOCALE_RU = new Locale("ru");
    private static final Locale LOCALE_TR = new Locale("tr");
    private static final Locale LOCALE_HR = new Locale("hr");
    private static final Locale LOCALE_ES_EC = new Locale("es", "EC");

    private static final Map<Locale, Map<Key, String>> STRINGS = build();


    public static Locale localeEn() { return LOCALE_EN; }
    public static Locale localeRu() { return LOCALE_RU; }
    public static Locale localeTr() { return LOCALE_TR; }
    public static Locale localeHr() { return LOCALE_HR; }
    public static Locale localeEsEc() { return LOCALE_ES_EC; }

    public static Locale localeFromCode(String code) {
        if (code == null) return LOCALE_EN;
        return switch (code) {
            case "ru" -> LOCALE_RU;
            case "tr" -> LOCALE_TR;
            case "hr" -> LOCALE_HR;
            case "es-EC" -> LOCALE_ES_EC;
            default -> LOCALE_EN;
        };
    }

    public static String codeFromLocale(Locale locale) {
        if (locale == null) return "en";
        String lang = locale.getLanguage();
        String country = locale.getCountry();
        if ("es".equals(lang) && "EC".equals(country)) return "es-EC";
        return lang;
    }

    public static String text(Locale locale, Key key) {
        Map<Key, String> map = STRINGS.getOrDefault(locale, STRINGS.get(LOCALE_EN));
        return map.getOrDefault(key, STRINGS.get(LOCALE_EN).getOrDefault(key, ""));
    }

    public static NumberFormat numberFormat(Locale locale) {
        return NumberFormat.getNumberInstance(locale);
    }

    public static NumberFormat integerFormat(Locale locale) {
        return NumberFormat.getIntegerInstance(locale);
    }

    public static DateFormat dateFormat(Locale locale) {
        return DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
    }

    public static DateFormat dateTimeFormat(Locale locale) {
        return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, locale);
    }

    public static NumberFormat decimalFormat(Locale locale, int minFraction, int maxFraction) {
        NumberFormat nf = NumberFormat.getNumberInstance(locale);
        nf.setMinimumFractionDigits(minFraction);
        nf.setMaximumFractionDigits(maxFraction);
        return nf;
    }

    private static Map<Locale, Map<Key, String>> build() {
        Map<Locale, Map<Key, String>> data = new HashMap<>();
        data.put(LOCALE_EN, mapEn());
        data.put(LOCALE_RU, mapRu());
        data.put(LOCALE_TR, mapTr());
        data.put(LOCALE_HR, mapHr());
        data.put(LOCALE_ES_EC, mapEsEc());
        return data;
    }

    private static Map<Key, String> mapEn() {
        Map<Key, String> map = new HashMap<>();
        map.put(Key.TITLE_ENTER, "Enter Platform");
        map.put(Key.SUBTITLE_SIGN_IN, "Sign in to manage your music collection");
        map.put(Key.USERNAME, "Username");
        map.put(Key.PASSWORD, "Password");
        map.put(Key.LOGIN, "Login");
        map.put(Key.REGISTER, "Register");
        map.put(Key.THEME_LIGHT, "Light");
        map.put(Key.THEME_DARK, "Dark");
        map.put(Key.REQUIRED_FIELDS, "Login and password are required.");
        map.put(Key.APP_STATE_NOT_READY, "Application state is not ready.");
        map.put(Key.LOGIN_FAILED_PREFIX, "Login failed: ");
        map.put(Key.ITEMS, "Music Bands");
        map.put(Key.VISUALIZATION, "Visualization Area");
        map.put(Key.FILTER, "Filter by name...");
        map.put(Key.DETAILS, "Band Details");
        map.put(Key.WELCOME, "Welcome");
        map.put(Key.LOGOUT, "Log out");
        map.put(Key.LANGUAGE, "Language");
        map.put(Key.DASHBOARD, "Dashboard");
        map.put(Key.SETTINGS, "Settings");
        map.put(Key.ADD, "New Band");
        map.put(Key.EDIT, "Edit");
        map.put(Key.DELETE, "Delete");
        map.put(Key.NAME, "Name");
        map.put(Key.OWNER, "Owner");
        map.put(Key.COORD_X, "Coord X");
        map.put(Key.COORD_Y, "Coord Y");
        map.put(Key.PARTICIPANTS, "Participants");
        map.put(Key.SINGLES_COUNT, "Singles");
        map.put(Key.ESTABLISHMENT_DATE, "Est. Date");
        map.put(Key.GENRE, "Genre");
        map.put(Key.STUDIO, "Studio");
        map.put(Key.CREATION_DATE, "Added");
        map.put(Key.SAVE, "Save Changes");
        map.put(Key.RUN, "Run");
        map.put(Key.CANCEL, "Cancel");
        map.put(Key.SYNCING, "Live Sync");
        map.put(Key.OWN, "You");
        map.put(Key.ID, "ID");
        map.put(Key.FIELD_ID, "ID");
        map.put(Key.FIELD_NAME, "Name");
        map.put(Key.FIELD_OWNER, "Owner");
        map.put(Key.FIELD_X, "Coord X");
        map.put(Key.FIELD_Y, "Coord Y");
        map.put(Key.FIELD_PARTICIPANTS, "Participants");
        map.put(Key.FIELD_SINGLES, "Singles");
        map.put(Key.FIELD_DATE, "Est. Date");
        map.put(Key.FIELD_GENRE, "Genre");
        map.put(Key.FIELD_STUDIO, "Studio");
        map.put(Key.FIELD_CREATION, "Added");
        map.put(Key.COORDINATES, "Coordinates");
        map.put(Key.NO_BANDS, "No bands in collection.");
        map.put(Key.COMMANDS, "Commands");
        map.put(Key.COMMAND_HELP, "Help — list all commands");
        map.put(Key.COMMAND_INFO, "Info — collection statistics");
        map.put(Key.COMMAND_SHOW, "Show — list all bands");
        map.put(Key.COMMAND_CLEAR, "Clear — remove all of your bands");
        map.put(Key.COMMAND_INSERT, "Insert — add a new band (via Add button)");
        map.put(Key.COMMAND_UPDATE, "Update — edit a band (via Edit button)");
        map.put(Key.COMMAND_REMOVE_KEY, "Remove by key — enter a key");
        map.put(Key.COMMAND_REMOVE_GREATER, "Remove greater — right-click a band");
        map.put(Key.COMMAND_REMOVE_GREATER_KEY, "Remove greater key — enter a key");
        map.put(Key.COMMAND_REMOVE_LOWER, "Remove lower — right-click a band");
        map.put(Key.COMMAND_COUNT_BY_PARTICIPANTS, "Count by participants — enter a number");
        map.put(Key.COMMAND_GROUP_BY_NAME, "Group by name");
        map.put(Key.COMMAND_PRINT_SINGLES_DESC, "Print singles (descending)");
        map.put(Key.KEY_LABEL, "Key");
        map.put(Key.VALUE_LABEL, "Value");
        map.put(Key.CONFIRM_DELETE_TITLE, "Confirm delete");
        map.put(Key.CONFIRM_DELETE_MESSAGE, "Delete this band? This cannot be undone.");
        map.put(Key.LOGIN_REQUIRED, "Please log in to perform this action.");
        map.put(Key.OPERATION_FAILED, "Operation failed: ");
        map.put(Key.OPERATION_SUCCESS, "Done");
        map.put(Key.VALIDATION_NAME_REQUIRED, "Name is required.");
        map.put(Key.VALIDATION_X_NOT_NUMBER, "Coord X must be a number.");
        map.put(Key.VALIDATION_X_RANGE, "Coord X must be > -431.");
        map.put(Key.VALIDATION_Y_NOT_INTEGER, "Coord Y must be an integer.");
        map.put(Key.VALIDATION_PARTICIPANTS_NOT_NUMBER, "Participants must be a number.");
        map.put(Key.VALIDATION_PARTICIPANTS_RANGE, "Participants must be > 0.");
        map.put(Key.VALIDATION_SINGLES_NOT_NUMBER, "Singles must be a number.");
        map.put(Key.VALIDATION_SINGLES_RANGE, "Singles must be > 0.");
        map.put(Key.VALIDATION_GENERIC_ERROR, "Error: ");
        map.put(Key.HELP_TITLE, "Commands");
        map.put(Key.HELP_SUBTITLE, "All server commands available in this session. Use the Commands palette or keyboard shortcuts to run them.");
        map.put(Key.HELP_ABOUT, "BandManager — JavaFX client for the Lab 8 collection manager. Lab coursework by Ariunbat.");
        map.put(Key.CONN_CONNECTED, "Live");
        map.put(Key.CONN_CONNECTING, "Connecting…");
        map.put(Key.CONN_DISCONNECTED, "Offline");
        map.put(Key.TOAST_BANDS_ADDED, "%d new band(s) synced");
        map.put(Key.TOAST_BANDS_REMOVED, "%d band(s) removed");
        map.put(Key.TOAST_SYNC_OK, "Up to date");
        return map;
    }

    private static Map<Key, String> mapRu() {
        Map<Key, String> map = new HashMap<>();
        map.put(Key.TITLE_ENTER, "Вход");
        map.put(Key.SUBTITLE_SIGN_IN, "Войдите, чтобы управлять своей музыкальной коллекцией");
        map.put(Key.USERNAME, "Имя пользователя");
        map.put(Key.PASSWORD, "Пароль");
        map.put(Key.LOGIN, "Войти");
        map.put(Key.REGISTER, "Регистрация");
        map.put(Key.THEME_LIGHT, "Светлая");
        map.put(Key.THEME_DARK, "Темная");
        map.put(Key.REQUIRED_FIELDS, "Логин и пароль обязательны.");
        map.put(Key.APP_STATE_NOT_READY, "Состояние приложения не готово.");
        map.put(Key.LOGIN_FAILED_PREFIX, "Ошибка входа: ");
        map.put(Key.ITEMS, "Музыкальные группы");
        map.put(Key.VISUALIZATION, "Область визуализации");
        map.put(Key.FILTER, "Фильтр по имени...");
        map.put(Key.DETAILS, "Детали группы");
        map.put(Key.WELCOME, "Добро пожаловать");
        map.put(Key.LOGOUT, "Выйти");
        map.put(Key.LANGUAGE, "Язык");
        map.put(Key.DASHBOARD, "Панель управления");
        map.put(Key.SETTINGS, "Настройки");
        map.put(Key.ADD, "Новая группа");
        map.put(Key.EDIT, "Изменить");
        map.put(Key.DELETE, "Удалить");
        map.put(Key.NAME, "Название");
        map.put(Key.OWNER, "Владелец");
        map.put(Key.COORD_X, "Коорд. X");
        map.put(Key.COORD_Y, "Коорд. Y");
        map.put(Key.PARTICIPANTS, "Участники");
        map.put(Key.SINGLES_COUNT, "Синглы");
        map.put(Key.ESTABLISHMENT_DATE, "Дата осн.");
        map.put(Key.GENRE, "Жанр");
        map.put(Key.STUDIO, "Студия");
        map.put(Key.CREATION_DATE, "Добавлено");
        map.put(Key.SAVE, "Сохранить");
        map.put(Key.RUN, "Выполнить");
        map.put(Key.CANCEL, "Отмена");
        map.put(Key.SYNCING, "Синхронизация");
        map.put(Key.OWN, "Вы");
        map.put(Key.ID, "ID");
        map.put(Key.FIELD_ID, "ID");
        map.put(Key.FIELD_NAME, "Название");
        map.put(Key.FIELD_OWNER, "Владелец");
        map.put(Key.FIELD_X, "Коорд. X");
        map.put(Key.FIELD_Y, "Коорд. Y");
        map.put(Key.FIELD_PARTICIPANTS, "Участники");
        map.put(Key.FIELD_SINGLES, "Синглы");
        map.put(Key.FIELD_DATE, "Дата осн.");
        map.put(Key.FIELD_GENRE, "Жанр");
        map.put(Key.FIELD_STUDIO, "Студия");
        map.put(Key.FIELD_CREATION, "Добавлено");
        map.put(Key.COORDINATES, "Координаты");
        map.put(Key.NO_BANDS, "В коллекции нет групп.");
        map.put(Key.COMMANDS, "Команды");
        map.put(Key.COMMAND_HELP, "Help — список всех команд");
        map.put(Key.COMMAND_INFO, "Info — статистика коллекции");
        map.put(Key.COMMAND_SHOW, "Show — показать все группы");
        map.put(Key.COMMAND_CLEAR, "Clear — удалить все ваши группы");
        map.put(Key.COMMAND_INSERT, "Insert — добавить группу (кнопка «Добавить»)");
        map.put(Key.COMMAND_UPDATE, "Update — изменить группу (кнопка «Изменить»)");
        map.put(Key.COMMAND_REMOVE_KEY, "Remove by key — введите ключ");
        map.put(Key.COMMAND_REMOVE_GREATER, "Remove greater — правый клик по группе");
        map.put(Key.COMMAND_REMOVE_GREATER_KEY, "Remove greater key — введите ключ");
        map.put(Key.COMMAND_REMOVE_LOWER, "Remove lower — правый клик по группе");
        map.put(Key.COMMAND_COUNT_BY_PARTICIPANTS, "Count by participants — введите число");
        map.put(Key.COMMAND_GROUP_BY_NAME, "Group by name");
        map.put(Key.COMMAND_PRINT_SINGLES_DESC, "Print singles (по убыванию)");
        map.put(Key.KEY_LABEL, "Ключ");
        map.put(Key.VALUE_LABEL, "Значение");
        map.put(Key.CONFIRM_DELETE_TITLE, "Подтвердите удаление");
        map.put(Key.CONFIRM_DELETE_MESSAGE, "Удалить эту группу? Действие необратимо.");
        map.put(Key.LOGIN_REQUIRED, "Войдите, чтобы выполнить действие.");
        map.put(Key.OPERATION_FAILED, "Ошибка: ");
        map.put(Key.OPERATION_SUCCESS, "Готово");
        map.put(Key.VALIDATION_NAME_REQUIRED, "Поле «Название» обязательно.");
        map.put(Key.VALIDATION_X_NOT_NUMBER, "Координата X должна быть числом.");
        map.put(Key.VALIDATION_X_RANGE, "Координата X должна быть > -431.");
        map.put(Key.VALIDATION_Y_NOT_INTEGER, "Координата Y должна быть целым числом.");
        map.put(Key.VALIDATION_PARTICIPANTS_NOT_NUMBER, "Количество участников должно быть числом.");
        map.put(Key.VALIDATION_PARTICIPANTS_RANGE, "Количество участников должно быть > 0.");
        map.put(Key.VALIDATION_SINGLES_NOT_NUMBER, "Количество синглов должно быть числом.");
        map.put(Key.VALIDATION_SINGLES_RANGE, "Количество синглов должно быть > 0.");
        map.put(Key.VALIDATION_GENERIC_ERROR, "Ошибка: ");
        map.put(Key.HELP_TITLE, "Команды");
        map.put(Key.HELP_SUBTITLE, "Все серверные команды, доступные в этой сессии. Используйте палитру команд или горячие клавиши.");
        map.put(Key.HELP_ABOUT, "BandManager — JavaFX-клиент для менеджера коллекции (лабораторная №8). Курсовая работа Ариунбата.");
        map.put(Key.CONN_CONNECTED, "В сети");
        map.put(Key.CONN_CONNECTING, "Подключение…");
        map.put(Key.CONN_DISCONNECTED, "Не в сети");
        map.put(Key.TOAST_BANDS_ADDED, "Синхронизировано групп: %d");
        map.put(Key.TOAST_BANDS_REMOVED, "Удалено групп: %d");
        map.put(Key.TOAST_SYNC_OK, "Актуально");
        return map;
    }

    private static Map<Key, String> mapTr() {
        Map<Key, String> map = new HashMap<>();
        map.put(Key.TITLE_ENTER, "Giriş");
        map.put(Key.SUBTITLE_SIGN_IN, "Müzik koleksiyonunuzu yönetmek için giriş yapın");
        map.put(Key.USERNAME, "Kullanıcı Adı");
        map.put(Key.PASSWORD, "Şifre");
        map.put(Key.LOGIN, "Giriş Yap");
        map.put(Key.REGISTER, "Kayıt Ol");
        map.put(Key.THEME_LIGHT, "Açık");
        map.put(Key.THEME_DARK, "Koyu");
        map.put(Key.REQUIRED_FIELDS, "Kullanıcı adı ve şifre gerekli.");
        map.put(Key.APP_STATE_NOT_READY, "Uygulama durumu hazır değil.");
        map.put(Key.LOGIN_FAILED_PREFIX, "Giriş başarısız: ");
        map.put(Key.ITEMS, "Müzik Grupları");
        map.put(Key.VISUALIZATION, "Görselleştirme Alanı");
        map.put(Key.FILTER, "İsme göre filtrele...");
        map.put(Key.DETAILS, "Grup Detayları");
        map.put(Key.WELCOME, "Hoş geldiniz");
        map.put(Key.LOGOUT, "Çıkış Yap");
        map.put(Key.LANGUAGE, "Dil");
        map.put(Key.DASHBOARD, "Kontrol Paneli");
        map.put(Key.SETTINGS, "Ayarlar");
        map.put(Key.ADD, "Yeni Grup");
        map.put(Key.EDIT, "Düzenle");
        map.put(Key.DELETE, "Sil");
        map.put(Key.NAME, "İsim");
        map.put(Key.OWNER, "Sahip");
        map.put(Key.COORD_X, "X Koor");
        map.put(Key.COORD_Y, "Y Koor");
        map.put(Key.PARTICIPANTS, "Katılımcılar");
        map.put(Key.SINGLES_COUNT, "Single Sayısı");
        map.put(Key.ESTABLISHMENT_DATE, "Kuruluş Tarihi");
        map.put(Key.GENRE, "Tür");
        map.put(Key.STUDIO, "Stüdyo");
        map.put(Key.CREATION_DATE, "Eklendi");
        map.put(Key.SAVE, "Değişiklikleri Kaydet");
        map.put(Key.RUN, "Çalıştır");
        map.put(Key.CANCEL, "İptal");
        map.put(Key.SYNCING, "Senkronize");
        map.put(Key.OWN, "Siz");
        map.put(Key.ID, "ID");
        map.put(Key.FIELD_ID, "ID");
        map.put(Key.FIELD_NAME, "İsim");
        map.put(Key.FIELD_OWNER, "Sahip");
        map.put(Key.FIELD_X, "X Koor");
        map.put(Key.FIELD_Y, "Y Koor");
        map.put(Key.FIELD_PARTICIPANTS, "Katılımcılar");
        map.put(Key.FIELD_SINGLES, "Single Sayısı");
        map.put(Key.FIELD_DATE, "Kuruluş Tarihi");
        map.put(Key.FIELD_GENRE, "Tür");
        map.put(Key.FIELD_STUDIO, "Stüdyo");
        map.put(Key.FIELD_CREATION, "Eklendi");
        map.put(Key.COORDINATES, "Koordinatlar");
        map.put(Key.NO_BANDS, "Koleksiyonda grup yok.");
        map.put(Key.COMMANDS, "Komutlar");
        map.put(Key.COMMAND_HELP, "Help — tüm komutları listele");
        map.put(Key.COMMAND_INFO, "Info — koleksiyon istatistikleri");
        map.put(Key.COMMAND_SHOW, "Show — tüm grupları göster");
        map.put(Key.COMMAND_CLEAR, "Clear — kendi gruplarını sil");
        map.put(Key.COMMAND_INSERT, "Insert — yeni grup ekle (Ekle butonu)");
        map.put(Key.COMMAND_UPDATE, "Update — grup düzenle (Düzenle butonu)");
        map.put(Key.COMMAND_REMOVE_KEY, "Remove by key — bir anahtar gir");
        map.put(Key.COMMAND_REMOVE_GREATER, "Remove greater — gruba sağ tıkla");
        map.put(Key.COMMAND_REMOVE_GREATER_KEY, "Remove greater key — bir anahtar gir");
        map.put(Key.COMMAND_REMOVE_LOWER, "Remove lower — gruba sağ tıkla");
        map.put(Key.COMMAND_COUNT_BY_PARTICIPANTS, "Count by participants — bir sayı gir");
        map.put(Key.COMMAND_GROUP_BY_NAME, "Group by name");
        map.put(Key.COMMAND_PRINT_SINGLES_DESC, "Print singles (azalan)");
        map.put(Key.KEY_LABEL, "Anahtar");
        map.put(Key.VALUE_LABEL, "Değer");
        map.put(Key.CONFIRM_DELETE_TITLE, "Silmeyi onayla");
        map.put(Key.CONFIRM_DELETE_MESSAGE, "Bu grup silinsin mi? Geri alınamaz.");
        map.put(Key.LOGIN_REQUIRED, "Bu işlem için giriş yapmalısınız.");
        map.put(Key.OPERATION_FAILED, "İşlem başarısız: ");
        map.put(Key.OPERATION_SUCCESS, "Tamam");
        map.put(Key.VALIDATION_NAME_REQUIRED, "Ad alanı zorunludur.");
        map.put(Key.VALIDATION_X_NOT_NUMBER, "X koordinatı bir sayı olmalıdır.");
        map.put(Key.VALIDATION_X_RANGE, "X koordinatı > -431 olmalıdır.");
        map.put(Key.VALIDATION_Y_NOT_INTEGER, "Y koordinatı bir tam sayı olmalıdır.");
        map.put(Key.VALIDATION_PARTICIPANTS_NOT_NUMBER, "Katılımcı sayısı bir sayı olmalıdır.");
        map.put(Key.VALIDATION_PARTICIPANTS_RANGE, "Katılımcı sayısı > 0 olmalıdır.");
        map.put(Key.VALIDATION_SINGLES_NOT_NUMBER, "Single sayısı bir sayı olmalıdır.");
        map.put(Key.VALIDATION_SINGLES_RANGE, "Single sayısı > 0 olmalıdır.");
        map.put(Key.VALIDATION_GENERIC_ERROR, "Hata: ");
        map.put(Key.HELP_TITLE, "Komutlar");
        map.put(Key.HELP_SUBTITLE, "Bu oturumda kullanılabilen tüm sunucu komutları. Çalıştırmak için komut paletini veya klavye kısayollarını kullanın.");
        map.put(Key.HELP_ABOUT, "BandManager — Lab 8 koleksiyon yöneticisi için JavaFX istemcisi. Ariunbat tarafından laboratuvar ödevi.");
        map.put(Key.CONN_CONNECTED, "Çevrimiçi");
        map.put(Key.CONN_CONNECTING, "Bağlanıyor…");
        map.put(Key.CONN_DISCONNECTED, "Çevrimdışı");
        map.put(Key.TOAST_BANDS_ADDED, "%d yeni grup senkronize edildi");
        map.put(Key.TOAST_BANDS_REMOVED, "%d grup silindi");
        map.put(Key.TOAST_SYNC_OK, "Güncel");
        return map;
    }

    private static Map<Key, String> mapHr() {
        Map<Key, String> map = new HashMap<>();
        map.put(Key.TITLE_ENTER, "Prijava");
        map.put(Key.SUBTITLE_SIGN_IN, "Prijavite se za upravljanje glazbenom kolekcijom");
        map.put(Key.USERNAME, "Korisničko ime");
        map.put(Key.PASSWORD, "Lozinka");
        map.put(Key.LOGIN, "Prijava");
        map.put(Key.REGISTER, "Registracija");
        map.put(Key.THEME_LIGHT, "Svijetla");
        map.put(Key.THEME_DARK, "Tamna");
        map.put(Key.REQUIRED_FIELDS, "Korisničko ime i lozinka su obavezni.");
        map.put(Key.APP_STATE_NOT_READY, "Stanje aplikacije nije spremno.");
        map.put(Key.LOGIN_FAILED_PREFIX, "Prijava neuspjela: ");
        map.put(Key.ITEMS, "Glazbeni sastavi");
        map.put(Key.VISUALIZATION, "Područje vizualizacije");
        map.put(Key.FILTER, "Filtriraj po imenu...");
        map.put(Key.DETAILS, "Detalji sastava");
        map.put(Key.WELCOME, "Dobrodošli");
        map.put(Key.LOGOUT, "Odjava");
        map.put(Key.LANGUAGE, "Jezik");
        map.put(Key.DASHBOARD, "Nadzorna ploča");
        map.put(Key.SETTINGS, "Postavke");
        map.put(Key.ADD, "Novi sastav");
        map.put(Key.EDIT, "Uredi");
        map.put(Key.DELETE, "Obriši");
        map.put(Key.NAME, "Ime");
        map.put(Key.OWNER, "Vlasnik");
        map.put(Key.COORD_X, "X Koor");
        map.put(Key.COORD_Y, "Y Koor");
        map.put(Key.PARTICIPANTS, "Sudionici");
        map.put(Key.SINGLES_COUNT, "Broj singlova");
        map.put(Key.ESTABLISHMENT_DATE, "Datum osn.");
        map.put(Key.GENRE, "Žanr");
        map.put(Key.STUDIO, "Studio");
        map.put(Key.CREATION_DATE, "Dodano");
        map.put(Key.SAVE, "Spremi promjene");
        map.put(Key.RUN, "Pokreni");
        map.put(Key.CANCEL, "Odustani");
        map.put(Key.SYNCING, "Sinkronizacija");
        map.put(Key.OWN, "Vi");
        map.put(Key.ID, "ID");
        map.put(Key.FIELD_ID, "ID");
        map.put(Key.FIELD_NAME, "Ime");
        map.put(Key.FIELD_OWNER, "Vlasnik");
        map.put(Key.FIELD_X, "X Koor");
        map.put(Key.FIELD_Y, "Y Koor");
        map.put(Key.FIELD_PARTICIPANTS, "Sudionici");
        map.put(Key.FIELD_SINGLES, "Broj singlova");
        map.put(Key.FIELD_DATE, "Datum osn.");
        map.put(Key.FIELD_GENRE, "Žanr");
        map.put(Key.FIELD_STUDIO, "Studio");
        map.put(Key.FIELD_CREATION, "Dodano");
        map.put(Key.COORDINATES, "Koordinate");
        map.put(Key.NO_BANDS, "Nema sastava u kolekciji.");
        map.put(Key.COMMANDS, "Naredbe");
        map.put(Key.COMMAND_HELP, "Help — popis svih naredbi");
        map.put(Key.COMMAND_INFO, "Info — statistika kolekcije");
        map.put(Key.COMMAND_SHOW, "Show — prikaži sve sastave");
        map.put(Key.COMMAND_CLEAR, "Clear — obriši sve svoje sastave");
        map.put(Key.COMMAND_INSERT, "Insert — dodaj novi sastav (gumb Dodaj)");
        map.put(Key.COMMAND_UPDATE, "Update — uredi sastav (gumb Uredi)");
        map.put(Key.COMMAND_REMOVE_KEY, "Remove by key — unesi ključ");
        map.put(Key.COMMAND_REMOVE_GREATER, "Remove greater — desni klik na sastav");
        map.put(Key.COMMAND_REMOVE_GREATER_KEY, "Remove greater key — unesi ključ");
        map.put(Key.COMMAND_REMOVE_LOWER, "Remove lower — desni klik na sastav");
        map.put(Key.COMMAND_COUNT_BY_PARTICIPANTS, "Count by participants — unesi broj");
        map.put(Key.COMMAND_GROUP_BY_NAME, "Group by name");
        map.put(Key.COMMAND_PRINT_SINGLES_DESC, "Print singles (silazno)");
        map.put(Key.KEY_LABEL, "Ključ");
        map.put(Key.VALUE_LABEL, "Vrijednost");
        map.put(Key.CONFIRM_DELETE_TITLE, "Potvrdi brisanje");
        map.put(Key.CONFIRM_DELETE_MESSAGE, "Obriši ovaj sastav? Ova radnja je nepovratna.");
        map.put(Key.LOGIN_REQUIRED, "Prijavite se za ovu radnju.");
        map.put(Key.OPERATION_FAILED, "Radnja neuspjela: ");
        map.put(Key.OPERATION_SUCCESS, "Gotovo");
        map.put(Key.VALIDATION_NAME_REQUIRED, "Naziv je obavezan.");
        map.put(Key.VALIDATION_X_NOT_NUMBER, "X koordinata mora biti broj.");
        map.put(Key.VALIDATION_X_RANGE, "X koordinata mora biti > -431.");
        map.put(Key.VALIDATION_Y_NOT_INTEGER, "Y koordinata mora biti cijeli broj.");
        map.put(Key.VALIDATION_PARTICIPANTS_NOT_NUMBER, "Broj sudionika mora biti broj.");
        map.put(Key.VALIDATION_PARTICIPANTS_RANGE, "Broj sudionika mora biti > 0.");
        map.put(Key.VALIDATION_SINGLES_NOT_NUMBER, "Broj singlova mora biti broj.");
        map.put(Key.VALIDATION_SINGLES_RANGE, "Broj singlova mora biti > 0.");
        map.put(Key.VALIDATION_GENERIC_ERROR, "Greška: ");
        map.put(Key.HELP_TITLE, "Naredbe");
        map.put(Key.HELP_SUBTITLE, "Sve serverske naredbe dostupne u ovoj sesiji. Koristite paletu naredbi ili prečice na tipkovnici.");
        map.put(Key.HELP_ABOUT, "BandManager — JavaFX klijent za upravljač kolekcijom (Lab 8). Laboratorijski rad Ariunbata.");
        map.put(Key.CONN_CONNECTED, "Povezan");
        map.put(Key.CONN_CONNECTING, "Povezivanje…");
        map.put(Key.CONN_DISCONNECTED, "Nepovezan");
        map.put(Key.TOAST_BANDS_ADDED, "Sinkronizirano grupa: %d");
        map.put(Key.TOAST_BANDS_REMOVED, "Uklonjeno grupa: %d");
        map.put(Key.TOAST_SYNC_OK, "Ažurirano");
        return map;
    }

    private static Map<Key, String> mapEsEc() {
        Map<Key, String> map = new HashMap<>();
        map.put(Key.TITLE_ENTER, "Iniciar sesión");
        map.put(Key.SUBTITLE_SIGN_IN, "Inicia sesión para administrar tu colección de música");
        map.put(Key.USERNAME, "Nombre de usuario");
        map.put(Key.PASSWORD, "Contraseña");
        map.put(Key.LOGIN, "Iniciar sesión");
        map.put(Key.REGISTER, "Registrarse");
        map.put(Key.THEME_LIGHT, "Claro");
        map.put(Key.THEME_DARK, "Oscuro");
        map.put(Key.REQUIRED_FIELDS, "Usuario y contraseña son obligatorios.");
        map.put(Key.APP_STATE_NOT_READY, "El estado de la aplicación no está listo.");
        map.put(Key.LOGIN_FAILED_PREFIX, "Error de inicio de sesión: ");
        map.put(Key.ITEMS, "Bandas de Música");
        map.put(Key.VISUALIZATION, "Área de visualización");
        map.put(Key.FILTER, "Filtrar por nombre...");
        map.put(Key.DETAILS, "Detalles de la banda");
        map.put(Key.WELCOME, "Bienvenido");
        map.put(Key.LOGOUT, "Cerrar sesión");
        map.put(Key.LANGUAGE, "Idioma");
        map.put(Key.DASHBOARD, "Panel de control");
        map.put(Key.SETTINGS, "Configuración");
        map.put(Key.ADD, "Nueva Banda");
        map.put(Key.EDIT, "Editar");
        map.put(Key.DELETE, "Eliminar");
        map.put(Key.NAME, "Nombre");
        map.put(Key.OWNER, "Propietario");
        map.put(Key.COORD_X, "Coord X");
        map.put(Key.COORD_Y, "Coord Y");
        map.put(Key.PARTICIPANTS, "Participantes");
        map.put(Key.SINGLES_COUNT, "Sencillos");
        map.put(Key.ESTABLISHMENT_DATE, "Fecha Est.");
        map.put(Key.GENRE, "Género");
        map.put(Key.STUDIO, "Estudio");
        map.put(Key.CREATION_DATE, "Añadido");
        map.put(Key.SAVE, "Guardar cambios");
        map.put(Key.RUN, "Ejecutar");
        map.put(Key.CANCEL, "Cancelar");
        map.put(Key.SYNCING, "Sincronizando");
        map.put(Key.OWN, "Tú");
        map.put(Key.ID, "ID");
        map.put(Key.FIELD_ID, "ID");
        map.put(Key.FIELD_NAME, "Nombre");
        map.put(Key.FIELD_OWNER, "Propietario");
        map.put(Key.FIELD_X, "Coord X");
        map.put(Key.FIELD_Y, "Coord Y");
        map.put(Key.FIELD_PARTICIPANTS, "Participantes");
        map.put(Key.FIELD_SINGLES, "Sencillos");
        map.put(Key.FIELD_DATE, "Fecha Est.");
        map.put(Key.FIELD_GENRE, "Género");
        map.put(Key.FIELD_STUDIO, "Estudio");
        map.put(Key.FIELD_CREATION, "Añadido");
        map.put(Key.COORDINATES, "Coordenadas");
        map.put(Key.NO_BANDS, "No hay bandas en la colección.");
        map.put(Key.COMMANDS, "Comandos");
        map.put(Key.COMMAND_HELP, "Help — lista todos los comandos");
        map.put(Key.COMMAND_INFO, "Info — estadísticas de la colección");
        map.put(Key.COMMAND_SHOW, "Show — mostrar todas las bandas");
        map.put(Key.COMMAND_CLEAR, "Clear — eliminar todas tus bandas");
        map.put(Key.COMMAND_INSERT, "Insert — añadir banda (botón Añadir)");
        map.put(Key.COMMAND_UPDATE, "Update — editar banda (botón Editar)");
        map.put(Key.COMMAND_REMOVE_KEY, "Remove by key — ingresa una clave");
        map.put(Key.COMMAND_REMOVE_GREATER, "Remove greater — clic derecho en una banda");
        map.put(Key.COMMAND_REMOVE_GREATER_KEY, "Remove greater key — ingresa una clave");
        map.put(Key.COMMAND_REMOVE_LOWER, "Remove lower — clic derecho en una banda");
        map.put(Key.COMMAND_COUNT_BY_PARTICIPANTS, "Count by participants — ingresa un número");
        map.put(Key.COMMAND_GROUP_BY_NAME, "Group by name");
        map.put(Key.COMMAND_PRINT_SINGLES_DESC, "Print singles (descendente)");
        map.put(Key.KEY_LABEL, "Clave");
        map.put(Key.VALUE_LABEL, "Valor");
        map.put(Key.CONFIRM_DELETE_TITLE, "Confirmar eliminación");
        map.put(Key.CONFIRM_DELETE_MESSAGE, "¿Eliminar esta banda? No se puede deshacer.");
        map.put(Key.LOGIN_REQUIRED, "Inicia sesión para realizar esta acción.");
        map.put(Key.OPERATION_FAILED, "Operación fallida: ");
        map.put(Key.OPERATION_SUCCESS, "Listo");
        map.put(Key.VALIDATION_NAME_REQUIRED, "El nombre es obligatorio.");
        map.put(Key.VALIDATION_X_NOT_NUMBER, "La coordenada X debe ser un número.");
        map.put(Key.VALIDATION_X_RANGE, "La coordenada X debe ser > -431.");
        map.put(Key.VALIDATION_Y_NOT_INTEGER, "La coordenada Y debe ser un entero.");
        map.put(Key.VALIDATION_PARTICIPANTS_NOT_NUMBER, "Los participantes deben ser un número.");
        map.put(Key.VALIDATION_PARTICIPANTS_RANGE, "Los participantes deben ser > 0.");
        map.put(Key.VALIDATION_SINGLES_NOT_NUMBER, "Los singles deben ser un número.");
        map.put(Key.VALIDATION_SINGLES_RANGE, "Los singles deben ser > 0.");
        map.put(Key.VALIDATION_GENERIC_ERROR, "Error: ");
        map.put(Key.HELP_TITLE, "Comandos");
        map.put(Key.HELP_SUBTITLE, "Todos los comandos del servidor disponibles en esta sesión. Usa la paleta de comandos o los atajos de teclado.");
        map.put(Key.HELP_ABOUT, "BandManager — Cliente JavaFX para el gestor de colección (Lab 8). Trabajo de laboratorio de Ariunbat.");
        map.put(Key.CONN_CONNECTED, "En línea");
        map.put(Key.CONN_CONNECTING, "Conectando…");
        map.put(Key.CONN_DISCONNECTED, "Sin conexión");
        map.put(Key.TOAST_BANDS_ADDED, "%d banda(s) sincronizada(s)");
        map.put(Key.TOAST_BANDS_REMOVED, "%d banda(s) eliminada(s)");
        map.put(Key.TOAST_SYNC_OK, "Actualizado");
        return map;
    }
}
