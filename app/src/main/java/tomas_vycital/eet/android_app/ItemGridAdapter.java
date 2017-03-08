package tomas_vycital.eet.android_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import tomas_vycital.eet.android_app.items.Item;
import tomas_vycital.eet.android_app.items.ItemList;
import tomas_vycital.eet.android_app.receipt.Receipt;

/**
 * Created by tom on 3.3.17.
 */

abstract class ItemGridAdapter extends BaseAdapter {
    private final ItemList items;
    private final MainActivity context;
    Receipt receipt;

    ItemGridAdapter(MainActivity context, ItemList items, Receipt receipt) {
        this.context = context;
        this.items = items;
        this.receipt = receipt;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ItemGridAdapter.RecordHolder holder;
        Item item = this.items.get(position);

        if (view == null) {
            // New view
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item, null);

            holder = new ItemGridAdapter.RecordHolder();
            holder.container = view;
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.price = (TextView) view.findViewById(R.id.price);
            holder.dph = (TextView) view.findViewById(R.id.dph);

            view.setTag(holder);
        } else {
            // Recycled view
            holder = (ItemGridAdapter.RecordHolder) view.getTag();
        }

        // Set data for new/recycled view
        holder.name.setText(item.getName());
        holder.price.setText(item.getPriceStr());
        holder.dph.setText(item.getDPHStr());

        view.setOnClickListener(this.getOnClickListener(position, item));
        view.setOnLongClickListener(this.getOnLongClickListener(this.context, item));

        // Update it
        this.notifyDataSetChanged();

        return view;
    }

    private View.OnLongClickListener getOnLongClickListener(final MainActivity ma, final Item item) {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ma.editItem(item);
                return true;
            }
        };
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    protected abstract View.OnClickListener getOnClickListener(final int position, final Item item);

    private class RecordHolder {
        View container;
        TextView name;
        TextView price;
        TextView dph;
    }
}
