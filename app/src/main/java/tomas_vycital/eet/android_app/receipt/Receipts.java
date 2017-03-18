package tomas_vycital.eet.android_app.receipt;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tom on 18.3.17.
 */

public class Receipts {
    private static List<Receipt> receipts = new ArrayList<>();

    static void addReceipt(Receipt receipt) throws JSONException {
        receipt.setSubmitTime(new Date());
        receipt.toJSON().toString();
        Receipts.receipts.add(receipt);
    }

    public static Receipt[] getReceipts() {
        return Receipts.receipts.toArray(new Receipt[0]);
    }
}
