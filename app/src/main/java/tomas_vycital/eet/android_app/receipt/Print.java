package tomas_vycital.eet.android_app.receipt;

import android.os.Handler;

import tomas_vycital.eet.android_app.Messages;
import tomas_vycital.eet.android_app.printer.Printer;

/**
 * Created by tom on 2.3.17.
 */
class Print implements Runnable {
    private final Receipt receipt;
    private final Handler handler;
    private final Printer printer;

    Print(Receipt receipt, Handler handler, Printer printer) {
        this.receipt = receipt;
        this.handler = handler;
        this.printer = printer;
    }

    @Override
    public void run() {
        try {
            this.printer.print(this.receipt.getReceiptStr());
        } catch (Exception e) {
            this.handler.sendMessage(Messages.generateMessage(e));
        }
    }

    void start() {
        Thread thread = new Thread(this);
        thread.start();
    }
}