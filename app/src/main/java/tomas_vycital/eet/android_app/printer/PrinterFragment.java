package tomas_vycital.eet.android_app.printer;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;

import tomas_vycital.eet.android_app.BaseFragment;
import tomas_vycital.eet.android_app.Messages;
import tomas_vycital.eet.android_app.R;
import tomas_vycital.eet.android_app.settings.Settings;

public class PrinterFragment extends BaseFragment implements View.OnClickListener {
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
        this.layout = inflater.inflate(R.layout.printer, container, false);

        // Views
        this.info = (TextView) this.layout.findViewById(R.id.info);
        this.printers = (LinearLayout) this.layout.findViewById(R.id.printers);

        // Onclick listeners
        this.layout.findViewById(R.id.scan).setOnClickListener(this);
        this.layout.findViewById(R.id.test).setOnClickListener(this);
        this.layout.findViewById(R.id.disconnect).setOnClickListener(this);

        // Inflate the layout for this fragment
        return this.layout;
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.scan:
                this.info("Vyhledává se…");
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
                                PrinterFragment.this.info("Tiskárna zvolena: " + device.getName());
                            } catch (IOException e) {
                                PrinterFragment.this.info("K tiskárně se nepodařilo připojit");
                            }

                            PrinterFragment.this.handler.sendEmptyMessage(Messages.btPrinterChanged.ordinal());
                        }
                    });

                    PrinterFragment.this.printers.addView(btn);
                }
                this.info("Nalezeno: " + devices.length);
                break;
            case R.id.test:
                try {
                    this.printer.printSelfTest();
                    this.info("Tiskne se");
                } catch (IOException e) {
                    this.info("Nepodařilo se vytisknout");
                }
                break;
            case R.id.disconnect:
                try {
                    this.printer.disconnect();
                    this.info("Tiskárna byla odpojena");
                } catch (IOException e) {
                    this.info("Od tiskárny se nepodařilo odpojit");
                }
                break;
        }
    }
}
