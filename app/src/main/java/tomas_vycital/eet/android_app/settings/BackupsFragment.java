package tomas_vycital.eet.android_app.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

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
    private Button restore;

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
        this.restore = (Button) this.layout.findViewById(R.id.restore);

        // Onclick listeners
        this.layout.findViewById(R.id.backup).setOnClickListener(this);
        this.restore.setOnClickListener(this);

        this.refresh();

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

                this.refresh();
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
                    editor.apply();

                    this.items.loadSaved();

                    Snackbar.make(this.layout, "Obnoveno", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } catch (Exception e) {
                    Snackbar.make(this.layout, "Obnovení se nezdařilo", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }

                this.refresh();
                break;
        }
    }

    public void refresh() {
        // Continue only if the fragment is initialized
        if (this.getView() == null) {
            return;
        }

        // Radio buttons
        int backupButtons = this.generateRadioButtons(this.backups, Settings.backupsDir, Settings.backupFilter, null);
        if (backupButtons > 0) {
            this.restore.setEnabled(true);
        } else {
            this.restore.setEnabled(false);
        }
    }

    @Override
    public boolean fab() {
        return false;
    }

    private int generateRadioButtons(RadioGroup group, String dirStr, FilenameFilter filter, String oldName) {
        int count = 0;
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
}
