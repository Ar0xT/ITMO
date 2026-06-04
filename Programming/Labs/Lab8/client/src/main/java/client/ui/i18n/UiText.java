package client.ui.i18n;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class UiText {
    public enum Key {
        TITLE_ENTER,
        USERNAME,
        PASSWORD,
        LOGIN,
        REGISTER,
        THEME_LIGHT,
        THEME_SYSTEM,
        THEME_DARK,
        REQUIRED_FIELDS,
        APP_STATE_NOT_READY,
        LOGIN_FAILED_PREFIX
    }

    private static final Locale LOCALE_EN = Locale.ENGLISH;
    private static final Locale LOCALE_RU = new Locale("ru");
    private static final Locale LOCALE_TR = new Locale("tr");
    private static final Locale LOCALE_HR = new Locale("hr");
    private static final Locale LOCALE_ES_EC = new Locale("es", "EC");

    private static final Map<Locale, Map<Key, String>> STRINGS = build();

    private UiText() {
    }

    public static Locale localeEn() {
        return LOCALE_EN;
    }

    public static Locale localeRu() {
        return LOCALE_RU;
    }

    public static Locale localeTr() {
        return LOCALE_TR;
    }

    public static Locale localeHr() {
        return LOCALE_HR;
    }

    public static Locale localeEsEc() {
        return LOCALE_ES_EC;
    }

    public static String text(Locale locale, Key key) {
        Map<Key, String> map = STRINGS.getOrDefault(locale, STRINGS.get(LOCALE_EN));
        return map.getOrDefault(key, STRINGS.get(LOCALE_EN).getOrDefault(key, ""));
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
        map.put(Key.USERNAME, "Username");
        map.put(Key.PASSWORD, "Password");
        map.put(Key.LOGIN, "Login");
        map.put(Key.REGISTER, "Register");
        map.put(Key.THEME_LIGHT, "Light");
        map.put(Key.THEME_SYSTEM, "System");
        map.put(Key.THEME_DARK, "Dark");
        map.put(Key.REQUIRED_FIELDS, "Login and password are required.");
        map.put(Key.APP_STATE_NOT_READY, "Application state is not ready.");
        map.put(Key.LOGIN_FAILED_PREFIX, "Login failed: ");
        return map;
    }

    private static Map<Key, String> mapRu() {
        Map<Key, String> map = new HashMap<>();
        map.put(Key.TITLE_ENTER, "Вход");
        map.put(Key.USERNAME, "Имя пользователя");
        map.put(Key.PASSWORD, "Пароль");
        map.put(Key.LOGIN, "Войти");
        map.put(Key.REGISTER, "Регистрация");
        map.put(Key.THEME_LIGHT, "Светлая");
        map.put(Key.THEME_SYSTEM, "Системная");
        map.put(Key.THEME_DARK, "Темная");
        map.put(Key.REQUIRED_FIELDS, "Логин и пароль обязательны.");
        map.put(Key.APP_STATE_NOT_READY, "Состояние приложения не готово.");
        map.put(Key.LOGIN_FAILED_PREFIX, "Ошибка входа: ");
        return map;
    }

    private static Map<Key, String> mapTr() {
        Map<Key, String> map = new HashMap<>();
        map.put(Key.TITLE_ENTER, "Giriş");
        map.put(Key.USERNAME, "Kullanıcı Adı");
        map.put(Key.PASSWORD, "Şifre");
        map.put(Key.LOGIN, "Giriş Yap");
        map.put(Key.REGISTER, "Kayıt Ol");
        map.put(Key.THEME_LIGHT, "Açık");
        map.put(Key.THEME_SYSTEM, "Sistem");
        map.put(Key.THEME_DARK, "Koyu");
        map.put(Key.REQUIRED_FIELDS, "Kullanıcı adı ve şifre gerekli.");
        map.put(Key.APP_STATE_NOT_READY, "Uygulama durumu hazır değil.");
        map.put(Key.LOGIN_FAILED_PREFIX, "Giriş başarısız: ");
        return map;
    }

    private static Map<Key, String> mapHr() {
        Map<Key, String> map = new HashMap<>();
        map.put(Key.TITLE_ENTER, "Prijava");
        map.put(Key.USERNAME, "Korisničko ime");
        map.put(Key.PASSWORD, "Lozinka");
        map.put(Key.LOGIN, "Prijava");
        map.put(Key.REGISTER, "Registracija");
        map.put(Key.THEME_LIGHT, "Svijetla");
        map.put(Key.THEME_SYSTEM, "Sustav");
        map.put(Key.THEME_DARK, "Tamna");
        map.put(Key.REQUIRED_FIELDS, "Korisničko ime i lozinka su obavezni.");
        map.put(Key.APP_STATE_NOT_READY, "Stanje aplikacije nije spremno.");
        map.put(Key.LOGIN_FAILED_PREFIX, "Prijava neuspjela: ");
        return map;
    }

    private static Map<Key, String> mapEsEc() {
        Map<Key, String> map = new HashMap<>();
        map.put(Key.TITLE_ENTER, "Iniciar sesión");
        map.put(Key.USERNAME, "Nombre de usuario");
        map.put(Key.PASSWORD, "Contraseña");
        map.put(Key.LOGIN, "Iniciar sesión");
        map.put(Key.REGISTER, "Registrarse");
        map.put(Key.THEME_LIGHT, "Claro");
        map.put(Key.THEME_SYSTEM, "Sistema");
        map.put(Key.THEME_DARK, "Oscuro");
        map.put(Key.REQUIRED_FIELDS, "Usuario y contraseña son obligatorios.");
        map.put(Key.APP_STATE_NOT_READY, "El estado de la aplicación no está listo.");
        map.put(Key.LOGIN_FAILED_PREFIX, "Error de inicio de sesión: ");
        return map;
    }
}
