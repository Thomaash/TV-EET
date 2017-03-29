package tomas_vycital.eet.android_app.printer;

import tomas_vycital.eet.android_app.settings.Settings;

/**
 * Printer type independent utils
 */
public class PrinterUtils {
    private final static int minSpace = 2;

    /**
     * @return Separator without ending the line (as long as the receipt is wide)
     */
    private static String getSeparator() {
        return new String(new char[Settings.getReceiptWidth()]).replace("\0", "-");
    }

    /**
     * @return Separator (as long as the receipt is wide) with empty line before and after
     */
    public static String getSeparatorNl() {
        return "\n" + PrinterUtils.getSeparator() + "\n\n";
    }

    /**
     * Aligns text using the physical receipt width as saved in the settings
     *
     * @param start The text at the left side of the receipt
     * @param end   The text at the right side of the receipt
     * @return Aligned text, may be longer than the receipt is wide if the supplied texts do not fit in the available space, but they will always be divided by some space
     */
    public static String align(String start, String end) {
        int space = Settings.getReceiptWidth() - start.length() - end.length();
        return start + new String(new char[space < PrinterUtils.minSpace ? PrinterUtils.minSpace : space]).replace("\0", " ") + end;
    }
}
