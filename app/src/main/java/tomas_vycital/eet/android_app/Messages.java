package tomas_vycital.eet.android_app;

import android.os.Message;

/**
 * Created by tom on 8.3.17.
 */

public enum Messages {
    exception, receiptChanged, btPrinterChanged, btNotEnabled;

    public static Message generateMessage(Exception e) {
        Message msg = new Message();
        msg.obj = e;
        msg.what = Messages.exception.ordinal();
        return msg;
    }
}
