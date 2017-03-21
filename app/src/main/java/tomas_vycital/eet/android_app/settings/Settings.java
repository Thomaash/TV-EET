package tomas_vycital.eet.android_app.settings;

import android.content.SharedPreferences;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.HashMap;

/**
 * Created by tom on 3.3.17.
 */

public class Settings {
    static final FilenameFilter keyFilter;
    static final FilenameFilter backupFilter;
    static final String keysDir = Environment.getExternalStorageDirectory().toString() + "/TV EET/Keys";
    static final String backupsDir = Environment.getExternalStorageDirectory().toString() + "/TV EET/Backups";
    static final HashMap<String, Object> defaults;
    static SharedPreferences prefs;

    static {
        defaults = new HashMap<>();
        defaults.put("DIC", "→NUTNO ZADAT←");
        defaults.put("heading", "");
        defaults.put("footing", "");
        defaults.put("receiptWidth", 32);
        defaults.put("server", Server.play.getID());
        defaults.put("verifying", true);
        defaults.put("codepage", 0);
        defaults.put("charset", Charset.ascii.getStr());
        defaults.put("keyFileName", null);

        keyFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.matches("^.*\\.p12$");
            }
        };
        backupFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.matches("^.*\\.json$");
            }
        };
    }

    public static void setup(SharedPreferences prefs) {
        Settings.prefs = prefs;
    }

    public static String getDIC() {
        return Settings.getString("DIC");
    }

    public static String getHeading() {
        return Settings.getString("heading");
    }

    public static String getFooting() {
        return Settings.getString("footing");
    }

    public static int getReceiptWidth() {
        return Settings.getInteger("receiptWidth");
    }

    public static String getIdPokl() {
        return Settings.getString("idPokl");
    }

    public static String getIdProvoz() {
        return Settings.getString("idProvoz");
    }

    public static String getLastMAC() {
        return Settings.getString("lastMAC");
    }

    public static void setLastMAC(String mac) {
        Settings.prefs.edit().putString("lastMAC", mac).apply();
    }

    static String getKeyName() {
        return Settings.getString("keyFileName");
    }

    private static String getKeyPath() {
        return Settings.keysDir + "/" + Settings.getKeyName();
    }

    public static FileInputStream getKeyIS() throws FileNotFoundException {
        return new FileInputStream(Settings.getKeyPath());
    }

    public static Server getServer() {
        return Server.fromID(Settings.prefs.getInt("server", (Integer) Settings.defaults.get("server")));
    }

    private static String getString(String id) {
        return Settings.prefs.getString(id, (String) Settings.defaults.get(id));
    }

    private static Integer getInteger(String id) {
        return Settings.prefs.getInt(id, (Integer) Settings.defaults.get(id));
    }

    private static Boolean getBoolean(String id) {
        return Settings.prefs.getBoolean(id, (Boolean) Settings.defaults.get(id));
    }

    public static String getItems() {
        return Settings.getString("items");
    }

    public static void setItems(String items) {
        Settings.prefs.edit().putString("items", items).apply();
    }

    public static boolean getVerifying() {
        return Settings.getBoolean("verifying");
    }

    public static String getModeStr() {
        return "běžný";
    }

    public static int getCodepage() {
        return Settings.getInteger("codepage");
    }

    public static Charset getCharset() {
        return Charset.fromStr(Settings.getString("charset"));
    }
}
