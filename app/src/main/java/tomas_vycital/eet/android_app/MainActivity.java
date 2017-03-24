package tomas_vycital.eet.android_app;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;

import tomas_vycital.eet.android_app.history.HistoryFragment;
import tomas_vycital.eet.android_app.items.AvailableItemsListenerFactory;
import tomas_vycital.eet.android_app.items.EditItemFragment;
import tomas_vycital.eet.android_app.items.Item;
import tomas_vycital.eet.android_app.items.Items;
import tomas_vycital.eet.android_app.items.ItemsFragment;
import tomas_vycital.eet.android_app.items.ReceiptItemsListenerFactory;
import tomas_vycital.eet.android_app.printer.BTPrinter;
import tomas_vycital.eet.android_app.printer.PrinterFragment;
import tomas_vycital.eet.android_app.receipt.Receipt;
import tomas_vycital.eet.android_app.receipt.ReceiptFragment;
import tomas_vycital.eet.android_app.receipt.Receipts;
import tomas_vycital.eet.android_app.settings.BackupsFragment;
import tomas_vycital.eet.android_app.settings.Settings;
import tomas_vycital.eet.android_app.settings.SettingsFragment;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Receipt receipt;
    private BTPrinter printer;

    private NavigationView navigationView;
    private FloatingActionButton fab;

    private MenuItem price;

    private ItemsFragment availableItemsFragment;
    private ItemsFragment receiptItemsFragment;
    private ReceiptFragment receiptFragment;
    private HistoryFragment historyFragment;
    private PrinterFragment printerFragment;
    private SettingsFragment settingsFragment;
    private BackupsFragment backupsFragment;
    private EditItemFragment editItemFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) this.findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        Settings.setup(this.getSharedPreferences("settings", MODE_PRIVATE));
        Receipts.setup(this);

        Handler handler = new MessageHandler(this);
        Items items = new Items();
        this.printer = new BTPrinter(handler);
        this.receipt = new Receipt(handler);

        this.fab = (FloatingActionButton) this.findViewById(R.id.fabPrint);
        assert this.fab != null;
        this.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.showOnly(MainActivity.this.receiptFragment, R.id.menu_receipt);
            }
        });

        DrawerLayout drawer = (DrawerLayout) this.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert drawer != null;
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        this.navigationView = (NavigationView) this.findViewById(R.id.nav_view);
        assert this.navigationView != null;
        this.navigationView.setNavigationItemSelectedListener(this);

        // Fragments
        this.availableItemsFragment = ItemsFragment.newInstance(items, new AvailableItemsListenerFactory(items, this.receipt, this));
        this.receiptItemsFragment = ItemsFragment.newInstance(this.receipt, new ReceiptItemsListenerFactory(this.receipt, this));
        this.receiptFragment = ReceiptFragment.newInstance(this.receipt, this.printer, handler);
        this.historyFragment = HistoryFragment.newInstance(this);
        this.printerFragment = PrinterFragment.newInstance(this.printer, handler);
        this.settingsFragment = SettingsFragment.newInstance(this.printer);
        this.backupsFragment = BackupsFragment.newInstance(items);
        this.editItemFragment = EditItemFragment.newInstance(items);

        // Default fragment (all items)
        this.showOnly(this.availableItemsFragment, R.id.menu_items);

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

    void handleMessage(Message msg) {
        switch (Messages.values()[msg.what]) {
            case exception:
                Exception e = (Exception) msg.obj;
                if (e != null) {
                    Snackbar.make(this.getWindow().getDecorView().getRootView(), e.getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
                this.receiptUpdated();
                break;
            case receiptChanged:
                this.receiptUpdated();
                break;
            case btNotEnabled:
                Snackbar.make(this.getWindow().getDecorView().getRootView(), "Není zapnutý Bluetooth", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                break;
            case clearReceipt:
                this.receipt.clear();
                break;
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
            case R.id.action_receipt_negative:
                this.receipt.toggleNegative();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.menu_items:
                this.showOnly(this.availableItemsFragment);
                break;
            case R.id.menu_receipt_items:
                this.showOnly(this.receiptItemsFragment);
                break;
            case R.id.menu_receipt:
                this.showOnly(this.receiptFragment);
                break;
            case R.id.menu_history:
                this.showOnly(this.historyFragment);
                break;
            case R.id.menu_printer:
                this.showOnly(this.printerFragment);
                break;
            case R.id.menu_settings:
                this.showOnly(this.settingsFragment);
                break;
            case R.id.menu_backups:
                this.showOnly(this.backupsFragment);
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
        // Set the price if already initialized
        if (this.price != null) {
            this.price.setTitle(this.getString(R.string.str_price, this.receipt.getPriceStr()));
        }

        // Refresh receipt preview
        this.receiptFragment.refresh();
        this.receiptItemsFragment.refresh();
    }

    private void showOnly(Fragment f) {
        // Custom fragment change actions
        RefreshableFragment rf = ((RefreshableFragment) f);
        // rf.refresh();
        if (rf.fab()) {
            this.fab.show();
        } else {
            this.fab.hide();
        }

        // Change fragments
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragments, f);
        ft.commit();
    }

    private void showOnly(Fragment fragment, int itemID) {
        this.showOnly(fragment);
        this.navigationView.setCheckedItem(itemID);
    }

    public void editItem(Item item) {
        this.editItemFragment.edit(item);
        this.showOnly(this.editItemFragment, R.id.menu_edit_item);
    }

    public void setReceipt(JSONObject receipt) throws JSONException, ParseException {
        this.receipt.fromJSON(receipt);
        this.showOnly(this.receiptItemsFragment);
    }
}
