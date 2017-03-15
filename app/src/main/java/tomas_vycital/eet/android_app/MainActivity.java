package tomas_vycital.eet.android_app;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import tomas_vycital.eet.android_app.items.Item;
import tomas_vycital.eet.android_app.items.Items;
import tomas_vycital.eet.android_app.printer.BTPrinter;
import tomas_vycital.eet.android_app.receipt.Receipt;
import tomas_vycital.eet.android_app.settings.Settings;
import tomas_vycital.eet.android_app.settings.SettingsOCL;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Items items;
    private Receipt receipt;
    private BTPrinter printer;
    private Handler handler;

    private AvailableItemGridAdapter availableItemsAdapter;
    private ReceiptItemGridAdapter receiptItemsAdapter;

    private NavigationView navigationView;
    private FloatingActionButton fab;

    private Item currentItem;
    private MenuItem price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        Settings.setup(this.getSharedPreferences("settings", MODE_PRIVATE));

        this.items = new Items();
        this.printer = new BTPrinter();
        this.handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                MainActivity.this.handleMessage(msg);
            }
        };
        this.receipt = new Receipt(this.handler);

        this.fab = (FloatingActionButton) this.findViewById(R.id.fabPrint);
        assert this.fab != null;
        this.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.receiptUpdated();
                MainActivity.this.showOnly(R.id.receipt_include, R.id.menu_receipt);
            }
        });

        this.showOnly(R.id.items_include);

        DrawerLayout drawer = (DrawerLayout) this.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert drawer != null;
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        this.navigationView = (NavigationView) this.findViewById(R.id.nav_view);
        assert this.navigationView != null;
        this.navigationView.setNavigationItemSelectedListener(this);

        // Available items
        GridView availableItems = (GridView) MainActivity.this.findViewById(R.id.item_grid);
        MainActivity.this.availableItemsAdapter = new AvailableItemGridAdapter(MainActivity.this, MainActivity.this.items, MainActivity.this.receipt);
        assert availableItems != null;
        availableItems.setAdapter(MainActivity.this.availableItemsAdapter);

        // Receipt
        GridView receiptItems = (GridView) MainActivity.this.findViewById(R.id.menu_receipt_items);
        MainActivity.this.receiptItemsAdapter = new ReceiptItemGridAdapter(MainActivity.this, MainActivity.this.receipt);
        assert receiptItems != null;
        receiptItems.setAdapter(MainActivity.this.receiptItemsAdapter);

        // Settings
        Button settingsButton = (Button) MainActivity.this.findViewById(R.id.settings_save);
        assert settingsButton != null;
        LinearLayout settingsValues = (LinearLayout) MainActivity.this.findViewById(R.id.settings_values);
        assert settingsValues != null;
        new SettingsOCL(this, settingsButton, settingsValues, this.printer);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Check and request permissions if necessary
                if (MainActivity.this.checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED
                        || MainActivity.this.checkSelfPermission(Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED
                        || MainActivity.this.checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED
                        || MainActivity.this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        || MainActivity.this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    MainActivity.this.requestPermissions(new String[]{
                            Manifest.permission.INTERNET,
                            Manifest.permission.BLUETOOTH,
                            Manifest.permission.BLUETOOTH_ADMIN,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, 1);
                }

                // Connect to the last printer (if possible)
                String lastMAC = Settings.getLastMAC();
                if (lastMAC != null) {
                    try {
                        MainActivity.this.printer.connect(lastMAC);
                        Snackbar.make(MainActivity.this.fab, "Naposledy použitá tiskárna byla nalezene a automaticky připojena", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } catch (IOException ignored) {
                    }
                }
            }
        });
        thread.start();
    }

    private void handleMessage(Message msg) {
        switch (Messages.values()[msg.what]) {
            case exception:
                Exception e = (Exception) msg.obj;
                if (e != null) {
                    Snackbar.make(this.getWindow().getDecorView().getRootView(), e.getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                this.receiptUpdated();
                break;
            case receiptPriceChanged:
                // The is not initialized immediately
                if (this.price != null) {
                    this.price.setTitle(this.getString(R.string.str_price, this.receipt.getPriceStr()));
                }
                break;
        }
    }

    private void newReceipt() {
        this.receipt.clear();
        this.availableItemsAdapter.notifyDataSetChanged();
        this.receiptUpdated();
    }

    private void submitReceipt(View view, boolean negative) {
        if (this.receipt.isEmpty()) {
            Snackbar.make(view, "Účtenka je prázdná", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            return;
        }
        try {
            this.receipt.setNegative(negative);
            this.receipt.submit(this.handler);
        } catch (UnrecoverableKeyException | CertificateException | KeyStoreException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Snackbar.make(view, "Nepodařilo se načíst klíč", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.getMenuInflater().inflate(R.menu.main, menu);
        this.price = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_print:
                this.ocPrint(item.getActionView());
                break;
            case R.id.action_submit:
                this.submitReceipt(item.getActionView(), false);
                break;
            case R.id.action_submit_negative:
                this.submitReceipt(item.getActionView(), true);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.menu_items:
                this.availableItemsAdapter.notifyDataSetChanged();
                this.showOnly(R.id.items_include);
                break;
            case R.id.menu_receipt_items:
                this.receiptItemsAdapter.notifyDataSetChanged();
                this.showOnly(R.id.receipt_items_include);
                break;
            case R.id.menu_receipt:
                this.receiptUpdated();
                this.showOnly(R.id.receipt_include);
                break;
            case R.id.menu_printer:
                this.showOnly(R.id.printer_include);
                break;
            case R.id.menu_settings:
                this.showOnly(R.id.settings_include);
                break;
            case R.id.menu_edit_item:
                this.editItem(null);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void receiptUpdated() {
        // Update receipt text view
        TextView textView = (TextView) this.findViewById(R.id.receipt);
        assert textView != null;
        textView.setText(this.receipt.getReceiptStr());

        // Update receipt items view
        this.receiptItemsAdapter.notifyDataSetChanged();
    }

    private void showOnly(int id) {
        FrameLayout mainFrame = (FrameLayout) this.findViewById(R.id.main_frame);

        // Hide all
        assert mainFrame != null;
        for (int i = 0; i < mainFrame.getChildCount(); ++i) {
            mainFrame.getChildAt(i).setVisibility(View.GONE);
        }

        // Show the one with the ID
        View include = this.findViewById(id);
        assert include != null;
        include.setVisibility(View.VISIBLE);

        // Show actions
        switch (id) {
            case R.id.printer_include:
                this.refreshPrinterInfo();
                break;
        }

        // Show hide FAB
        switch (id) {
            case R.id.items_include:
            case R.id.receipt_items_include:
                this.fab.show();
                break;
            default:
                this.fab.hide();
        }
    }

    private void refreshPrinterInfo() {
        BluetoothDevice device = this.printer.getDevice();
        TextView info = (TextView) this.findViewById(R.id.printer_info);
        assert info != null;
        info.setText(
                device == null
                        ? "Nepřipojeno"
                        : "Připojeno: " + device.getName() + " (" + device.getAddress() + ")"
        );
    }

    private void showOnly(int includeID, int itemID) {
        this.showOnly(includeID);
        this.navigationView.setCheckedItem(itemID);
    }

    public void ocPrinterList(View view) {
        Snackbar.make(view, "Vyhledává se…", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        final LinearLayout list = (LinearLayout) this.findViewById(R.id.printers_list);
        assert list != null;
        list.removeAllViews();
        BluetoothDevice[] devices = this.printer.list();

        for (final BluetoothDevice device : devices) {
            final Button btn = new Button(this);
            btn.setText(device.getName() + " (" + device.getAddress() + ")");
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btn.setClickable(false);
                    try {
                        MainActivity.this.printer.connect(device);
                        Settings.setLastMAC(device.getAddress());
                        Snackbar.make(list, "Tiskárna zvolena: " + device.getName(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    } catch (IOException e) {
                        Snackbar.make(list, "K tiskárně se nepodařilo připojit", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    }

                    MainActivity.this.refreshPrinterInfo();
                }
            });

            list.addView(btn);
        }
        Snackbar.make(view, "Nalezeno: " + devices.length, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    public void ocPrinterTest(View view) {
        try {
            this.printer.printSelfTest();
            Snackbar.make(view, "Tiskne se", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } catch (IOException e) {
            Snackbar.make(view, "Nepodařilo se vytisknout", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
    }

    public void ocPrinterDisconnect(View view) {
        try {
            this.printer.disconnect();
            Snackbar.make(view, "Tiskárna byla odpojena", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        } catch (IOException e) {
            Snackbar.make(view, "Od tiskárny se nepodařilo odpojit", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }

        this.refreshPrinterInfo();
    }

    public void editItem(Item item) {
        View layout = this.findViewById(R.id.edit_item_include);
        assert layout != null;

        this.currentItem = item;

        if (item == null) {
            ((EditText) layout.findViewById(R.id.edit_item_name)).setText("");
            ((EditText) layout.findViewById(R.id.edit_item_price)).setText("");
            ((RadioGroup) layout.findViewById(R.id.edit_item_vat)).clearCheck();
            layout.findViewById(R.id.edit_item_change).setEnabled(false);
            layout.findViewById(R.id.edit_item_delete).setEnabled(false);
        } else {
            int id;
            switch (item.getVAT()) {
                case basic:
                    id = R.id.edit_item_vat_basic;
                    break;
                case reduced1:
                    id = R.id.edit_item_vat_reduced1;
                    break;
                case reduced2:
                    id = R.id.edit_item_vat_reduced2;
                    break;
                default:
                    id = R.id.edit_item_vat_exempt;
            }
            ((EditText) layout.findViewById(R.id.edit_item_name)).setText(item.getName());
            ((EditText) layout.findViewById(R.id.edit_item_price)).setText(item.getPriceRawStr());
            ((RadioButton) layout.findViewById(id)).toggle();
            layout.findViewById(R.id.edit_item_change).setEnabled(true);
            layout.findViewById(R.id.edit_item_delete).setEnabled(true);
        }

        this.showOnly(R.id.edit_item_include);
    }

    public void ocChangeItem(View view) {
        this.ocDeleteItem(view);
        this.ocAddItem(view);
    }

    public void ocAddItem(View view) {
        View layout = this.findViewById(R.id.edit_item_include);
        assert layout != null;

        VAT vat;
        switch (((RadioGroup) layout.findViewById(R.id.edit_item_vat)).getCheckedRadioButtonId()) {
            case R.id.edit_item_vat_basic:
                vat = VAT.basic;
                break;
            case R.id.edit_item_vat_reduced1:
                vat = VAT.reduced1;
                break;
            case R.id.edit_item_vat_reduced2:
                vat = VAT.reduced2;
                break;
            default: // R.id.edit_item_vat_exempt;
                vat = VAT.exempt;
        }

        Item item = new Item(
                ((EditText) layout.findViewById(R.id.edit_item_name)).getText().toString(),
                ((EditText) layout.findViewById(R.id.edit_item_price)).getText().toString(),
                vat
        );
        this.items.add(item);
        this.availableItemsAdapter.notifyDataSetChanged();
        this.editItem(item);
    }

    public void ocDeleteItem(View view) {
        this.items.remove(this.currentItem);
        this.availableItemsAdapter.notifyDataSetChanged();
        this.editItem(null);
    }

    public void ocSubmit(View view) {
        this.submitReceipt(view, false);
    }

    public void ocPrint(View view) {
        this.receipt.print(this.handler, this.printer);
    }

    public void ocClearReceipt(View view) {
        MainActivity.this.newReceipt();
        Snackbar.make(view, "Účtenka byla vyprázdněna", Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }
}
