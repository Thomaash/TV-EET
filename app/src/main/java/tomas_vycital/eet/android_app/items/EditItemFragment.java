package tomas_vycital.eet.android_app.items;

import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tomas_vycital.eet.android_app.BaseFragment;
import tomas_vycital.eet.android_app.R;
import tomas_vycital.eet.android_app.VAT;

public class EditItemFragment extends BaseFragment implements View.OnClickListener {
    private AutoCompleteTextView category;
    private GridLayout colors;
    private RadioButton vatBasic;
    private RadioButton vatExempt;
    private RadioButton vatReduced1;
    private RadioButton vatReduced2;
    private RadioGroup vat;
    private TextView name;
    private TextView price;
    private List<RadioButton> colorRBs;

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
        this.layout = inflater.inflate(R.layout.edit_item, container, false);

        // Views
        this.category = (AutoCompleteTextView) this.layout.findViewById(R.id.category);
        this.colors = (GridLayout) this.layout.findViewById(R.id.colors);
        this.name = (TextView) this.layout.findViewById(R.id.name);
        this.price = (TextView) this.layout.findViewById(R.id.price);
        this.vat = (RadioGroup) this.layout.findViewById(R.id.vat);
        this.vatBasic = (RadioButton) this.layout.findViewById(R.id.vat_basic);
        this.vatExempt = (RadioButton) this.layout.findViewById(R.id.vat_exempt);
        this.vatReduced1 = (RadioButton) this.layout.findViewById(R.id.vat_reduced1);
        this.vatReduced2 = (RadioButton) this.layout.findViewById(R.id.vat_reduced2);

        // Categories autocompletion
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this.getContext(), android.R.layout.select_dialog_singlechoice, this.items.getCategories());
        this.category.setThreshold(0);
        this.category.setAdapter(adapter);

        // Colors
        this.colorRBs = new ArrayList<>();
        for (ItemColor color : ItemColor.values()) {
            RadioButton rb = new RadioButton(this.getContext());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                // Set the color properly
                rb.setButtonTintList(new ColorStateList(
                        new int[][]{
                                new int[]{-android.R.attr.state_enabled},
                                new int[]{android.R.attr.state_enabled}
                        },
                        new int[]{
                                color.getInt(),
                                color.getInt()
                        }
                ));
            } else {
                // Fallback to background on old devices
                rb.setBackgroundColor(color.getInt());
            }
            rb.setTag(R.id.colorRBs, tags.colorRB);
            rb.setTag(R.id.colorRB_colorID, color.getID());
            rb.setOnClickListener(this);
            this.colorRBs.add(rb);
            this.colors.addView(rb);
        }

        // Buttons
        this.change = (Button) this.layout.findViewById(R.id.change);
        this.add = (Button) this.layout.findViewById(R.id.add);
        this.delete = (Button) this.layout.findViewById(R.id.delete);

        // Onclick listeners
        this.change.setOnClickListener(this);
        this.add.setOnClickListener(this);
        this.delete.setOnClickListener(this);

        // Inflate the layout for this fragment
        return this.layout;
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
                this.info("Položka byla přidána");
                break;
            case R.id.change:
                this.items.remove(this.currentItem);
                this.createSetAndAddItem();
                this.info("Položka byla změněna");
                break;
            case R.id.delete:
                this.items.remove(this.currentItem);
                this.edit(null);
                this.info("Položka byla smazána");
                break;
            default:
                switch ((tags) v.getTag(R.id.colorRBs)) {
                    case colorRB:
                        for (RadioButton rb : this.colorRBs) {
                            if (v != rb) {
                                rb.setChecked(false);
                            }
                        }
                        break;
                }
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
        ItemColor color = ItemColor.color24;
        for (RadioButton rb : this.colorRBs) {
            if (rb.isChecked()) {
                color = ItemColor.fromID((int) rb.getTag(R.id.colorRB_colorID));
            }
        }

        return new Item(
                this.name.getText().toString(),
                this.price.getText().toString(),
                vat,
                color,
                this.category.getText().toString()
        );
    }

    public void edit(Item item) {
        this.currentItem = item;
        this.refresh();
    }

    private void updateViews() {
        if (this.currentItem == null) { // New item (empty fields)
            // Inputs
            this.name.setText("");
            this.price.setText("");
            this.vat.clearCheck();
            for (RadioButton rb : this.colorRBs) {
                rb.setChecked(false);
            }
            this.category.setText("");

            // Buttons
            this.change.setEnabled(false);
            this.add.setEnabled(true);
            this.delete.setEnabled(false);
        } else { // Existing item
            // Inputs
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
            for (RadioButton rb : this.colorRBs) {
                rb.setChecked((int) rb.getTag(R.id.colorRB_colorID) == this.currentItem.getColor().getID());
            }
            this.category.setText(this.currentItem.getCategory());

            // Buttons
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

    private enum tags {
        colorRB
    }
}
