package tomas_vycital.eet.android_app.items;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import tomas_vycital.eet.android_app.R;

class ItemsFragmentRecyclerViewAdapter extends RecyclerView.Adapter<ItemsFragmentRecyclerViewAdapter.ViewHolder> {
    private final ItemsListenerFactory clicks;
    private ItemList items;

    ItemsFragmentRecyclerViewAdapter(ItemList items, ItemsListenerFactory clicks) {
        this.items = items;
        this.clicks = clicks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setItem(this.items.get(position));
        holder.view.setOnClickListener(this.clicks.onClick(position));
        holder.view.setOnLongClickListener(this.clicks.onLongClick(position));
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public void setItems(ItemList filtered) {
        this.items = filtered;
        this.clicks.setItems(filtered);
        this.notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final View view;
        private final TextView name;
        private final TextView price;
        private final TextView vat;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            this.name = (TextView) view.findViewById(R.id.name);
            this.price = (TextView) view.findViewById(R.id.price);
            this.vat = (TextView) view.findViewById(R.id.vat);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + this.name.getText() + "'";
        }

        void setItem(Item item) {
            this.name.setText(item.getName());
            this.price.setText(item.getPriceStr());
            this.vat.setText(item.getVAT().toString());
        }
    }
}
