package tomas_vycital.eet.android_app;

import android.support.design.widget.Snackbar;
import android.view.View;

import tomas_vycital.eet.android_app.items.Item;
import tomas_vycital.eet.android_app.receipt.Receipt;

/**
 * Created by tom on 18.2.17.
 */

class ReceiptItemGridAdapter extends ItemGridAdapter {
    ReceiptItemGridAdapter(MainActivity context, Receipt receipt) {
        super(context, receipt, receipt);
    }

    @Override
    protected View.OnClickListener getOnClickListener(final int position, final Item item) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReceiptItemGridAdapter.this.receipt.remove(position);
                ReceiptItemGridAdapter.this.notifyDataSetChanged();

                Snackbar.make(view, "Odebráno: " + item.getBrief(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        };
    }

}
