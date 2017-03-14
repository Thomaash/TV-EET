package tomas_vycital.eet.android_app.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Writer;
import java.util.Calendar;
import java.util.TimeZone;

import tomas_vycital.eet.android_app.R;
import tomas_vycital.eet.android_app.printer.BTPrinter;

/**
 * Created by tom on 3.3.17.
 */

public class SettingsOCL implements View.OnClickListener {
    private final Context context;
    private final View layout;
    private final BTPrinter printer;

    public SettingsOCL(Context context, Button settingsButton, LinearLayout settings, BTPrinter printer) {
        this.context = context;
        this.layout = settings;
        this.printer = printer;

        this.load();

        // Onclick listeners
        settingsButton.setOnClickListener(this);
        this.layout.findViewById(R.id.settings_codepage_test).setOnClickListener(this);
        this.layout.findViewById(R.id.settings_backup).setOnClickListener(this);
        this.layout.findViewById(R.id.settings_restore).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        SharedPreferences.Editor editor;

        switch (v.getId()) {
            case R.id.settings_save:
                Snackbar.make(this.layout, "Ukládá se…", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                editor = Settings.prefs.edit();
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
                editor.putInt("codepage", this.getUnsavedCodepage());
                editor.putString("charset", this.getUnsavedCharset().getStr());
                editor.putString("keyFileName", ((RadioButton) this.layout.findViewById(((RadioGroup) this.layout.findViewById(R.id.settings_keys)).getCheckedRadioButtonId())).getText().toString());
                editor.apply();

                Snackbar.make(this.layout, "Uloženo", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                break;
            case R.id.settings_codepage_test:
                try {
                    this.printer.testCP(this.getUnsavedCodepage(), this.getUnsavedCharset());
                } catch (IOException ignored) {
                }
                break;
            case R.id.settings_backup:
                try {
                    (new File(Settings.backupsDir)).mkdirs();
                    File file = new File(Settings.backupsDir + "/" + String.format("%tFT%<tR%<tZ", Calendar.getInstance(TimeZone.getDefault())) + ".json");
                    Writer writer = new BufferedWriter(new FileWriter(file));
                    writer.write(
                            (new JSONObject(
                                    Settings.prefs.getAll()
                            )).toString()
                    );
                    writer.close();

                    Snackbar.make(this.layout, "Zazálohováno", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } catch (Exception e) {
                    Snackbar.make(this.layout, "Záloha se nezdařilo", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                break;
            case R.id.settings_restore:
                try {
                    String fileName = ((RadioButton) this.layout.findViewById(((RadioGroup) this.layout.findViewById(R.id.settings_backups)).getCheckedRadioButtonId())).getText().toString();
                    JsonReader reader = new JsonReader(new FileReader(Settings.backupsDir + "/" + fileName));

                    editor = Settings.prefs.edit();
                    reader.beginObject();
                    while (reader.hasNext()) {
                        String name = reader.nextName();
                        Object defaultValue = Settings.defaults.get(name);
                        if (defaultValue instanceof Integer) {
                            editor.putInt(name, reader.nextInt());
                        } else if (defaultValue instanceof Boolean) {
                            editor.putBoolean(name, reader.nextBoolean());
                        } else { // String
                            editor.putString(name, reader.nextString());
                        }
                    }
                    reader.endObject();
                    editor.commit();

                    this.load();

                    Snackbar.make(this.layout, "Obnoveno", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } catch (Exception e) {
                    Snackbar.make(this.layout, "Obnovení se nezdařilo", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                break;
        }
    }

    private void load() {
        // Settings values
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
        ((EditText) this.layout.findViewById(R.id.settings_codepage)).setText("" + Settings.getCodepage());
        switch (Settings.getCharset()) {
            case ascii:
                ((RadioButton) this.layout.findViewById(R.id.settings_charset_ascii)).setChecked(true);
                break;
            case iso88592:
                ((RadioButton) this.layout.findViewById(R.id.settings_charset_iso88592)).setChecked(true);
                break;
            case cp852:
                ((RadioButton) this.layout.findViewById(R.id.settings_charset_cp852)).setChecked(true);
                break;
            case windows1250:
                ((RadioButton) this.layout.findViewById(R.id.settings_charset_windows1250)).setChecked(true);
                break;
        }

        // Radio buttons
        int keyButtons = this.generateRadioButtons(R.id.settings_keys, Settings.keysDir, Settings.keyFilter, Settings.getKeyName());
        if (keyButtons > 0) {
            this.layout.findViewById(R.id.settings_keys).setVisibility(View.VISIBLE);
            this.layout.findViewById(R.id.settings_nokeys).setVisibility(View.GONE);
        }
        int backupButtons = this.generateRadioButtons(R.id.settings_backups, Settings.backupsDir, Settings.backupFilter, null);
        if (backupButtons > 0) {
            this.layout.findViewById(R.id.settings_backups).setVisibility(View.VISIBLE);
            this.layout.findViewById(R.id.settings_restore).setVisibility(View.VISIBLE);
        }
    }

    private int generateRadioButtons(int viewID, String dirStr, FilenameFilter filter, String oldName) {
        int count = 0;
        RadioGroup group = (RadioGroup) this.layout.findViewById(viewID);
        File dir = new File(dirStr);
        dir.mkdirs();
        for (File file : dir.listFiles(filter)) {
            String name = file.getName();
            RadioButton radioButton = new RadioButton(this.context);
            radioButton.setText(name);
            group.addView(radioButton);
            if (name.equals(oldName)) {
                radioButton.setChecked(true);
            }

            ++count;
        }

        return count;
    }

    private int getUnsavedCodepage() {
        try {
            return Integer.valueOf(((EditText) this.layout.findViewById(R.id.settings_codepage)).getText().toString());
        } catch (Exception ignored) {
        }
        return (int) Settings.defaults.get("codepage");
    }

    private Charset getUnsavedCharset() {
        switch (((RadioGroup) this.layout.findViewById(R.id.settings_charset)).getCheckedRadioButtonId()) {
            case R.id.settings_charset_iso88592:
                return Charset.iso88592;
            case R.id.settings_charset_cp852:
                return Charset.cp852;
            case R.id.settings_charset_windows1250:
                return Charset.windows1250;
            default: // R.id.settings_charset_ascii
                return Charset.ascii;
        }
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
}
