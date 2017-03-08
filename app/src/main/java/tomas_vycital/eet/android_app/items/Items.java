package tomas_vycital.eet.android_app.items;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import tomas_vycital.eet.android_app.Settings;
import tomas_vycital.eet.android_app.VAT;

/**
 * Created by tom on 18.2.17.
 */

public class Items implements ItemList {
    private final List<Item> items;

    public Items() {
        List<Item> items;
        try {
            // Saved items
            items = this.fromJSON(Settings.getItems());
        } catch (JSONException e) {
            // Example items
            items = Arrays.asList(
                    new Item("Example 1", 3899, VAT.basic),
                    new Item("Example 2", 3500, VAT.basic),
                    new Item("Example 3", 5000, VAT.basic),
                    new Item("Example 4", 6600, VAT.exempt),
                    new Item("Example 5", 5000, VAT.basic),
                    new Item("Example 6", 4500, VAT.basic),
                    new Item("Example 7", 1200, VAT.basic),
                    new Item("Example 8", 1350, VAT.reduced1),
                    new Item("Example 9", 1500, VAT.basic),
                    new Item("Example 10", 1100, VAT.basic),
                    new Item("Example 11", 4300, VAT.reduced2),
                    new Item("Example 12", 10000, VAT.basic)
            );
        }
        this.items = items;
        Collections.sort(this.items);
    }

    @Override
    public Item get(int i) {
        return this.items.get(i);
    }

    public void add(Item item) {
        this.items.add(item);
        Collections.sort(this.items);
        this.saveQ();
    }

    public void remove(Item item) {
        this.items.remove(item);
        this.saveQ();
    }

    @Override
    public int size() {
        return this.items.size();
    }

    private JSONObject toJSON() throws JSONException {
        JSONObject object = new JSONObject();
        JSONArray array = new JSONArray();
        for (Item item : this.items) {
            array.put(item.toJSON());
        }
        object.put("items", array);
        return object;
    }

    private List<Item> fromJSON(String json) throws JSONException {
        JSONObject object = new JSONObject(json);
        JSONArray array = (JSONArray) object.get("items");
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < array.length(); ++i) {
            items.add(new Item((JSONObject) array.get(i)));
        }
        return items;
    }

    private void save() throws JSONException {
        Settings.setItems(this.toJSON().toString());
    }

    private void saveQ() {
        try {
            this.save();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
