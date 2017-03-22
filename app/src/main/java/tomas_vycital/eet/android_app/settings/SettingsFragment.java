package tomas_vycital.eet.android_app.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import tomas_vycital.eet.android_app.R;
import tomas_vycital.eet.android_app.RefreshableFragment;
import tomas_vycital.eet.android_app.printer.BTPrinter;

/**
 * Created by tom on 3.3.17.
 */

public class SettingsFragment extends Fragment implements View.OnClickListener, RefreshableFragment {
    private Context context;
    private View layout;
    private BTPrinter printer;

    private RadioGroup server;
    private EditText dic;
    private EditText heading;
    private EditText footing;
    private EditText receiptWidth;
    private EditText idPokl;
    private EditText idProvoz;
    private RadioButton serverPlay;
    private RadioButton serverProd;
    private Switch verifying;
    private EditText codepage;
    private RadioButton charsetASCII;
    private RadioButton charsetUTF8;
    private RadioButton charsetISO88592;
    private RadioButton charsetCP852;
    private RadioButton charsetWindows1250;
    private RadioGroup keys;
    private TextView nokeys;
    private RadioGroup charset;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance(BTPrinter printer) {
        SettingsFragment fragment = new SettingsFragment();
        fragment.printer = printer;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.context = this.getContext();
        this.layout = inflater.inflate(R.layout.settings, container, false);

        // Views
        this.server = (RadioGroup) this.layout.findViewById(R.id.server);
        this.dic = (EditText) this.layout.findViewById(R.id.dic);
        this.heading = (EditText) this.layout.findViewById(R.id.heading);
        this.footing = (EditText) this.layout.findViewById(R.id.footing);
        this.receiptWidth = (EditText) this.layout.findViewById(R.id.receipt_width);
        this.idPokl = (EditText) this.layout.findViewById(R.id.id_pokl);
        this.idProvoz = (EditText) this.layout.findViewById(R.id.id_provoz);
        this.serverPlay = (RadioButton) this.layout.findViewById(R.id.server_play);
        this.serverProd = (RadioButton) this.layout.findViewById(R.id.server_prod);
        this.verifying = (Switch) this.layout.findViewById(R.id.verifying);
        this.codepage = (EditText) this.layout.findViewById(R.id.codepage);
        this.charsetASCII = (RadioButton) this.layout.findViewById(R.id.charset_ascii);
        this.charsetUTF8 = (RadioButton) this.layout.findViewById(R.id.charset_utf8);
        this.charsetISO88592 = (RadioButton) this.layout.findViewById(R.id.charset_iso88592);
        this.charsetCP852 = (RadioButton) this.layout.findViewById(R.id.charset_cp852);
        this.charsetWindows1250 = (RadioButton) this.layout.findViewById(R.id.charset_windows1250);
        this.keys = (RadioGroup) this.layout.findViewById(R.id.keys);
        this.nokeys = (TextView) this.layout.findViewById(R.id.nokeys);
        this.charset = (RadioGroup) this.layout.findViewById(R.id.charset);

        // Onclick listeners
        this.layout.findViewById(R.id.save).setOnClickListener(this);
        this.layout.findViewById(R.id.codepage_test).setOnClickListener(this);

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
            case R.id.save:
                Snackbar.make(this.layout, "Ukládá se…", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                editor = Settings.prefs.edit();
                this.saveString(editor, R.id.dic, "DIC");
                this.saveString(editor, R.id.heading, "heading");
                this.saveString(editor, R.id.footing, "footing");
                this.saveInteger(editor, R.id.receipt_width, "receiptWidth");
                this.saveString(editor, R.id.id_pokl, "idPokl");
                this.saveString(editor, R.id.id_provoz, "idProvoz");
                switch (this.server.getCheckedRadioButtonId()) {
                    case R.id.server_play:
                        editor.putInt("server", Server.play.getID());
                        break;
                    case R.id.server_prod:
                        editor.putInt("server", Server.prod.getID());
                        break;
                }
                this.saveBoolean(editor, R.id.verifying, "verifying");
                editor.putInt("codepage", this.getUnsavedCodepage());
                editor.putString("charset", this.getUnsavedCharset().getStr());
                editor.putString("keyFileName", this.getRadioGroupValue(R.id.keys));
                editor.apply();

                Snackbar.make(this.layout, "Uloženo", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                this.refreshAll();
                break;
            case R.id.codepage_test:
                try {
                    this.printer.testCP(this.getUnsavedCodepage(), this.getUnsavedCharset());
                } catch (IOException ignored) {
                }
                break;
        }
    }

    private void refreshAll() {
        // Settings values
        this.dic.setText(Settings.getDIC());
        this.heading.setText(Settings.getHeading());
        this.footing.setText(Settings.getFooting());
        this.receiptWidth.setText(String.valueOf(Settings.getReceiptWidth()));
        this.idPokl.setText(Settings.getIdPokl());
        this.idProvoz.setText(Settings.getIdProvoz());
        switch (Settings.getServer()) {
            case play:
                this.serverPlay.setChecked(true);
                break;
            case prod:
                this.serverProd.setChecked(true);
                break;
        }
        this.verifying.setChecked(Settings.getVerifying());
        this.codepage.setText("" + Settings.getCodepage());
        switch (Settings.getCharset()) {
            case ascii:
                this.charsetASCII.setChecked(true);
                break;
            case utf8:
                this.charsetUTF8.setChecked(true);
                break;
            case iso88592:
                this.charsetISO88592.setChecked(true);
                break;
            case cp852:
                this.charsetCP852.setChecked(true);
                break;
            case windows1250:
                this.charsetWindows1250.setChecked(true);
                break;
        }

        // Radio buttons
        this.refresh();
    }

    public void refresh() {
        // Continue only if the fragment is initialized
        if (this.getView() == null) {
            return;
        }

        // Radio buttons
        int keyButtons = this.generateRadioButtons(R.id.keys, Settings.keysDir, Settings.keyFilter, Settings.getKeyName());
        if (keyButtons > 0) {
            this.keys.setVisibility(View.VISIBLE);
            this.nokeys.setVisibility(View.GONE);
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
            return Integer.valueOf(this.codepage.getText().toString());
        } catch (Exception ignored) {
        }
        return (int) Settings.defaults.get("codepage");
    }

    private Charset getUnsavedCharset() {
        switch (this.charset.getCheckedRadioButtonId()) {
            case R.id.charset_utf8:
                return Charset.utf8;
            case R.id.charset_iso88592:
                return Charset.iso88592;
            case R.id.charset_cp852:
                return Charset.cp852;
            case R.id.charset_windows1250:
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
