package tomas_vycital.eet.android_app.receipt;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import tomas_vycital.eet.android_app.Messages;
import tomas_vycital.eet.android_app.R;
import tomas_vycital.eet.android_app.RefreshableFragment;
import tomas_vycital.eet.android_app.printer.BTPrinter;

public class ReceiptFragment extends Fragment implements View.OnClickListener, RefreshableFragment {
    private BTPrinter printer;
    private Receipt receipt;
    private Handler handler;
    private TextView textView;
    private Button clear;
    private Button submit;
    private Button print;

    public ReceiptFragment() {
        // Required empty public constructor
    }

    public static ReceiptFragment newInstance(Receipt receipt, BTPrinter printer, Handler handler) {
        ReceiptFragment fragment = new ReceiptFragment();
        fragment.receipt = receipt;
        fragment.printer = printer;
        fragment.handler = handler;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.receipt, container, false);

        this.textView = (TextView) layout.findViewById(R.id.receipt);
        this.clear = (Button) layout.findViewById(R.id.clear);
        this.submit = (Button) layout.findViewById(R.id.submit);
        this.print = (Button) layout.findViewById(R.id.print);

        this.refresh();

        // Onclick listeners
        this.clear.setOnClickListener(this);
        this.submit.setOnClickListener(this);
        this.print.setOnClickListener(this);

        // Inflate the layout for this fragment
        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.refresh();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear:
                this.handler.sendEmptyMessage(Messages.clearReceipt.ordinal());
                break;
            case R.id.submit:
                if (this.receipt.isEmpty()) {
                    Snackbar.make(v, "Účtenka je prázdná", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    return;
                }
                try {
                    this.receipt.submit(this.handler);
                } catch (FileNotFoundException e) {
                    Snackbar.make(v, "Nebyl nalezen certifikát (.p12)", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } catch (UnrecoverableKeyException | CertificateException | KeyStoreException | NoSuchAlgorithmException | IOException e) {
                    Snackbar.make(v, "Nepodařilo se nahlásit tržbu", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                break;
            case R.id.print:
                this.receipt.print(this.handler, this.printer);
                break;
        }
    }

    @Override
    public void refresh() {
        if (this.textView == null) {
            return;
        }
        this.textView.setText(this.receipt.getReceiptStr());
        this.clear.setEnabled(this.receipt.isClearable());
        this.submit.setEnabled(this.receipt.isSubmittable());
        this.print.setEnabled(this.receipt.isPrintable());
    }

    @Override
    public boolean fab() {
        return false;
    }
}
