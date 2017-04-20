package tomas_vycital.eet.android_app.items;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tomas_vycital.eet.android_app.BaseFragment;
import tomas_vycital.eet.android_app.MainActivity;
import tomas_vycital.eet.android_app.R;
import tomas_vycital.eet.android_app.receipt.Receipt;

public class ReceiptItemsFragment extends BaseFragment {
    private ItemList items;
    private ItemsListenerFactory clicks;
    private ItemsFragmentRecyclerViewAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ReceiptItemsFragment() {
    }

    public static ReceiptItemsFragment newInstance(Receipt receipt, MainActivity ma) {
        ReceiptItemsFragment fragment = new ReceiptItemsFragment();
        fragment.items = receipt;
        fragment.clicks = new ReceiptItemsListenerFactory(receipt, ma);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.layout = inflater.inflate(R.layout.receipt_items, container, false);

        // Set the adapter
        Context context = this.layout.getContext();
        RecyclerView recyclerView = (RecyclerView) this.layout;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        this.adapter = new ItemsFragmentRecyclerViewAdapter(this.items, this.clicks);
        recyclerView.setAdapter(this.adapter);

        return this.layout;
    }

    @Override
    public void refresh() {
        // Continue only if the fragment is initialized
        if (this.getView() == null) {
            return;
        }

        this.adapter.notifyDataSetChanged();
    }

    @Override
    public boolean fab() {
        return true;
    }
}
