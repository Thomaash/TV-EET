package tomas_vycital.eet.android_app.history;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import tomas_vycital.eet.android_app.BaseFragment;
import tomas_vycital.eet.android_app.MainActivity;
import tomas_vycital.eet.android_app.R;
import tomas_vycital.eet.android_app.receipt.Receipt;
import tomas_vycital.eet.android_app.receipt.Receipts;

public class HistoryFragment extends BaseFragment implements CalendarView.OnDateChangeListener {
    private LinearLayout receipts;
    private MainActivity ma;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance(MainActivity ma) {
        HistoryFragment fragment = new HistoryFragment();
        fragment.ma = ma;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.layout = inflater.inflate(R.layout.history, container, false);

        // Views
        this.receipts = (LinearLayout) this.layout.findViewById(R.id.receipts);

        // Onclick listeners
        ((CalendarView) this.layout.findViewById(R.id.calendar)).setOnDateChangeListener(this);

        // List today
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        this.listReceipts(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        return this.layout;
    }

    @Override
    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int day) {
        this.listReceipts(year, month, day);
    }

    private void listReceipts(int year, int month, int day) {
        this.receipts.removeAllViews();

        int added = 0;
        try {
            for (JSONObject jsonReceipt : Receipts.getReceipts(year, month, day)) {
                try {
                    Receipt receipt = new Receipt(jsonReceipt);
                    this.receipts.addView(new HistoryReceipt(
                            this.ma,
                            jsonReceipt,
                            receipt.getSubmitTime(),
                            receipt.getPriceStr()
                    ).getView());
                    ++added;
                } catch (JSONException | ParseException ignored) {
                    this.info("Nepodařilo se načíst účtenku z historie");
                }
            }
        } catch (JSONException ignored) {
            this.info("Nepodařilo se načíst účtenky z historie");
        }

        if (added == 0) {
            TextView info = new TextView(this.ma);
            info.setText("Tento den neobsahuje žádné účtenky");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                info.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            }
            this.receipts.addView(info);
        }
    }
}
