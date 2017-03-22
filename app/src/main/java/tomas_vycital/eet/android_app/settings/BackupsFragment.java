package tomas_vycital.eet.android_app.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.Writer;
import java.util.Calendar;
import java.util.TimeZone;

import tomas_vycital.eet.android_app.R;
import tomas_vycital.eet.android_app.RefreshableFragment;
import tomas_vycital.eet.android_app.items.Items;

/**
 * Created by tom on 3.3.17.
 */

public class BackupsFragment extends Fragment implements View.OnClickListener, RefreshableFragment {
    private Context context;
    private View layout;
    private Items items;

    private RadioGroup backups;

    public BackupsFragment() {
        // Required empty public constructor
    }

    public static BackupsFragment newInstance(Items items) {
        BackupsFragment fragment = new BackupsFragment();
        fragment.items = items;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.context = this.getContext();
        this.layout = inflater.inflate(R.layout.backups, container, false);

        // Views
        this.backups = (RadioGroup) this.layout.findViewById(R.id.backups);

        // Onclick listeners
        this.layout.findViewById(R.id.backup).setOnClickListener(this);
        this.layout.findViewById(R.id.restore).setOnClickListener(this);

        this.refreshAll();

        // Inflate the layout for this fragment
        return this.layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.refresh();
    }

    @Override
    public void onClick(View v) {
        SharedPreferences.Editor editor;

        switch (v.getId()) {
            case R.id.backup:
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

                this.refreshAll();
                break;
            case R.id.restore:
                try {
                    String fileName = ((RadioButton) this.layout.findViewById(this.backups.getCheckedRadioButtonId())).getText().toString();
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

                    this.items.loadSaved();

                    Snackbar.make(this.layout, "Obnoveno", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } catch (Exception e) {
                    Snackbar.make(this.layout, "Obnovení se nezdařilo", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }

                this.refreshAll();
                break;
        }
    }

    private void refreshAll() {
        // Radio buttons
        this.refresh();
    }

    public void refresh() {
        // Continue only if the fragment is initialized
        if (this.getView() == null) {
            return;
        }

        // Radio buttons
        int backupButtons = this.generateRadioButtons(R.id.backups, Settings.backupsDir, Settings.backupFilter, null);
        if (backupButtons > 0) {
            this.layout.findViewById(R.id.restore).setEnabled(true);
        } else {
            this.layout.findViewById(R.id.restore).setEnabled(false);
        }
    }

    @Override
    public boolean fab() {
        return false;
    }

    private int generateRadioButtons(int viewID, String dirStr, FilenameFilter filter, String oldName) {
        int count = 0;
        RadioGroup group = (RadioGroup) this.layout.findViewById(viewID);
        group.removeAllViews();
        File dir = new File(dirStr);
        dir.mkdirs();
        File[] files = dir.listFiles(filter);
        if (files != null) {
            for (File file : files) {
                String name = file.getName();
                RadioButton radioButton = new RadioButton(this.context);
                radioButton.setText(name);
                group.addView(radioButton);
                if (name.equals(oldName)) {
                    radioButton.setChecked(true);
                }

                ++count;
            }
        }

        return count;
    }

    @Nullable
    private String getRadioGroupValue(int groupRID) {
        RadioButton radioButton = (RadioButton) this.layout.findViewById(((RadioGroup) this.layout.findViewById(groupRID)).getCheckedRadioButtonId());
        return radioButton == null ? null : radioButton.getText().toString();
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
            case R.id.settings_charset_utf8:
                return Charset.utf8;
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
