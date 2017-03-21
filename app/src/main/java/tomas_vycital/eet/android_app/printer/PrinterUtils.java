package tomas_vycital.eet.android_app.printer;

import tomas_vycital.eet.android_app.settings.Settings;

/**
 * Printer type independent utils
 */

public class PrinterUtils {
    private final static int minSpace = 2;

    private static String getSeparator() {
        return new String(new char[Settings.getReceiptWidth()]).replace("\0", "-");
    }

    public static String getSeparatorNl() {
        return "\n" + PrinterUtils.getSeparator() + "\n\n";
    }

    public static String align(String start, String end) {
        int space = Settings.getReceiptWidth() - start.length() - end.length();
        return start + new String(new char[space < PrinterUtils.minSpace ? PrinterUtils.minSpace : space]).replace("\0", " ") + end;
    }
}
