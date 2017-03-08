package tomas_vycital.eet.android_app;

/**
 * Contains all possible VAT values
 */

public enum VAT {
    exempt(0, 0), basic(21, 1), reduced1(15, 2), reduced2(10, 3);

    private final int percentage;
    private final int id;

    VAT(int percentage, int id) {
        this.percentage = percentage;
        this.id = id;
    }

    public static VAT fromID(int id) {
        for (VAT vat : VAT.values()) {
            if (vat.id == id) {
                return vat;
            }
        }
        return null;
    }

    public int getPercentage() {
        return this.percentage;
    }

    public double get() {
        return this.percentage / 100.0;
    }

    public String toString() {
        return this.percentage + "%";
    }

    public int getID() {
        return this.id;
    }
}
