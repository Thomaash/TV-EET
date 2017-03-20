package tomas_vycital.eet.android_app.receipt;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tom on 18.3.17.
 */

public class Receipts {
    private static List<JSONObject> receipts = new ArrayList<>();

    static void addReceipt(Receipt receipt) throws JSONException {
        receipt.setSubmitTime(new Date());
        Receipts.receipts.add(receipt.toJSON());
    }

    public static JSONObject[] getReceipts() {
        return Receipts.receipts.toArray(new JSONObject[0]);
    }
}
