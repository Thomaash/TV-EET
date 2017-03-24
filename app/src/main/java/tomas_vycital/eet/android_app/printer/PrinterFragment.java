package tomas_vycital.eet.android_app.printer;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;

import tomas_vycital.eet.android_app.Messages;
import tomas_vycital.eet.android_app.R;
import tomas_vycital.eet.android_app.RefreshableFragment;
import tomas_vycital.eet.android_app.settings.Settings;

public class PrinterFragment extends Fragment implements RefreshableFragment, View.OnClickListener {
    private BTPrinter printer;
    private Handler handler;
    private LinearLayout printers;
    private TextView info;

    public PrinterFragment() {
        // Required empty public constructor
    }

    public static PrinterFragment newInstance(BTPrinter printer, Handler handler) {
        PrinterFragment fragment = new PrinterFragment();
        fragment.printer = printer;
        fragment.handler = handler;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.printer, container, false);

        // Views
        this.info = (TextView) layout.findViewById(R.id.info);
        this.printers = (LinearLayout) layout.findViewById(R.id.printers);

        // Onclick listeners
        layout.findViewById(R.id.scan).setOnClickListener(this);
        layout.findViewById(R.id.test).setOnClickListener(this);
        layout.findViewById(R.id.disconnect).setOnClickListener(this);

        // Inflate the layout for this fragment
        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.refresh();
    }

    @Override
    public void refresh() {
        // Continue only if the fragment is initialized
        if (this.getView() == null) {
            return;
        }

        BluetoothDevice device = this.printer.getDevice();
        this.info.setText(
                device == null
                        ? "Nepřipojeno"
                        : "Připojeno: " + device.getName() + " (" + device.getAddress() + ")"
        );
    }

    @Override
    public boolean fab() {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scan:
                Snackbar.make(v, "Vyhledává se…", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                this.printers.removeAllViews();
                BluetoothDevice[] devices = this.printer.list();

                for (final BluetoothDevice device : devices) {
                    final Button btn = new Button(this.getContext());
                    btn.setText(device.getName() + " (" + device.getAddress() + ")");
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            btn.setClickable(false);
                            try {
                                PrinterFragment.this.printer.connect(device);
                                Settings.setLastMAC(device.getAddress());
                                Snackbar.make(v, "Tiskárna zvolena: " + device.getName(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                            } catch (IOException e) {
                                Snackbar.make(v, "K tiskárně se nepodařilo připojit", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                            }

                            PrinterFragment.this.handler.sendEmptyMessage(Messages.btPrinterChanged.ordinal());
                        }
                    });

                    PrinterFragment.this.printers.addView(btn);
                }
                Snackbar.make(v, "Nalezeno: " + devices.length, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                break;
            case R.id.test:
                try {
                    this.printer.printSelfTest();
                    Snackbar.make(v, "Tiskne se", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } catch (IOException e) {
                    Snackbar.make(v, "Nepodařilo se vytisknout", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                break;
            case R.id.disconnect:
                try {
                    this.printer.disconnect();
                    Snackbar.make(v, "Tiskárna byla odpojena", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } catch (IOException e) {
                    Snackbar.make(v, "Od tiskárny se nepodařilo odpojit", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                break;
        }
    }
}
