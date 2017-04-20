package tomas_vycital.eet.android_app.items;

import android.support.design.widget.Snackbar;
import android.view.View;

import tomas_vycital.eet.android_app.MainActivity;
import tomas_vycital.eet.android_app.receipt.Receipt;

class ReceiptItemsListenerFactory implements ItemsListenerFactory {
    private final Receipt receipt;
    private final MainActivity ma;

    ReceiptItemsListenerFactory(Receipt receipt, MainActivity ma) {
        this.receipt = receipt;
        this.ma = ma;
    }

    @Override
    public View.OnClickListener onClick(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Item item = ReceiptItemsListenerFactory.this.receipt.remove(position);
                Snackbar.make(view, "Odebráno: " + item.getBrief(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        };
    }

    @Override
    public View.OnLongClickListener onLongClick(final int position) {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ReceiptItemsListenerFactory.this.ma.editItem(ReceiptItemsListenerFactory.this.receipt.get(position));
                return true;
            }
        };
    }

    @Override
    public void setItems(ItemList filtered) {

    }
}
