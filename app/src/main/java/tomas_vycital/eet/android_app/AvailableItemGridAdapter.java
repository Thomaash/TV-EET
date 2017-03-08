package tomas_vycital.eet.android_app;

import android.support.design.widget.Snackbar;
import android.view.View;

import tomas_vycital.eet.android_app.items.Item;
import tomas_vycital.eet.android_app.items.Items;
import tomas_vycital.eet.android_app.receipt.Receipt;

/**
 * Created by tom on 18.2.17.
 */

class AvailableItemGridAdapter extends ItemGridAdapter {

    AvailableItemGridAdapter(MainActivity context, Items items, Receipt receipt) {
        super(context, items, receipt);
    }

    @Override
    protected View.OnClickListener getOnClickListener(final int position, final Item item) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    AvailableItemGridAdapter.this.receipt.add(item);
                    Snackbar.make(view, "Zakoupeno: " + item.getBrief(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        };
    }
}
