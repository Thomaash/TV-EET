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
    clearReceipt,
    /**
     * Refreshes available items list
     */
    itemsChanged;

    /**
     * Generates new message to be shown to the user out of an exception
     *
     * @param e The exception containing the text to be shown to the user
     * @return The message to be send to a handler
     */
    public static Message generateMessage(Exception e) {
        Message msg = new Message();
        msg.obj = e;
        msg.what = Messages.exception.ordinal();
        return msg;
    }

    /**
     * Generates new message to be shown to the user
     *
     * @param str The text to be shown to the user
     * @return The message to be send to a handler
     */
    public static Message generateMessage(String str) {
        return Messages.generateMessage(new Exception(str));
    }
}
