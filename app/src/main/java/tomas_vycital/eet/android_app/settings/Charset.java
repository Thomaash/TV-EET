package tomas_vycital.eet.android_app.settings;

/**
 * Created by tom on 9.3.17.
 */
public enum Charset {
    ascii("ASCII"), utf8("UTF-8"), iso88592("ISO-8859-2"), windows1250("Windows-1250"), cp852("CP-852");

    private final String str;
    private final java.nio.charset.Charset charset;

    Charset(String str) {
        this.str = str;
        this.charset = java.nio.charset.Charset.forName(this.str);
    }

    public static Charset fromStr(String str) {
        for (Charset charset : Charset.values()) {
            if (charset.str.equals(str)) {
                return charset;
            }
        }
        return null;
    }

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

    public String getStr() {
        return this.str;
    }

    public byte[] toBytes(String string) {
        return this.charset.encode(this == Charset.ascii ? Charset.toASCII(string) : string).array();
    }
}
