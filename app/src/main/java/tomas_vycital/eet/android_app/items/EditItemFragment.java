package tomas_vycital.eet.android_app.items;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import tomas_vycital.eet.android_app.R;
import tomas_vycital.eet.android_app.RefreshableFragment;
import tomas_vycital.eet.android_app.VAT;

/**
 * Created by tom on 3.3.17.
 */

public class EditItemFragment extends Fragment implements View.OnClickListener, RefreshableFragment {
    private TextView name;
    private TextView price;
    private RadioGroup vat;
    private RadioButton vatBasic;
    private RadioButton vatExempt;
    private RadioButton vatReduced1;
    private RadioButton vatReduced2;

    private Button change;
    private Button add;
    private Button delete;

    private Items items;
    private Item currentItem;

    public EditItemFragment() {
        // Required empty public constructor
    }

    public static EditItemFragment newInstance(Items items) {
        EditItemFragment fragment = new EditItemFragment();
        fragment.items = items;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.edit_item, container, false);

        // Views
        this.name = (TextView) layout.findViewById(R.id.name);
        this.price = (TextView) layout.findViewById(R.id.price);
        this.vat = (RadioGroup) layout.findViewById(R.id.vat);
        this.vatBasic = (RadioButton) layout.findViewById(R.id.vat_basic);
        this.vatExempt = (RadioButton) layout.findViewById(R.id.vat_exempt);
        this.vatReduced1 = (RadioButton) layout.findViewById(R.id.vat_reduced1);
        this.vatReduced2 = (RadioButton) layout.findViewById(R.id.vat_reduced2);

        this.change = (Button) layout.findViewById(R.id.change);
        this.add = (Button) layout.findViewById(R.id.add);
        this.delete = (Button) layout.findViewById(R.id.delete);

        // Onclick listeners
        this.change.setOnClickListener(this);
        this.add.setOnClickListener(this);
        this.delete.setOnClickListener(this);

        // Inflate the layout for this fragment
        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.refresh();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add:
                this.createSetAndAddItem();
                Snackbar.make(v, "Položka byla přidána", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                break;
            case R.id.change:
                this.items.remove(this.currentItem);
                this.createSetAndAddItem();
                Snackbar.make(v, "Položka byla změněna", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                break;
            case R.id.delete:
                this.items.remove(this.currentItem);
                this.edit(null);
                Snackbar.make(v, "Položka byla smazána", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                break;
        }
    }

    private void createSetAndAddItem() {
        Item item = this.createItem();
        this.items.add(item);
        this.edit(item);
    }

    private Item createItem() {
        VAT vat;
        switch (this.vat.getCheckedRadioButtonId()) {
            case R.id.vat_basic:
                vat = VAT.basic;
                break;
            case R.id.vat_reduced1:
                vat = VAT.reduced1;
                break;
            case R.id.vat_reduced2:
                vat = VAT.reduced2;
                break;
            default: // R.id.vat_exempt;
                vat = VAT.exempt;
        }

        return new Item(
                this.name.getText().toString(),
                this.price.getText().toString(),
                vat
        );
    }

    public void edit(Item item) {
        this.currentItem = item;
        this.refresh();
    }

    private void updateViews() {
        if (this.currentItem == null) { // New item (empty fields)
            this.name.setText("");
            this.price.setText("");
            this.vat.clearCheck();
            this.change.setEnabled(false);
            this.add.setEnabled(true);
            this.delete.setEnabled(false);
        } else { // Existing item
            this.name.setText(this.currentItem.getName());
            this.price.setText(this.currentItem.getPriceRawStr());
            switch (this.currentItem.getVAT()) {
                case basic:
                    this.vatBasic.toggle();
                    break;
                case reduced1:
                    this.vatReduced1.toggle();
                    break;
                case reduced2:
                    this.vatReduced2.toggle();
                    break;
                default:
                    this.vatExempt.toggle();
            }
            this.change.setEnabled(true);
            this.add.setEnabled(true);
            this.delete.setEnabled(true);
        }
    }

    @Override
    public void refresh() {
        // Continue only if the fragment is initialized
        if (this.getView() == null) {
            return;
        }

        this.updateViews();
    }

    @Override
    public boolean fab() {
        return false;
    }
}
