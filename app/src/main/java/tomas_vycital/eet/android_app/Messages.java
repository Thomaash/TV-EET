package tomas_vycital.eet.android_app;

import android.os.Message;

/**
 * Messages that can be passed to the main activity
 */
public enum Messages {
    /**
     * The exception's detail message will be shown to the user
     */
    exception,
    /**
     * Refreshes all fragments, views, etc. containing receipt info
     */
    receiptChanged,
    /**
     * Refreshes printer related fragments, views…
     */
    btPrinterChanged,
    /**
     * Informs the user that Bluetooth has to be enabled before repeating the operation
     */
    btNotEnabled,
    /**
     * Removes all items and other non-permanent data from the receipt
     */
    clearReceipt;

    /**
     * Generates new message out of an exception
     *
     * @param e The exception containing text to be shown to the user
     * @return The message to be send to a handler
     */
    public static Message generateMessage(Exception e) {
        Message msg = new Message();
        msg.obj = e;
        msg.what = Messages.exception.ordinal();
        return msg;
    }
}
