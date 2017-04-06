package tomas_vycital.eet.android_app.receipt;

import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.SparseIntArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import tomas_vycital.eet.android_app.Messages;
import tomas_vycital.eet.android_app.VAT;
import tomas_vycital.eet.android_app.error.UnreadableKeyPassword;
import tomas_vycital.eet.android_app.items.Item;
import tomas_vycital.eet.android_app.items.ItemList;
import tomas_vycital.eet.android_app.printer.Printer;
import tomas_vycital.eet.android_app.settings.Settings;
import tomas_vycital.eet.lib.EETReceipt;

/**
 * Holds bought items
 */
public class Receipt implements ItemList {
    private static final SimpleDateFormat jsonDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    private static final SimpleDateFormat receiptDateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private static final SimpleDateFormat receiptTimeFormat = new SimpleDateFormat("HH:mm:ss");
    private final Handler handler;
    private final List<Item> items;
    EETReceipt eetReceipt;
    private int multiplier;
    private Date submitTime;
    private String bkp;
    private String fik;
    private String pkp;
    private long number;

    public Receipt(Handler handler) {
        this.items = new ArrayList<>();
        this.handler = handler;
        this.clear();
    }

    public Receipt(JSONObject receipt) throws JSONException, ParseException {
        this((Handler) null);
        this.fromJSON(receipt);
    }

    static Date parseDate(String string) throws ParseException {
        return Receipt.jsonDateFormat.parse(string);
    }

    @Nullable
    private static String getStringOrNull(JSONObject receipt, String name) {
        try {
            return receipt.getString(name);
        } catch (Exception ignored) {
            return null;
        }
    }

    public void add(Item item) {
        this.changed();
        this.items.add(item);
    }

    public Item remove(int i) {
        this.changed();
        return this.items.remove(i);
    }

    public void toggleNegative() {
        this.changed();
        this.multiplier *= -1;
    }

    boolean isEmpty() {
        return this.items.isEmpty();
    }

    String getReceiptStr() {
        String negative = this.multiplier == 1 ? "" : "-";
        String str = "";
        Date date = this.submitTime == null ? new Date() : this.submitTime;

        str += Settings.getHeading() + "\n";
        str += RSU.getSeparatorNl();
        str += RSU.nvl("DIČ", Settings.getDIC());
        str += RSU.nvl("IČO", Settings.getICO());
        str += RSU.nvl("Provozovna", Settings.getIdProvoz());
        str += RSU.nvl("Pokladna", Settings.getIdPokl());
        str += RSU.nvl("Č. účtenky", String.valueOf(this.number));
        str += RSU.nvl("Datum", Receipt.receiptDateFormat.format(date));
        str += RSU.nvl("Čas", Receipt.receiptTimeFormat.format(date));
        str += RSU.getSeparatorNl();

        int sum = 0;
        int[] vats = new int[VAT.values().length];
        HashMap<String, Integer> amounts = new HashMap<>();
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < this.items.size(); ++i) {
            Item item = this.items.get(i);
            Integer current = amounts.get(item.getName());
            if (current == null) {
                current = 0;
                items.add(item);
            }

            amounts.put(item.getName(), current + 1);
            sum += item.getPrice();
            vats[item.getVAT().ordinal()] += item.getVATH();
        }

        Collections.sort(items);
        for (int i = 0; i < items.size(); ++i) {
            Item item = items.get(i);
            int amount = amounts.get(item.getName());
            str += RSU.align(item.getName(), negative + item.getPriceRawStr(amount) + " kč") + "\n";
            str += "  " + amount + " ks, " + negative + item.getPriceRawStr() + " kč/ks, " + item.getVAT().toString() + " DPH\n";
        }

        str += RSU.getSeparatorNl();

        int sumVAT = 0;
        for (VAT vat : VAT.values()) {
            int sumOneVAT = vats[vat.ordinal()];
            sumVAT += sumOneVAT;

            if (sumOneVAT > 0) {
                str += RSU.align("       " + vat.getPaddedPercentage() + " DPH:", negative + Item.priceFormat.format(sumOneVAT / 100.0) + " kč\n");
            }
        }

        str += "\n";

        str += RSU.align("Součet bez DPH:", negative + Item.priceFormat.format((sum - sumVAT) / 100.0) + " kč\n");
        str += RSU.align("           DPH:", negative + Item.priceFormat.format(sumVAT / 100.0) + " kč\n");
        str += RSU.align("        Součet:", negative + Item.priceFormat.format(sum / 100.0) + " kč\n");

        if (this.bkp != null && this.pkp != null) {
            str += "\n";
            str += "BKP: " + this.bkp + "\n";
            if (this.fik == null) {
                str += "PKP: " + this.pkp + "\n";
            } else {
                str += "FIK: " + this.fik + "\n";
            }
            str += "Režim tržby: " + Settings.getModeStr() + "\n";
        }

