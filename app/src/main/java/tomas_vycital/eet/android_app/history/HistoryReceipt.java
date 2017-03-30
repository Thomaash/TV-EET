package tomas_vycital.eet.android_app.history;

import android.os.Build;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import tomas_vycital.eet.android_app.MainActivity;

class HistoryReceipt implements View.OnClickListener {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private final Button button;
    private final JSONObject receipt;
    private final MainActivity ma;

    HistoryReceipt(MainActivity ma, JSONObject receipt, Date submitDate, String priceStr) {
        this.receipt = receipt;
        this.ma = ma;
        this.button = new Button(this.ma);
        this.button.setText(HistoryReceipt.dateFormat.format(submitDate) + ": " + priceStr + " kč");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            this.button.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        }
        this.button.setOnClickListener(this);
    }

    View getView() {
        return this.button;
    }

    @Override
    public void onClick(View v) {
        try {
            this.ma.setReceipt(this.receipt);
        } catch (JSONException | ParseException e) {
            Snackbar.make(this.button, "Účtenku se nepodařilo načíst", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }
}
