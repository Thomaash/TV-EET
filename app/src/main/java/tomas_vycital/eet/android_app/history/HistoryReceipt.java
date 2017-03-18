package tomas_vycital.eet.android_app.history;

import android.view.View;
import android.widget.Button;

import java.text.SimpleDateFormat;

import tomas_vycital.eet.android_app.MainActivity;
import tomas_vycital.eet.android_app.receipt.Receipt;

/**
 * Created by tom on 18.3.17.
 */

class HistoryReceipt implements View.OnClickListener {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private final Button view;
    private final Receipt receipt;
    private final MainActivity ma;

    HistoryReceipt(MainActivity ma, Receipt receipt) {
        this.receipt = receipt;
        this.ma = ma;
        this.view = new Button(this.ma);
        this.view.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        this.view.setText(HistoryReceipt.dateFormat.format(this.receipt.getSubmitTime()) + ": " + this.receipt.getPriceStr() + " kč");
        this.view.setOnClickListener(this);
    }

    View getView() {
        return this.view;
    }

    @Override
    public void onClick(View v) {
        this.ma.setReceipt(this.receipt);
    }
}
