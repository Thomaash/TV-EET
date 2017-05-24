package tomas_vycital.eet.android_app.error;

/**
 * Thrown for unusable input values
 */
public class BadUserInput extends Exception {
    public BadUserInput(String str) {
        super(str);
    }
}
