package tomas_vycital.eet.android_app.receipt;

import android.content.Context;
import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Maintains receipts history, has to be initialized with setup method before use
 */
public class Receipts {
    private static DBHelper db;

    /**
     * Adds receipt to the history (sets submit time to present time)
     *
     * @param receipt The receipt to be saved
     * @throws JSONException As long as there is no bug in the code this should never be thrown
     */
    static void addReceipt(Receipt receipt) throws JSONException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(receipt.getSubmitTime());

        Receipts.db.addDay(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), receipt.toJSON().toString());
    }

    /**
     * Add receipt to the history (uses submit time from the receipt)
     *
     * @param json The JSON string with the receipt
     * @throws JSONException  Thrown if the JSON is not valid receipt
     * @throws ParseException Thrown if the submit time is not in the correct format
     */
    public static void addReceipt(JSONObject json) throws JSONException, ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(Receipt.parseDate(json.getString("submitTime")));

        Receipts.db.addDay(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), json.toString());
    }

    /**
     * Deletes all receipts from the history (permanently)
     */
    public static void clear() {
        Receipts.db.clear();
    }

    /**
     * Returns all receipts from requested day as an array of JSONs
     *
     * @param year  The year (e.g. 2017 for 2017 AD)
     * @param month The month (e.g. 1 for January)
     * @param day   The day (e.g. 1 for the first day in the month)
     * @return List containing all receipts from the day in JSON objects
     * @throws JSONException Can by thrown only if the data in the db are corrupted
     */
    @NonNull
    public static List<JSONObject> getReceipts(int year, int month, int day) throws JSONException {
        return Receipts.stringsToJSONs(Receipts.db.getDay(year, month, day));
    }

    /**
     * Returns all receipts from the history as an array of JSONs
     *
     * @return List containing all receipts from the day in JSON objects
     * @throws JSONException Can by thrown only if the data in the db are corrupted
     */
    @NonNull
    public static List<JSONObject> getReceipts() throws JSONException {
        return Receipts.stringsToJSONs(Receipts.db.getAll());
    }

    /**
     * Initializes connection to the db
     *
     * @param context Android context
     */
    public static void setup(Context context) {
        Receipts.db = new DBHelper(context, "receipts_history", 1);
    }

    /**
     * Converts JSON strings to JSON objects
     *
     * @param receipts Strings
     * @return JSONObjects
     * @throws JSONException Malformed strings
     */
    @NonNull
    private static List<JSONObject> stringsToJSONs(List<String> receipts) throws JSONException {
        List<JSONObject> jsons = new ArrayList<>();
        for (String receipt : receipts) {
            jsons.add(new JSONObject(receipt));
        }
        return jsons;
    }
}
