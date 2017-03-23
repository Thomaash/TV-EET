package tomas_vycital.eet.android_app;

import android.os.Message;

public enum Messages {
    exception, receiptChanged, btPrinterChanged, btNotEnabled,
    clearReceipt;

    public static Message generateMessage(Exception e) {
        Message msg = new Message();
        msg.obj = e;
        msg.what = Messages.exception.ordinal();
        return msg;
    }
}
