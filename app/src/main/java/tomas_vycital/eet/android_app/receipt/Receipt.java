package tomas_vycital.eet.android_app.receipt;

import android.os.Handler;
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
import tomas_vycital.eet.android_app.items.Item;
import tomas_vycital.eet.android_app.items.ItemList;
import tomas_vycital.eet.android_app.printer.Printer;
import tomas_vycital.eet.android_app.printer.PrinterUtils;
import tomas_vycital.eet.android_app.settings.Settings;
import tomas_vycital.eet.lib.EETReceipt;

/**
 * Holds bought items
 */

public class Receipt implements ItemList {
    private static final SimpleDateFormat jsonDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
    private final Handler handler;
    EETReceipt eetReceipt;
    private List<Item> items;
    private int multiplier;
    private Date submitTime;

    public Receipt(Handler handler) {
        this.items = new ArrayList<>();
        this.handler = handler;
        this.clear();
    }

    public Receipt(JSONObject receipt) throws JSONException, ParseException {
        this((Handler) null);
        this.fromJSON(receipt);
    }

    public void add(Item item) {
        this.changed();
        this.items.add(item);
    }

    public void remove(int i) {
        this.changed();
        this.items.remove(i);
    }

    public void setNegative(boolean negative) {
        this.changed();
        this.multiplier = negative ? -1 : 1;
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    public String getReceiptStr() {
        String negative = this.multiplier == 1 ? "" : "-";
        String str = Settings.getHeading() + "\n"
                + PrinterUtils.getSeparatorNl()
                + "DIČ: " + Settings.getDIC() + "\n"
                + PrinterUtils.getSeparatorNl();
        HashMap<String, Integer> amounts = new HashMap<>();
        List<Item> items = new ArrayList<>();

        int sumWithVAT = 0;
        int sumWithoutVAT = 0;

        for (int i = 0; i < this.items.size(); ++i) {
            Item item = this.items.get(i);
            Integer current = amounts.get(item.getName());
            if (current == null) {
                current = 0;
                items.add(item);
            }

            amounts.put(item.getName(), current + 1);
            sumWithVAT += item.getPrice();
            sumWithoutVAT += item.getPrice() * (1 - (float) item.getVATPercentage() / 100);
        }

        Collections.sort(items);
        for (int i = 0; i < items.size(); ++i) {
            Item item = items.get(i);
            int amount = amounts.get(item.getName());
            str += PrinterUtils.align(amount + " ks: " + item.getName(), negative + item.getPriceStr()) + "\n";
        }

        str += PrinterUtils.getSeparatorNl();
        str += PrinterUtils.align("Součet bez DPH:", negative + Item.priceFormat.format(sumWithoutVAT / 100.0) + " kč\n");
        str += PrinterUtils.align("           DPH:", negative + Item.priceFormat.format((sumWithVAT - sumWithoutVAT) / 100.0) + " kč\n");
        str += PrinterUtils.align("        Součet:", negative + Item.priceFormat.format(sumWithVAT / 100.0) + " kč\n");

        if (this.eetReceipt != null) {
            str += "\n";
            str += "BKP: " + this.eetReceipt.getBKP() + "\n";
            if (this.eetReceipt.getFIK() == null) {
                str += "PKP: " + this.eetReceipt.getPKP() + "\n";
            } else {
                str += "FIK: " + this.eetReceipt.getFIK() + "\n";
            }
            str += "Režim tržby: " + Settings.getModeStr() + "\n";
        }
        str += PrinterUtils.getSeparatorNl();

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

    public void submit(Handler handler) throws IOException, UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException {
        if (this.eetReceipt == null) {
            int celkTrzba = 0;
            SparseIntArray zaklDan = new SparseIntArray();

            for (int i = 0; i < this.items.size(); ++i) {
                Item item = this.items.get(i);
                celkTrzba += item.getPrice();
                zaklDan.put(item.getVATPercentage(), zaklDan.get(item.getVATPercentage()) + item.getPrice());
            }

            this.eetReceipt = (new EETReceipt())
                    .setPrvniZaslani(true)
                    .setCelkTrzba(celkTrzba * this.multiplier)
                    .setDan1((int) (zaklDan.get(1) * VAT.basic.get()) * this.multiplier)
                    .setDan2((int) (zaklDan.get(2) * VAT.reduced1.get()) * this.multiplier)
                    .setDan3((int) (zaklDan.get(3) * VAT.reduced2.get()) * this.multiplier)
                    .setDatTrzby(new Date())
                    .setDicPopl(Settings.getDIC())
                    .setIdPokl(Settings.getIdPokl())
                    .setIdProvoz(Settings.getIdProvoz())
                    .setOvereni(Settings.getVerifying())
                    .setPoradCis("0/6460/ZQ42")
                    .setRezim(0)
                    .setZaklDan1(zaklDan.get(1) * this.multiplier)
                    .setZaklDan2(zaklDan.get(2) * this.multiplier)
                    .setZaklDan3(zaklDan.get(3) * this.multiplier)
                    .setZaklNepodlDph(zaklDan.get(0) * this.multiplier)
                    .setP12(Settings.getKeyIS(), "eet".toCharArray())
            ;
        } else {
            this.eetReceipt.setPrvniZaslani(false);
        }

        (new Submit(this, handler)).start();
    }

    public void print(Handler handler, Printer printer) {
        (new Print(this, handler, printer)).start();
    }

    public void clear() {
        this.items.clear();
        this.changed();
    }

    private void changed() {
        this.eetReceipt = null;
        this.multiplier = 1;
        if (this.handler != null) {
            this.handler.sendEmptyMessage(Messages.receiptPriceChanged.ordinal());
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
        return Item.priceFormat.format(this.getPrice() / 100.0);
    }

    public Date getSubmitTime() {
        return this.submitTime;
    }

    void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    JSONObject toJSON() throws JSONException {
        JSONObject receipt = new JSONObject();

        JSONArray items = new JSONArray();
        for (Item item : this.items) {
            items.put(item.toJSON());
        }

        receipt.put("multiplier", this.multiplier);
        receipt.put("submitTime", Receipt.jsonDateFormat.format(this.submitTime));
        receipt.put("items", items);

        return receipt;
    }

    public void fromJSON(JSONObject receipt) throws JSONException, ParseException {
        this.multiplier = (int) receipt.get("multiplier");
        this.submitTime = Receipt.jsonDateFormat.parse((String) receipt.get("submitTime"));

        this.items.clear();
        JSONArray items = receipt.getJSONArray("items");
        for (int i = 0; i < items.length(); ++i) {
            this.items.add(new Item((JSONObject) items.get(i)));
        }
    }
}
