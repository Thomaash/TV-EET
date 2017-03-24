package tomas_vycital.eet.android_app.receipt;

import android.content.Context;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Receipts {
    private static DBHelper db;

    static void addReceipt(Receipt receipt) throws JSONException {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        receipt.setSubmitTime(date);

        Receipts.db.addDay(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), receipt.toJSON().toString());
    }

    public static void addReceipt(JSONObject json) throws JSONException, ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(Receipt.parseDate(json.getString("submitTime")));

        Receipts.db.addDay(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), json.toString());
    }

    public static void clear() {
        Receipts.db.clear();
    }

    @NonNull
    public static JSONObject[] getReceipts(int year, int month, int day) throws JSONException {
        return Receipts.stringsToJSONs(Receipts.db.getDay(year, month, day));
    }

    @NonNull
    public static JSONObject[] getReceipts() throws JSONException {
        return Receipts.stringsToJSONs(Receipts.db.getAll());
    }

    public static void setup(Context context) {
        Receipts.db = new DBHelper(context, "receipts_history", 1);
    }

    @NonNull
    private static JSONObject[] stringsToJSONs(List<String> receipts) throws JSONException {
        List<JSONObject> jsons = new ArrayList<>();
        for (String receipt : receipts) {
            jsons.add(new JSONObject(receipt));
        }
        return jsons.toArray(new JSONObject[0]);
    }
}
