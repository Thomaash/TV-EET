package tomas_vycital.eet.android_app.history;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CalendarView;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

import tomas_vycital.eet.android_app.MainActivity;
import tomas_vycital.eet.android_app.R;
import tomas_vycital.eet.android_app.receipt.Receipt;
import tomas_vycital.eet.android_app.receipt.Receipts;

/**
 * Created by tom on 18.3.17.
 */

public class HistoryGUI implements CalendarView.OnDateChangeListener {
    private final MainActivity ma;
    private final LinearLayout receipts;

    public HistoryGUI(MainActivity ma, View layout) {
        this.ma = ma;
        this.receipts = (LinearLayout) layout.findViewById(R.id.history_receipts);

        ((CalendarView) layout.findViewById(R.id.history_calendar)).setOnDateChangeListener(this);
    }

    @Override
    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
        this.receipts.removeAllViews();
        for (JSONObject jsonReceipt : Receipts.getReceipts()) {
            try {
                Receipt receipt = new Receipt(jsonReceipt);
                this.receipts.addView(new HistoryReceipt(
                        this.ma,
                        jsonReceipt,
                        receipt.getSubmitTime(),
                        receipt.getPriceStr()
                ).getView());
            } catch (JSONException | ParseException ignored) {
            }
        }
    }

    public void refresh() {
        this.receipts.removeAllViews();
    }
}