        str += RSU.getSeparatorNl();

        str += Settings.getName() + "\n";
        str += Settings.getAddress() + "\n";

        str += RSU.getSeparatorNl();

        str += Settings.getFooting() + "\n";

        // Limit the receipt to the actual width of the physical receipt
        return str.replaceAll("(.{" + Settings.getReceiptWidth() + "})\\n?", "$1\n");
    }

    @Override
    public int size() {
        return this.items.size();
    }

    @Override
    public Item get(int i) {
        return this.items.get(i);
    }

    void submit(Handler handler) throws UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException, UnreadableKeyPassword, IOException {
        if (this.eetReceipt == null) {
            int celkTrzba = 0;
            SparseIntArray zaklDan = new SparseIntArray();

            for (int i = 0; i < this.items.size(); ++i) {
                Item item = this.items.get(i);
                celkTrzba += item.getPrice();
                zaklDan.put(item.getVATPercentage(), zaklDan.get(item.getVATPercentage()) + item.getPrice());
            }

            this.submitTime = new Date();
            this.eetReceipt = (new EETReceipt())
                    .setPrvniZaslani(true)
                    .setCelkTrzba(celkTrzba * this.multiplier)
                    .setDan1((int) (zaklDan.get(1) * VAT.basic.get()) * this.multiplier)
                    .setDan2((int) (zaklDan.get(2) * VAT.reduced1.get()) * this.multiplier)
                    .setDan3((int) (zaklDan.get(3) * VAT.reduced2.get()) * this.multiplier)
                    .setDatTrzby(this.submitTime)
                    .setDicPopl(Settings.getDIC())
                    .setIdPokl(Settings.getIdPokl())
                    .setIdProvoz(Settings.getIdProvoz())
                    .setOvereni(Settings.getVerifying())
                    .setPoradCis(String.valueOf(this.number))
                    .setRezim(0)
                    .setZaklDan1(zaklDan.get(1) * this.multiplier)
                    .setZaklDan2(zaklDan.get(2) * this.multiplier)
                    .setZaklDan3(zaklDan.get(3) * this.multiplier)
                    .setZaklNepodlDph(zaklDan.get(0) * this.multiplier)
                    .setP12(Settings.getKeyIS(), Settings.getKeyPassword().toCharArray())
            ;
        } else {
            this.eetReceipt.setPrvniZaslani(false);
        }

        (new Submit(this, handler)).start();
    }

    void print(Handler handler, Printer printer) {
        (new Print(this, handler, printer)).start();
    }

    public void clear() {
        this.items.clear();
        this.multiplier = 1;
        this.changed();
    }

    private void changed() {
        this.eetReceipt = null;
        this.bkp = null;
        this.pkp = null;
        this.fik = null;
        this.number = System.currentTimeMillis();

        if (this.handler != null) {
            this.handler.sendEmptyMessage(Messages.receiptChanged.ordinal());
        }
    }

    private int getPrice() {
        int price = 0;

        for (Item item : this.items) {
            price += item.getPrice();
        }

        return price;
    }

    public String getPriceStr() {
        return Item.priceFormat.format(this.multiplier * this.getPrice() / 100.0);
    }

    public Date getSubmitTime() {
        return this.submitTime;
    }

    JSONObject toJSON() throws JSONException {
        JSONObject receipt = new JSONObject();

        JSONArray items = new JSONArray();
        for (Item item : this.items) {
            items.put(item.toJSON());
        }

        receipt.put("multiplier", this.multiplier);
        receipt.put("submitTime", Receipt.jsonDateFormat.format(this.submitTime));
        receipt.put("bkp", this.bkp);
        receipt.put("pkp", this.pkp);
        receipt.put("fik", this.fik);
        receipt.put("items", items);

        return receipt;
    }

    public void fromJSON(JSONObject receipt) throws JSONException, ParseException {
        this.clear();

        this.multiplier = (int) receipt.get("multiplier");
        this.submitTime = Receipt.parseDate((String) receipt.get("submitTime"));
        this.bkp = Receipt.getStringOrNull(receipt, "bkp");
        this.pkp = Receipt.getStringOrNull(receipt, "pkp");
        this.fik = Receipt.getStringOrNull(receipt, "fik");

        JSONArray items = receipt.getJSONArray("items");
        for (int i = 0; i < items.length(); ++i) {
            this.items.add(new Item((JSONObject) items.get(i)));
        }
    }

    void onSubmit() {
        this.bkp = this.eetReceipt.getBKP();
        this.fik = this.eetReceipt.getFIK();
        this.pkp = this.eetReceipt.getPKP();
    }

    boolean isClearable() {
        return this.items.size() > 0;
    }

    boolean isSubmittable() {
        return this.pkp == null && this.items.size() > 0;
    }

    boolean isPrintable() {
        return this.items.size() > 0;
    }
}
