package tomas_vycital.eet.android_app.items;

import android.view.View;

/**
 * Created by tom on 21.3.17.
 */

interface ItemsListenerFactory {
    View.OnClickListener onClick(int position);

    View.OnLongClickListener onLongClick(int position);
}
