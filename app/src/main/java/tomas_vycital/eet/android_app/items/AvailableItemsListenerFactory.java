package tomas_vycital.eet.android_app.items;

import android.support.design.widget.Snackbar;
import android.view.View;

import tomas_vycital.eet.android_app.MainActivity;
import tomas_vycital.eet.android_app.receipt.Receipt;

/**
 * Created by tom on 21.3.17.
 */

public class AvailableItemsListenerFactory implements ItemsListenerFactory {
    private final Items items;
    private final Receipt receipt;
    private final MainActivity ma;

    public AvailableItemsListenerFactory(Items items, Receipt receipt, MainActivity ma) {
        this.items = items;
        this.receipt = receipt;
        this.ma = ma;
    }

    @Override
    public View.OnClickListener onClick(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Item item = AvailableItemsListenerFactory.this.items.get(position);
                AvailableItemsListenerFactory.this.receipt.add(item);
                Snackbar.make(view, "Přidáno: " + item.getBrief(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        };
    }

    @Override
    public View.OnLongClickListener onLongClick(final int position) {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AvailableItemsListenerFactory.this.ma.editItem(AvailableItemsListenerFactory.this.items.get(position));
                return true;
            }
        };
    }
}
