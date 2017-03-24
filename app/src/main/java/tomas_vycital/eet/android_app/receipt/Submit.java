package tomas_vycital.eet.android_app.receipt;

import android.os.Handler;

import tomas_vycital.eet.android_app.Messages;
import tomas_vycital.eet.android_app.settings.Settings;
import tomas_vycital.eet.lib.EET;

class Submit implements Runnable {
    private final Receipt receipt;
    private final Handler handler;

    Submit(Receipt receipt, Handler handler) {
        this.receipt = receipt;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            switch (Settings.getServer()) {
                case play:
                    EET.sendPlay(this.receipt.eetReceipt);
                    break;
                case prod:
                    EET.sendProd(this.receipt.eetReceipt);
                    break;
            }
            this.receipt.onSubmit();
            Receipts.addReceipt(this.receipt);
            this.handler.sendMessage(Messages.generateMessage(new Exception("Tržba byla úspěšně nahlášena")));
        } catch (Exception e) {
            this.handler.sendMessage(Messages.generateMessage(e));
        }
    }

    void start() {
        Thread thread = new Thread(this);
        thread.start();
    }
}