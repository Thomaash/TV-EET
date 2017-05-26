package tomas_vycital.eet.android_app.items;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import tomas_vycital.eet.android_app.VAT;
import tomas_vycital.eet.android_app.settings.Settings;

/**
 * All available items, loads them from the settings, saves them or creates example items
 */
public class Items implements ItemList {
    private final List<Item> items;
    private final TreeSet<String> categories;

    /**
     * Tries to load saved items, if if fails then uses example items
     */
    public Items() {
        this.items = new ArrayList<>();
        this.categories = new TreeSet<>();

        try {
            // Saved items
            this.loadSaved();
        } catch (JSONException | NullPointerException e) {
            // Example items
            this.addNS(new Item("Example 01", 3899, VAT.basic, ItemColor.color0, "Odd"));
            this.addNS(new Item("Example 02", 3500, VAT.basic, ItemColor.color2, "Even"));
            this.addNS(new Item("Example 03", 5000, VAT.basic, ItemColor.color4, "Odd"));
            this.addNS(new Item("Example 04", 6600, VAT.exempt, ItemColor.color6, "Even"));
            this.addNS(new Item("Example 05", 5000, VAT.basic, ItemColor.color8, "Odd"));
            this.addNS(new Item("Example 06", 4500, VAT.basic, ItemColor.color10, "Even"));
            this.addNS(new Item("Example 07", 1200, VAT.basic, ItemColor.color12, "Odd"));
            this.addNS(new Item("Example 08", 1350, VAT.reduced1, ItemColor.color14, "Even"));
            this.addNS(new Item("Example 09", 1500, VAT.basic, ItemColor.color16, "Odd"));
            this.addNS(new Item("Example 10", 1100, VAT.basic, ItemColor.color18, "Even"));
            this.addNS(new Item("Example 11", 4300, VAT.reduced2, ItemColor.color20, "Odd"));
            this.addNS(new Item("Example 12", 10000, VAT.basic, ItemColor.color22, "Even"));
            Collections.sort(this.items);
        }
    }

    /**
     * Creates a new instance out of existing data
     *
     * @param items      A list of items
     * @param categories A tree set of categories
     */
    public Items(List<Item> items, TreeSet<String> categories) {
        this.items = items;
        this.categories = categories;
    }

    /**
     * Creates a new instance out of JSON string
     *
     * @param json JSON encoded data
     */
    public Items(String json) throws JSONException {
        this.items = new ArrayList<>();
        this.categories = new TreeSet<>();

        this.fromJSON(json);
    }

    /**
     * Loads saved items from the settings
     *
     * @throws JSONException        Thrown if the saved items are not valid
     * @throws NullPointerException @todo - no idea why this is here
     */
    public void loadSaved() throws JSONException, NullPointerException {
        this.items.clear();
        for (Item item : this.fromJSON(Settings.getItems())) {
            this.addNS(item);
        }
        Collections.sort(this.items);
    }

    /**
     * Returns item on index i
     *
     * @param i The index of the requested item
     * @return The requested item
     */
    @Override
    public Item get(int i) {
        return this.items.get(i);
    }

    /**
     * Adds an item to the list and sorts it
     *
     * @param item The item to be added
     */
    public void add(Item item) {
        this.addNS(item);
        Collections.sort(this.items);
        this.saveQ();
    }

    /**
     * Adds an item to the list without sorting or saving it
     *
     * @param item The item to be added
     */
    private void addNS(Item item) {
        this.items.add(item);
        this.addCategory(item.getCategory());
    }

    /**
     * Finds an item in the list and removes it
     *
     * @param item The item to be removed
     */
    void remove(Item item) {
        this.items.remove(item);
        this.saveQ();
    }

    /**
     * Returns the amount of items in the list
     *
     * @return The size of the list
     */
    @Override
    public int size() {
        return this.items.size();
    }

    /**
     * Creates a new instance with filtered items
     *
     * @param search   A string to search for
     * @param category Category name
     * @return Filtered items
     */
    @Override
    public Items filter(String search, String category) {
        String testString = search.toLowerCase();
        List<Item> filtered = new ArrayList<>();

        for (Item item : this.items) {
            if (item.match(testString) && (category == null || category.equals(item.getCategory()))) {
                filtered.add(item);
            }
        }

        return new Items(filtered, this.categories);
    }

    /**
     * Converts the internal list of items into JSON object
     *
     * @return JSONObject containing all the items
     * @throws JSONException Should never be thrown unless there's a bug in the code
     */
    private JSONObject toJSON() throws JSONException {
        JSONObject object = new JSONObject();
        JSONArray array = new JSONArray();
        for (Item item : this.items) {
            array.put(item.toJSON());
        }
        object.put("items", array);
        return object;
    }

    /**
     * Parses JSON (preferably generated by toJSON()) and returns a list
     *
     * @param json The JSON with items
     * @return Java list of items
     * @throws JSONException        Thrown when the supplied JSON is not valid
     * @throws NullPointerException @todo - no idea why this is here
     */
    private List<Item> fromJSON(String json) throws JSONException, NullPointerException {
        JSONObject object = new JSONObject(json);
        JSONArray array = (JSONArray) object.get("items");
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < array.length(); ++i) {
            this.addNS(new Item((JSONObject) array.get(i)));
        }
        return items;
    }

    /**
     * Saves all the items to the settings
     *
     * @throws JSONException Should never be thrown unless there is a bug in the code
     */
    private void save() throws JSONException {
        Settings.setItems(this.toJSON().toString());
    }

    /**
     * Just as save() but ignores errors
     */
    private void saveQ() {
        try {
            this.save();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds the category to the list if not already present
     *
     * @param category The category to be added
     */
    private void addCategory(String category) {
        this.categories.add(category);
    }

    /**
     * @return The list of all categories
     */
    @Override
    public String[] getCategories() {
        return categories.toArray(new String[0]);
    }
}
