package tomas_vycital.eet.android_app.items;

public interface ItemList {
    Item get(int i);

    int size();

    ItemList filter(String str);

    String[] getCategories();
}
