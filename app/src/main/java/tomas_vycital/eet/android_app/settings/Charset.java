package tomas_vycital.eet.android_app.settings;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Allows charset conversion (string to byte[]) and diacritics removal, if the selected charset doesn't support all the letters (ASCII)
 */
public enum Charset {
    ascii("ASCII"), utf8("UTF-8"), iso88592("ISO-8859-2"), windows1250("Windows-1250"), cp852("CP-852");

    private final String str;
    private final java.nio.charset.Charset charset;

    Charset(String str) {
        this.str = str;
        this.charset = java.nio.charset.Charset.forName(this.str);
    }

    /**
     * Returns Charset instance based on the name of the charset
     *
     * @param str The charset name, the same as returned by Charset.getStr()
     * @return Charset corresponding to the name or null if such charset doesn't exist
     */
    @Nullable
    public static Charset fromStr(String str) {
        for (Charset charset : Charset.values()) {
            if (charset.str.equals(str)) {
                return charset;
            }
        }
        return null;
    }

    /**
     * Removes Czech diacritics and replaces all other non-ASCII characters with space (ASCII 32)
     *
     * @param text The text to be converted to ASCII
     * @return The text consisted only of ASCII characters (still standard Java string)
     */
    private static String toASCII(String text) {
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

    /**
     * @return The name of this encoding
     */
    public String getStr() {
        return this.str;
    }

    /**
     * Converts supplied text to byte[] in specified encoding
     *
     * @param string The text to be converted
     * @return The encoded bytes
     */
    @NonNull
    public byte[] toBytes(String string) {
        return this.charset.encode(this == Charset.ascii ? Charset.toASCII(string) : string).array();
    }
}
