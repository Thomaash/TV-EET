package tomas_vycital.eet.android_app.items;

import android.support.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import tomas_vycital.eet.android_app.VAT;

public class Item implements Comparable<Item> {
    public static final DecimalFormat priceFormat = new DecimalFormat("0.00");

    private long price;
    private String name;
    private VAT vat;
    private ItemColor color;
    private String category;

    private String searchString;

    Item(String name, long price, VAT vat, ItemColor color, String category) {
        this.setUp(name, price, vat, color, category);
    }

    Item(String name, String priceStr, VAT vat, ItemColor color, String category) {
        String[] priceParts = priceStr.replaceAll("[^\\d,.]", "").split("[,.]");
        long price = Integer.valueOf(priceParts[0]) * 100;
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

        this.setUp(name, price, vat, color, category);
    }

    public Item(JSONObject object) throws JSONException {
        this.setUp(
                object.getString("name"),
                object.getLong("price"),
                VAT.fromID(object.getInt("VAT")),
                ItemColor.fromID(object.getInt("color")),
                object.getString("category")
        );
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("name", this.name);
        json.put("price", this.price);
        json.put("VAT", this.vat.getID());
        json.put("color", this.color.getID());
        json.put("category", this.category);
        return json;
    }

    private void setUp(String name, long price, VAT vat, ItemColor color, String category) {
        this.name = name;
        this.price = price;
        this.vat = vat;
        this.color = color;
        this.category = category;

        this.searchString = ""
                + this.name.toLowerCase() + "\t"
                + this.category.toLowerCase() + "\t"
                + this.vat.toString().toLowerCase() + "\t"
                + this.getPriceStr().toLowerCase()
        ;
    }

    public String getName() {
        return this.name;
    }

    public long getPrice() {
        return this.price;
    }

    String getPriceStr() {
        return Item.priceFormat.format(this.price / 100.0) + " kč";
    }

    public String getPriceRawStr() {
        return Item.priceFormat.format(this.price / 100.0);
    }

    public String getPriceRawStr(int amount) {
        return Item.priceFormat.format(amount * this.price / 100.0);
    }

    public VAT getVAT() {
        return this.vat;
    }

    public int getVATH() {
        return (int) (this.price * this.vat.get());
    }

    public int getVATPercentage() {
        return this.vat.getPercentage();
    }

    public ItemColor getColor() {
        return this.color;
    }

    String getBrief() {
        return this.name + " (" + this.price / 100f + ",-)";
    }

    @Override
    public int compareTo(@NonNull Item another) {
        return this.name.compareTo(another.name);
    }

    String getCategory() {
        return this.category;
    }

    boolean match(String str) {
        return this.searchString.contains(str);
    }
}
