package tomas_vycital.eet.android_app.printer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by tom on 4.3.17.
 */

public class BTPrinter implements Printer {
    private BluetoothSocket socket;
    private OutputStream outputStream;

    @Override
    public void print(String text) throws IOException {
        if (this.outputStream == null) {
            throw new IOException();
        }
        this.outputStream.write(PrinterUtils.toASCII(text).getBytes());
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
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); // Base UUID
        this.socket = device.createRfcommSocketToServiceRecord(uuid);
        this.socket.connect();
        this.outputStream = this.socket.getOutputStream();
    }

    public void disconnect() throws IOException {
        this.outputStream.close();
        this.socket.close();
    }

}
