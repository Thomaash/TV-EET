package tomas_vycital.eet.android_app;

import android.os.Handler;
import android.os.Message;

class MessageHandler extends Handler {
    private final MainActivity ma;

    MessageHandler(MainActivity ma) {
        super();
        this.ma = ma;
    }

    @Override
    public void handleMessage(Message msg) {
        this.ma.handleMessage(msg);
    }
}
