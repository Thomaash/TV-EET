package tomas_vycital.eet.android_app.history;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.LinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

import tomas_vycital.eet.android_app.MainActivity;
import tomas_vycital.eet.android_app.R;
import tomas_vycital.eet.android_app.RefreshableFragment;
import tomas_vycital.eet.android_app.receipt.Receipt;
import tomas_vycital.eet.android_app.receipt.Receipts;

/**
 * Created by tom on 18.3.17.
 */

public class HistoryFragment extends Fragment implements RefreshableFragment, CalendarView.OnDateChangeListener {
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
        View layout = inflater.inflate(R.layout.history, container, false);

        // Views
        this.receipts = (LinearLayout) layout.findViewById(R.id.receipts);

        // Onclick listeners
        ((CalendarView) layout.findViewById(R.id.calendar)).setOnDateChangeListener(this);

        // Inflate the layout for this fragment
        return layout;
    }

    @Override
    public void refresh() {
        // Continue only if the fragment is initialized
        if (this.getView() == null) {
            return;
        }

        this.receipts.removeAllViews();
    }

    @Override
    public boolean fab() {
        return false;
    }

    @Override
    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int day) {
        this.receipts.removeAllViews();
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
                } catch (JSONException | ParseException ignored) {
                }
            }
        } catch (JSONException ignored) {
        }
    }
}
