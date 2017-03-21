package tomas_vycital.eet.android_app.receipt;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import tomas_vycital.eet.android_app.R;
import tomas_vycital.eet.android_app.RefreshableFragment;
import tomas_vycital.eet.android_app.printer.BTPrinter;

/**
 * Created by tom on 3.3.17.
 */

public class ReceiptFragment extends Fragment implements View.OnClickListener, RefreshableFragment {
    private BTPrinter printer;
    private Receipt receipt;
    private Handler handler;
    private TextView textView;

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

        this.refresh();

        // Onclick listeners
        layout.findViewById(R.id.submit).setOnClickListener(this);
        layout.findViewById(R.id.print).setOnClickListener(this);

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
            case R.id.submit:
                if (this.receipt.isEmpty()) {
                    Snackbar.make(v, "Účtenka je prázdná", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    return;
                }
                try {
                    this.receipt.submit(this.handler);
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
    }

    @Override
    public boolean fab() {
        return false;
    }
}
