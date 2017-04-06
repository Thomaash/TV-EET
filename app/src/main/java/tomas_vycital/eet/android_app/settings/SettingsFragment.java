package tomas_vycital.eet.android_app.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONException;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import tomas_vycital.eet.android_app.BaseFragment;
import tomas_vycital.eet.android_app.R;
import tomas_vycital.eet.android_app.printer.BTPrinter;

public class SettingsFragment extends BaseFragment implements View.OnClickListener {
    private Context context;
    private BTPrinter printer;

    private EditText address;
    private EditText codepage;
    private EditText dic;
    private EditText footing;
    private EditText heading;
    private EditText ico;
    private EditText idPokl;
    private EditText idProvoz;
    private EditText keyPassword;
    private EditText name;
    private EditText receiptWidth;
    private RadioButton charsetASCII;
    private RadioButton charsetCP852;
    private RadioButton charsetISO88592;
    private RadioButton charsetUTF8;
    private RadioButton charsetWindows1250;
    private RadioButton serverPlay;
    private RadioButton serverProd;
    private RadioGroup charset;
    private RadioGroup keys;
    private RadioGroup server;
    private SwitchCompat verifying;
    private TextView nokeys;

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
        this.address = (EditText) this.layout.findViewById(R.id.address);
        this.charset = (RadioGroup) this.layout.findViewById(R.id.charset);
        this.charsetASCII = (RadioButton) this.layout.findViewById(R.id.charset_ascii);
        this.charsetCP852 = (RadioButton) this.layout.findViewById(R.id.charset_cp852);
        this.charsetISO88592 = (RadioButton) this.layout.findViewById(R.id.charset_iso88592);
        this.charsetUTF8 = (RadioButton) this.layout.findViewById(R.id.charset_utf8);
        this.charsetWindows1250 = (RadioButton) this.layout.findViewById(R.id.charset_windows1250);
        this.codepage = (EditText) this.layout.findViewById(R.id.codepage);
        this.dic = (EditText) this.layout.findViewById(R.id.dic);
        this.footing = (EditText) this.layout.findViewById(R.id.footing);
        this.heading = (EditText) this.layout.findViewById(R.id.heading);
        this.ico = (EditText) this.layout.findViewById(R.id.ico);
        this.idPokl = (EditText) this.layout.findViewById(R.id.id_pokl);
        this.idProvoz = (EditText) this.layout.findViewById(R.id.id_provoz);
        this.keyPassword = (EditText) this.layout.findViewById(R.id.key_password);
        this.keys = (RadioGroup) this.layout.findViewById(R.id.keys);
        this.name = (EditText) this.layout.findViewById(R.id.name);
        this.nokeys = (TextView) this.layout.findViewById(R.id.nokeys);
        this.receiptWidth = (EditText) this.layout.findViewById(R.id.receipt_width);
        this.server = (RadioGroup) this.layout.findViewById(R.id.server);
        this.serverPlay = (RadioButton) this.layout.findViewById(R.id.server_play);
        this.serverProd = (RadioButton) this.layout.findViewById(R.id.server_prod);
        this.verifying = (SwitchCompat) this.layout.findViewById(R.id.verifying);

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
                this.info("Ukládá se…");
                editor = Settings.prefs.edit();

                // Simple
                editor.putInt("codepage", this.getUnsavedCodepage());
                editor.putString("charset", this.getUnsavedCharset().getStr());
                editor.putString("keyFileName", this.getRadioGroupValue(this.keys));
                this.saveBoolean(editor, this.verifying, "verifying");
                this.saveInteger(editor, this.receiptWidth, "receiptWidth");
                this.saveString(editor, this.address, "address");
                this.saveString(editor, this.dic, "DIC");
                this.saveString(editor, this.footing, "footing");
                this.saveString(editor, this.heading, "heading");
                this.saveString(editor, this.ico, "ICO");
                this.saveString(editor, this.idPokl, "idPokl");
                this.saveString(editor, this.idProvoz, "idProvoz");
                this.saveString(editor, this.name, "name");

                // Server
                switch (this.server.getCheckedRadioButtonId()) {
                    case R.id.server_play:
                        editor.putInt("server", Server.play.getID());
                        break;
                    case R.id.server_prod:
                        editor.putInt("server", Server.prod.getID());
                        break;
                }

                // Password
                try {
                    this.savePassword(editor, this.keyPassword, "keyPassword");
                } catch (IOException | BadPaddingException | InvalidKeyException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | UnrecoverableEntryException | NoSuchProviderException | NoSuchPaddingException | KeyStoreException | IllegalBlockSizeException | SignatureException | JSONException e) {
                    this.info("Nepodařilo se uložit heslo ke klíči");
                }

                editor.apply();
                this.info("Uloženo");

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
        // Simple
        this.address.setText(Settings.getAddress());
        this.codepage.setText("" + Settings.getCodepage());
        this.dic.setText(Settings.getDIC());
        this.footing.setText(Settings.getFooting());
        this.heading.setText(Settings.getHeading());
        this.ico.setText(Settings.getICO());
        this.idPokl.setText(Settings.getIdPokl());
        this.idProvoz.setText(Settings.getIdProvoz());
        this.name.setText(Settings.getName());
        this.receiptWidth.setText(String.valueOf(Settings.getReceiptWidth()));
        this.verifying.setChecked(Settings.getVerifying());

        // Server
        switch (Settings.getServer()) {
            case play:
                this.serverPlay.setChecked(true);
                break;
            case prod:
                this.serverProd.setChecked(true);
                break;
        }

        // Charset
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

    @Override
    public void refresh() {
        // Continue only if the fragment is initialized
        if (this.getView() == null) {
            return;
        }

        // Radio buttons
        int keyButtons = this.generateRadioButtons(this.keys, Settings.keysDir, Settings.keyFilter, Settings.getKeyName());
        if (keyButtons > 0) {
            this.keys.setVisibility(View.VISIBLE);
            this.nokeys.setVisibility(View.GONE);
        }
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

    @Nullable
    private String getRadioGroupValue(RadioGroup view) {
        RadioButton radioButton = (RadioButton) this.layout.findViewById(view.getCheckedRadioButtonId());
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

    private void saveBoolean(SharedPreferences.Editor editor, SwitchCompat view, String prefID) {
        editor.putBoolean(prefID, view.isChecked());
    }

    private void saveString(SharedPreferences.Editor editor, EditText view, String prefID) {
        editor.putString(prefID, view.getText().toString());
    }

    private void saveInteger(SharedPreferences.Editor editor, EditText view, String prefID) {
        editor.putInt(prefID, Integer.valueOf(view.getText().toString()));
    }

    private void savePassword(SharedPreferences.Editor editor, EditText view, String prefID) throws IOException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException, UnrecoverableEntryException, InvalidAlgorithmParameterException, NoSuchPaddingException, NoSuchProviderException, SignatureException, KeyStoreException, IllegalBlockSizeException, JSONException {
        String text = view.getText().toString();
        if (text.length() > 0) { // Passwords are not shown to the user so do not rewrite it if it is empty
            editor.putString(prefID, Settings.encryption.encryptText(text).toJSON().toString());
        }
    }
}
