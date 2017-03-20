package tomas_vycital.eet.android_app.items;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import tomas_vycital.eet.android_app.VAT;

/**
 * Created by tom on 18.2.17.
 */

public class Item implements Comparable<Item> {
    public static DecimalFormat priceFormat = new DecimalFormat("0.00");

    private final int price;
    private final String name;
    private final VAT vat;

    Item(String name, int price, VAT vat) {
        this.name = name;
        this.price = price;
        this.vat = vat;
    }

    public Item(String name, String priceStr, VAT vat) {
        String[] priceParts = priceStr.replaceAll("[^\\d,.]", "").split("[,.]");
        int price = Integer.valueOf(priceParts[0]) * 100;
        if (priceParts.length > 1) {
            switch (priceParts[1].length()) {
                case 0:
                    priceParts[1] = "0";
                    break;
                case 1:
                    priceParts[1] += "0";
                    break;
                case 2:
                    break;
                default:
                    priceParts[1] = priceParts[1].substring(0, 2);
            }
            price += Integer.valueOf(priceParts[1]);
        }

        this.name = name;
        this.price = price;
        this.vat = vat;
    }

    public Item(JSONObject object) throws JSONException {
        this.name = (String) object.get("name");
        this.price = (int) object.get("price");
        this.vat = VAT.fromID((Integer) object.get("VAT"));
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("name", this.name);
        json.put("price", this.price);
        json.put("VAT", this.vat.getID());
        return json;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getPriceStr() {
        return Item.priceFormat.format(this.price / 100.0) + " kč";
    }

    public String getPriceRawStr() {
        return Item.priceFormat.format(this.price / 100.0);
    }

    public VAT getVAT() {
        return this.vat;
    }

    public int getVATPercentage() {
        return this.vat.getPercentage();
    }

    public String getDPHStr() {
        return this.vat.toString();
    }

    public String getBrief() {
        return this.name + " (" + this.price / 100f + ",-)";
    }

    @Override
    public int compareTo(@NonNull Item another) {
        return this.name.compareTo(another.name);
    }
}
