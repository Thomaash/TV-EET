package tomas_vycital.eet.android_app.printer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.ParcelUuid;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import tomas_vycital.eet.android_app.Messages;
import tomas_vycital.eet.android_app.settings.Charset;
import tomas_vycital.eet.android_app.settings.Settings;

public class BTPrinter implements Printer {
    private final BluetoothAdapter adapter;
    private final Handler handler;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private BluetoothDevice device;

    public BTPrinter(Handler handler) {
        this.handler = handler;
        this.adapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void testCP(int codepage, Charset charset) throws IOException {
        this.setCodepage(codepage);
        this.write(charset.toBytes(charset.getStr() + " " + codepage + "\n" + "ÁáČčĎďÉéĚěÍíŇňÓóŘřŠšŤťÚúŮůÝýŽž\n"));
    }

    void printSelfTest() throws IOException {
        this.write(new byte[]{0x1f, 0x11, 0x04});
    }

    private void setCodepage(int codepage) throws IOException {
        this.write(new byte[]{0x1B, 't', (byte) codepage});
    }

    private void write(String string) throws IOException {
        this.write(Settings.getCharset().toBytes(string));
    }

    private void write(byte[] bytes) throws IOException {
        if (this.outputStream == null) {
            throw new IOException();
        }

        this.outputStream.write(bytes);
        this.outputStream.flush();
    }

    BluetoothDevice getDevice() {
        return this.device;
    }

    @Override
    public void print(String text) throws IOException {
        this.write(text);
    }

    BluetoothDevice[] list() {
        if (this.isBTDisabled()) {
            return new BluetoothDevice[0];
        }
        return this.adapter.getBondedDevices().toArray(new BluetoothDevice[0]);
    }

    private boolean isBTDisabled() {
        if (this.adapter.isEnabled()) {
            return false;
        } else {
            this.handler.sendEmptyMessage(Messages.btNotEnabled.ordinal());
            return true;
        }
    }

    public void connect(String mac) throws IOException {
        if (this.isBTDisabled()) {
            return;
        }

        for (BluetoothDevice device : this.list()) {
            if (mac.equals(device.getAddress())) {
                this.connect(device);
                return;
            }
        }

        throw new IOException();
    }

    void connect(BluetoothDevice device) throws IOException {
        if (this.isBTDisabled()) {
            return;
        }

        // Get UUID
        UUID uuid;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            // Retrieve a UUID from the device
            ParcelUuid[] uuids = device.getUuids();
            if (uuids.length < 1) {
                throw new IOException();
            }
            uuid = uuids[0].getUuid();
        } else {
            // API less than 15 doesn't support retrieving a UUID from the device, try the base UUID
            uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        }

        // Set up a connection
        this.socket = device.createRfcommSocketToServiceRecord(uuid);
        this.socket.connect();
        this.outputStream = this.socket.getOutputStream();
        this.setCodepage(Settings.getCodepage());
        this.device = device;

        this.handler.sendEmptyMessage(Messages.btPrinterChanged.ordinal());
    }

    void disconnect() throws IOException {
        if (this.outputStream != null) {
            this.outputStream.close();
        }
        if (this.socket != null) {
            this.socket.close();
        }
        this.device = null;

        this.handler.sendEmptyMessage(Messages.btPrinterChanged.ordinal());
    }
}
