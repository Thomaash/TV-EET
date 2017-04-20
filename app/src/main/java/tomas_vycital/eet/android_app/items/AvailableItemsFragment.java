package tomas_vycital.eet.android_app.items;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import tomas_vycital.eet.android_app.BaseFragment;
import tomas_vycital.eet.android_app.MainActivity;
import tomas_vycital.eet.android_app.R;
import tomas_vycital.eet.android_app.receipt.Receipt;

public class AvailableItemsFragment extends BaseFragment implements View.OnClickListener, View.OnKeyListener {
    private ItemList items;
    private ItemsListenerFactory clicks;
    private ItemsFragmentRecyclerViewAdapter adapter;
    private AutoCompleteTextView search;
    private Button searchBtn;
    private RecyclerView list;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AvailableItemsFragment() {
    }

    public static AvailableItemsFragment newInstance(Items items, Receipt receipt, MainActivity ma) {
        AvailableItemsFragment fragment = new AvailableItemsFragment();
        fragment.items = items;
        fragment.clicks = new AvailableItemsListenerFactory(items, receipt, ma);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.layout = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Views
        this.search = (AutoCompleteTextView) this.layout.findViewById(R.id.search);
        this.searchBtn = (Button) this.layout.findViewById(R.id.search_btn);
        this.list = (RecyclerView) this.layout.findViewById(R.id.list);

        // Set the adapter
        Context context = this.layout.getContext();
        this.adapter = new ItemsFragmentRecyclerViewAdapter(this.items, this.clicks);
        this.list.setLayoutManager(new LinearLayoutManager(context));
        this.list.setAdapter(this.adapter);

        // Categories autocompletion
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(), android.R.layout.select_dialog_singlechoice, this.items.getCategories());
        this.search.setThreshold(0);
        this.search.setAdapter(adapter);
        this.search.setOnKeyListener(this);

        // Set onclick listener
        this.searchBtn.setOnClickListener(this);

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
    public void onStart() {
        super.onStart();

        // The fragment was recreated, filter the items again
        this.onClick(null);
    }

    @Override
    public boolean fab() {
        return true;
    }

    @Override
    public void onClick(View view) {
        this.adapter.setItems(this.items.filter(this.search.getText().toString()));
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_ENTER:
                case KeyEvent.KEYCODE_BACK:
                    this.onClick(view);
                    return true;
            }
        }

        return false;
    }
}
