package tomas_vycital.eet.android_app.receipt;

import android.os.Bundle;
import android.os.Handler;
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

import tomas_vycital.eet.android_app.BaseFragment;
import tomas_vycital.eet.android_app.Messages;
import tomas_vycital.eet.android_app.R;
import tomas_vycital.eet.android_app.error.UnreadableKeyPassword;
import tomas_vycital.eet.android_app.printer.BTPrinter;

public class ReceiptFragment extends BaseFragment implements View.OnClickListener {
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
        this.layout = inflater.inflate(R.layout.receipt, container, false);

        this.textView = (TextView) this.layout.findViewById(R.id.receipt);
        this.clear = (Button) this.layout.findViewById(R.id.clear);
        this.submit = (Button) this.layout.findViewById(R.id.submit);
        this.print = (Button) this.layout.findViewById(R.id.print);

        this.refresh();

        // Onclick listeners
        this.clear.setOnClickListener(this);
        this.submit.setOnClickListener(this);
        this.print.setOnClickListener(this);

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
            case R.id.clear:
                this.handler.sendEmptyMessage(Messages.clearReceipt.ordinal());
                break;
            case R.id.submit:
                if (this.receipt.isEmpty()) {
                    this.info("Účtenka je prázdná");
                    return;
                }
                try {
                    this.receipt.submit(this.handler);
                } catch (FileNotFoundException e) {
                    this.info("Nebyl nalezen certifikát (.p12)");
                } catch (UnrecoverableKeyException | CertificateException | KeyStoreException | NoSuchAlgorithmException | IOException e) {
                    this.info("Nepodařilo se nahlásit tržbu");
                } catch (UnreadableKeyPassword unreadableKeyPassword) {
                    this.info("Nepodařilo se přečíst heslo ke klíči");
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
