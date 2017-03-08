package tomas_vycital.eet.android_app.printer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.widget.TextView;

/**
 * Created by tom on 18.2.17.
 */

public class AlertPrinter {
    private final Context context;

    public AlertPrinter(Context context) {
        this.context = context;
    }

    public void print(String title, String text) {
        AlertDialog alert = new AlertDialog.Builder(this.context).create();
        alert.setTitle(title);
        alert.setMessage(text);
        alert.setButton(AlertDialog.BUTTON_NEUTRAL, "Zavřít", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();

        // Set monospace font (alert has to be shown)
        TextView messageView = (TextView) alert.findViewById(android.R.id.message);
        messageView.setTypeface(Typeface.MONOSPACE);
    }

}
