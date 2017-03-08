package tomas_vycital.eet.android_app;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import java.util.HashMap;

/**
 * Created by tom on 3.3.17.
 */

public class Settings implements View.OnClickListener {
    private static HashMap<String, Object> defaults;
    private static SharedPreferences prefs;

    static {
        defaults = new HashMap<>();
        defaults.put("DIC", "→NUTNO ZADAT←");
        defaults.put("heading", "");
        defaults.put("footing", "");
        defaults.put("receiptWidth", 32);
        defaults.put("server", Server.play.getID());
        defaults.put("verifying", true);
    }

    private final View layout;

    Settings(LinearLayout settings) {
        this.layout = settings;

        ((EditText) this.layout.findViewById(R.id.setting_dic)).setText(Settings.getDIC());
        ((EditText) this.layout.findViewById(R.id.setting_heading)).setText(Settings.getHeading());
        ((EditText) this.layout.findViewById(R.id.setting_footing)).setText(Settings.getFooting());
        ((EditText) this.layout.findViewById(R.id.settings_receipt_width)).setText(String.valueOf(Settings.getReceiptWidth()));
        ((EditText) this.layout.findViewById(R.id.settings_id_pokl)).setText(Settings.getIdPokl());
        ((EditText) this.layout.findViewById(R.id.settings_id_provoz)).setText(Settings.getIdProvoz());
        switch (Settings.getServer()) {
            case play:
                ((RadioButton) this.layout.findViewById(R.id.settings_server_play)).setChecked(true);
                break;
            case prod:
                ((RadioButton) this.layout.findViewById(R.id.settings_server_prod)).setChecked(true);
                break;
        }
        ((Switch) this.layout.findViewById(R.id.settings_verifying)).setChecked(Settings.getVerifying());
    }

    static void setup(SharedPreferences prefs) {
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


    @SuppressLint("ApplySharedPref")
    @Override
    public void onClick(View v) {
        Snackbar.make(this.layout, "Ukládá se…", Snackbar.LENGTH_LONG).setAction("Action", null).show();

        SharedPreferences.Editor editor = Settings.prefs.edit();
        this.saveString(editor, R.id.setting_dic, "DIC");
        this.saveString(editor, R.id.setting_heading, "heading");
        this.saveString(editor, R.id.setting_footing, "footing");
        this.saveInteger(editor, R.id.settings_receipt_width, "receiptWidth");
        this.saveString(editor, R.id.settings_id_pokl, "idPokl");
        this.saveString(editor, R.id.settings_id_provoz, "idProvoz");
        switch (((RadioGroup) this.layout.findViewById(R.id.settings_server)).getCheckedRadioButtonId()) {
            case R.id.settings_server_play:
                editor.putInt("server", Server.play.getID());
                break;
            case R.id.settings_server_prod:
                editor.putInt("server", Server.prod.getID());
                break;
        }
        this.saveBoolean(editor, R.id.settings_verifying, "verifying");
        editor.commit();

        Snackbar.make(this.layout, "Uloženo", Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    private void saveBoolean(SharedPreferences.Editor editor, int rID, String prefID) {
        editor.putBoolean(prefID, ((Switch) this.layout.findViewById(rID)).isChecked());
    }

    private void saveString(SharedPreferences.Editor editor, int rID, String prefID) {
        editor.putString(prefID, ((EditText) this.layout.findViewById(rID)).getText().toString());
    }

    private void saveInteger(SharedPreferences.Editor editor, int rID, String prefID) {
        editor.putInt(prefID, Integer.valueOf(((EditText) this.layout.findViewById(rID)).getText().toString()));
    }

    public enum Server {
        play(0), prod(1);

        private final int id;

        Server(int id) {
            this.id = id;
        }

        public static Server fromID(int id) {
            for (Server server : Server.values()) {
                if (server.id == id) {
                    return server;
                }
            }
            return null;
        }

        public int getID() {
            return this.id;
        }

    }
}
