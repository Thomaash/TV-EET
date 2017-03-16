package tomas_vycital.eet.android_app.printer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.ParcelUuid;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import tomas_vycital.eet.android_app.settings.Charset;
import tomas_vycital.eet.android_app.settings.Settings;

/**
 * Created by tom on 4.3.17.
 */

public class BTPrinter implements Printer {
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private BluetoothDevice device;

    public void testCP() throws IOException {
        this.testCP(Settings.getCodepage(), Settings.getCharset());
    }

    public void testCP(int codepage, Charset charset) throws IOException {
        this.setCodepage(codepage);
        this.write(charset.toBytes(charset.getStr() + " " + codepage + "\n" + "ÁáČčĎďÉéĚěÍíŇňÓóŘřŠšŤťÚúŮůÝýŽž\n"));
    }

    public void printSelfTest() throws IOException {
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

    public BluetoothDevice getDevice() {
        return this.device;
    }

    @Override
    public void print(String text) throws IOException {
        this.write(text);
    }

    public BluetoothDevice[] list() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return mBluetoothAdapter.getBondedDevices().toArray(new BluetoothDevice[0]);
    }

    public void connect(String mac) throws IOException {
        for (BluetoothDevice device : this.list()) {
            if (mac.equals(device.getAddress())) {
                this.connect(device);
                return;
            }
        }

        throw new IOException();
    }

    public void connect(BluetoothDevice device) throws IOException {
        // Get UUID
        ParcelUuid[] uuids = device.getUuids();
        if (uuids.length < 1) {
            throw new IOException();
        }
        UUID uuid = uuids[0].getUuid();

        // Set up a connection
        this.socket = device.createRfcommSocketToServiceRecord(uuid);
        this.socket.connect();
        this.outputStream = this.socket.getOutputStream();
        this.setCodepage(Settings.getCodepage());
        this.device = device;
    }

    public void disconnect() throws IOException {
        if (this.outputStream != null) {
            this.outputStream.close();
        }
        if (this.socket != null) {
            this.socket.close();
        }
        this.device = null;
    }
}
