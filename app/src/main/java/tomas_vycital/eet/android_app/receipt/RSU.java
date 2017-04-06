package tomas_vycital.eet.android_app.receipt;

import tomas_vycital.eet.android_app.settings.Settings;

/**
 * Receipt Static Utils
 */
class RSU {
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
    static String getSeparatorNl() {
        return "\n" + RSU.getSeparator() + "\n\n";
    }

    /**
     * Aligns text using the physical receipt width as saved in the settings but always keeps all the texts, even if it exceeds the limit
     *
     * @param start The text at the left side of the receipt
     * @param end   The text at the right side of the receipt
     * @return Aligned text or empty string if any argument is empty
     */
    static String align(String start, String end) {
        if (RSU.isEmpty(start) || RSU.isEmpty(end)) {
            return "";
        }

        int space = Settings.getReceiptWidth() - start.length() - end.length();
        return start + new String(new char[space < RSU.minSpace ? RSU.minSpace : space]).replace("\0", " ") + end + "\n";
    }

    /**
     * Constructs “name: value\n” line or empty string if the value is null
     *
     * @param name  The name of the value
     * @param value The value to be printed
     * @return The constructed line or empty string
     */
    static String nvl(String name, String value) {
        if (RSU.isEmpty(value)) {
            return "";
        }

        return name + ": " + value + "\n";
    }

    /**
     * Constructs “value\n” line or empty string if the value is null
     *
     * @param value The value to be printed
     * @return The constructed line or empty string
     */
    static String nvl(String value) {
        if (RSU.isEmpty(value)) {
            return "";
        }

        return value + "\n";
    }

    /**
     * @param value String value to be tested
     * @return True for null or empty string
     */
    static boolean isEmpty(String value) {
        return value == null || "".equals(value);
    }
}
