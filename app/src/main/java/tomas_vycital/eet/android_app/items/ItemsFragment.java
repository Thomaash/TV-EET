package tomas_vycital.eet.android_app.items;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tomas_vycital.eet.android_app.R;
import tomas_vycital.eet.android_app.RefreshableFragment;

public class ItemsFragment extends Fragment implements RefreshableFragment {
    private ItemList items;
    private ItemsListenerFactory clicks;
    private ItemsFragmentRecyclerViewAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemsFragment() {
    }

    public static ItemsFragment newInstance(ItemList items, ItemsListenerFactory clicks) {
        ItemsFragment fragment = new ItemsFragment();
        fragment.items = items;
        fragment.clicks = clicks;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        this.adapter = new ItemsFragmentRecyclerViewAdapter(this.items, this.clicks);
        recyclerView.setAdapter(this.adapter);

        return view;
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
