package tomas_vycital.eet.android_app.printer;

import tomas_vycital.eet.android_app.Settings;

/**
 * Printer type independent utils
 */

public class PrinterUtils {
    private final static int minSpace = 2;

    public static String getSeparator() {
        return new String(new char[Settings.getReceiptWidth()]).replace("\0", "-");
    }

    public static String getSeparatorNl() {
        return "\n" + PrinterUtils.getSeparator() + "\n\n";
    }

    public static String align(String start, String end) {
        int space = Settings.getReceiptWidth() - start.length() - end.length();
        return start + new String(new char[space < PrinterUtils.minSpace ? PrinterUtils.minSpace : space]).replace("\0", " ") + end;
    }

    public static String toASCII(String text) {
        return text
                .replace("Á", "A")
                .replace("É", "E")
                .replace("Í", "I")
                .replace("Ó", "O")
                .replace("Ú", "U")
                .replace("Ý", "Y")
                .replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ú", "u")
                .replace("ý", "y")
                .replace("Č", "C")
                .replace("č", "c")
                .replace("Ď", "D")
                .replace("ď", "d")
                .replace("Ě", "E")
                .replace("ě", "e")
                .replace("Ň", "N")
                .replace("ň", "n")
                .replace("Ř", "R")
                .replace("ř", "r")
                .replace("Š", "S")
                .replace("š", "s")
                .replace("Ť", "T")
                .replace("ť", "t")
                .replace("Ů", "U")
                .replace("ů", "u")
                .replace("Ž", "Z")
                .replace("ž", "z")
                .replaceAll("[^\\x00-\\x7F]", " ")
                ;
    }
}
