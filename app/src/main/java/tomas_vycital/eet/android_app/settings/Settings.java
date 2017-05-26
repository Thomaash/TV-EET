package tomas_vycital.eet.android_app.settings;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.util.HashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import tomas_vycital.eet.android_app.VAT;
import tomas_vycital.eet.android_app.error.UnreadableKeyPassword;
import tomas_vycital.eet.android_app.error.UnsupportedImportItemsVersion;
import tomas_vycital.eet.android_app.items.Items;

/**
 * Allows settings to be retrieved from anywhere in the app and saved from this package, items and last printer MAC can be saved from anywhere. has to be initialized with setup() before use.
 */
public class Settings {
    static final FilenameFilter keyFilter;
    static final FilenameFilter backupFilter;
    static final String keysDir = Environment.getExternalStorageDirectory().toString() + "/TV EET/Keys";
    static final String backupsDir = Environment.getExternalStorageDirectory().toString() + "/TV EET/Backups";
    static final HashMap<String, Object> defaults;
    static SharedPreferences prefs;
    static Encryption encryption;

    static {
        defaults = new HashMap<>();
        defaults.put("address", "");
        defaults.put("charset", Charset.ascii.getStr());
        defaults.put("codepage", 0);
        defaults.put("DIC", "→NUTNO ZADAT←");
        defaults.put("VAT0", 0);
        defaults.put("VAT1", 21);
        defaults.put("VAT2", 15);
        defaults.put("VAT3", 10);
        defaults.put("footing", "");
        defaults.put("heading", "");
        defaults.put("ICO", "");
        defaults.put("itemsImportURL", "https://pastebin.com/raw/eUZbZjTt");
        defaults.put("keyFileName", null);
        defaults.put("name", "");
        defaults.put("receiptWidth", 32);
        defaults.put("server", Server.play.getID());
        defaults.put("verifying", true);

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.encryption = new Encryption23();
        } else {
            Settings.encryption = new EncryptionNone();
        }
    }

    private static String getString(String id) {
        return Settings.prefs.getString(id, (String) Settings.defaults.get(id));
    }

    @NonNull
    private static Integer getInteger(String id) {
        return Settings.prefs.getInt(id, (Integer) Settings.defaults.get(id));
    }

    @NonNull
    private static Boolean getBoolean(String id) {
        return Settings.prefs.getBoolean(id, (Boolean) Settings.defaults.get(id));
    }

    /**
     * @return Daňové identifikační číslo
     */
    public static String getDIC() {
        return Settings.getString("DIC");
    }

    /**
     * @return Identifikační číslo osoby
     */
    public static String getICO() {
        return Settings.getString("ICO");
    }

    public static String getHeading() {
        return Settings.getString("heading");
    }

    public static String getFooting() {
        return Settings.getString("footing");
    }

    public static String getName() {
        return Settings.getString("name");
    }

    public static String getAddress() {
        return Settings.getString("address");
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

    public static String getKeyPassword() throws UnreadableKeyPassword {
        try {
            return Settings.encryption.decryptData(new IVE(new JSONObject(Settings.getString("keyPassword"))));
        } catch (UnrecoverableEntryException | NoSuchAlgorithmException | KeyStoreException | NoSuchPaddingException | NoSuchProviderException | IOException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | JSONException | InvalidAlgorithmParameterException | NullPointerException | IllegalArgumentException /* e.g. IV is null */ e) {
            throw new UnreadableKeyPassword();
        }
    }

    static String getItemsImportURL() {
        return Settings.getString("itemsImportURL");
    }

    static void importItems(String json) throws JSONException, UnsupportedImportItemsVersion {
        JSONObject obj = new JSONObject(json);
        int version = obj.getInt("version");
        switch (version) {
            case 1:
                new Items(json); // Throws an exception for invalid data
                Settings.setItems(json);
                break;
            default:
                throw new UnsupportedImportItemsVersion(version);
        }
    }

    public static int getVAT(VAT vat) {
        switch (vat) {
            case reduced2:
                return Settings.getInteger("VAT3");
            case reduced1:
                return Settings.getInteger("VAT2");
            case basic:
                return Settings.getInteger("VAT1");
            default:
                return Settings.getInteger("VAT0");
        }
    }
}
