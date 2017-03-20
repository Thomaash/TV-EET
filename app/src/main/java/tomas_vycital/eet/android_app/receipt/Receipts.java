package tomas_vycital.eet.android_app.receipt;

import android.content.Context;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by tom on 18.3.17.
 */

public class Receipts {
    private static DBHelper db;

    static void addReceipt(Receipt receipt) throws JSONException {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        receipt.setSubmitTime(date);

        Receipts.db.addDay(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), receipt.toJSON().toString());
    }

    @NonNull
    public static JSONObject[] getReceipts(int year, int month, int day) throws JSONException {
        List<JSONObject> jsons = new ArrayList<>();
        for (String receipt : Receipts.db.getDay(year, month, day)) {
            jsons.add(new JSONObject(receipt));
        }
        return jsons.toArray(new JSONObject[0]);
    }

    public static void setup(Context context) {
        Receipts.db = new DBHelper(context, "receipts_history", 1);
    }
}
