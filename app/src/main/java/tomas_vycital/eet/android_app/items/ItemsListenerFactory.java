package tomas_vycital.eet.android_app.items;

import android.view.View;

interface ItemsListenerFactory {
    View.OnClickListener onClick(int position);

    View.OnLongClickListener onLongClick(int position);

    void setItems(ItemList filtered);
}
