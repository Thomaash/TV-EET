package tomas_vycital.eet.android_app.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.Writer;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;
import java.util.TimeZone;

import tomas_vycital.eet.android_app.BaseFragment;
import tomas_vycital.eet.android_app.R;
import tomas_vycital.eet.android_app.items.Items;
import tomas_vycital.eet.android_app.receipt.Receipts;

public class BackupsFragment extends BaseFragment implements View.OnClickListener {
    private Context context;
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
        switch (v.getId()) {
            case R.id.backup:
                try {
                    (new File(Settings.backupsDir)).mkdirs();
                    File file = new File(Settings.backupsDir + "/" + String.format("%tFT%<tR%<tZ", Calendar.getInstance(TimeZone.getDefault())) + ".json");
                    Writer writer = new BufferedWriter(new FileWriter(file));
                    writer.write(
                            (new JSONObject())
                                    .put("version", 1)
                                    .put("settings", new JSONObject(Settings.prefs.getAll()))
                                    .put("history", new JSONArray(Receipts.getReceipts()))
                                    .toString()
                    );
                    writer.close();

                    this.info("Zazálohováno");
                } catch (Exception e) {
                    this.info("Záloha se nezdařilo");
                }

                this.refresh();
                break;
            case R.id.restore:
                try {
                    this.restore(Settings.backupsDir + "/" + ((RadioButton) this.layout.findViewById(this.backups.getCheckedRadioButtonId())).getText().toString());
                    this.items.loadSaved();

                    this.info("Obnoveno");
                } catch (JSONException | ParseException e) {
                    this.info("Obnovení se nezdařilo: soubor neobsahuje čitelnou zálohu");
                } catch (FileNotFoundException e) {
                    this.info("Obnovení se nezdařilo: soubor se zálohou nelze přečíst");
                } catch (Exception e) {
                    this.info("Obnovení se nezdařilo");
                }

                this.refresh();
                break;
        }
    }

    private void restore(String path) throws FileNotFoundException, JSONException, ParseException {
        JSONObject json = new JSONObject((new Scanner(new File(path))).useDelimiter("\\A").next());
        int version = json.getInt("version");
        switch (version) {
            case 1:
                this.restore1(json);
                break;
            default:
                if (version > 1) {
                    this.info("Obnovení se nezdařilo: záloha byla vytvořena novější verzí aplikace, aktualizujte aplikaci a zkuste to znovu");
                } else {
                    this.info("Obnovení se nezdařilo: neznámá verze zálohy");
                }
        }
    }

    private void restore1(JSONObject json) throws JSONException, ParseException {
        this.restore1Settings(json.getJSONObject("settings"));
        this.restore1History(json.getJSONArray("history"));
    }

    private void restore1Settings(JSONObject json) throws JSONException {
        Iterator<String> iter = json.keys();
        SharedPreferences.Editor editor = Settings.prefs.edit();

        while (iter.hasNext()) {
            String key = iter.next();
            Object defaultValue = Settings.defaults.get(key);
            if (defaultValue instanceof Integer) {
                editor.putInt(key, json.getInt(key));
            } else if (defaultValue instanceof Boolean) {
                editor.putBoolean(key, json.getBoolean(key));
            } else { // String
                editor.putString(key, json.getString(key));
            }
        }

        editor.apply();
    }

    private void restore1History(JSONArray json) throws JSONException, ParseException {
        Receipts.clear();
        for (int i = 0; i < json.length(); ++i) {
            Receipts.addReceipt(json.getJSONObject(i));
        }
    }

    @Override
    public void refresh() {
        // Continue only if the fragment is initialized
        if (this.getView() == null) {
            return;
        }

        // Radio buttons
        int backupButtons = this.generateRadioButtons(this.backups, Settings.backupsDir, Settings.backupFilter, null, Collections.reverseOrder());
        if (backupButtons > 0) {
            this.restore.setEnabled(true);
        } else {
            TextView info = new TextView(this.context);
            info.setText("Nebyly nalezeny žádné zálohy.\n\n" +
                    "Ujistěte se, že aplikace má oprávnění číst paměťovou kartu (může být nutné restartovat aplikaci).\n" +
                    "Také se ujistěte, že jsou zálohy umístěny ve správné složce (TV EET/Backups); " +
                    "tato složka je automaticky vytvořena při spuštění aplikace (může být nutné restartovat telefon aby byla vyditelná z počítače).\n");
            this.backups.addView(info);
            this.restore.setEnabled(false);
        }
    }
}
