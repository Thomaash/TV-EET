package tomas_vycital.eet.android_app;

import android.support.annotation.Nullable;

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

    /**
     * Returns VAT enum value from ID
     *
     * @param id The same as returned by getID()
     * @return Corresponding VAT enum value
     */
    @Nullable
    public static VAT fromID(int id) {
        for (VAT vat : VAT.values()) {
            if (vat.id == id) {
                return vat;
            }
        }
        return null;
    }

    /**
     * @return Percents as int (3 for 3%, 89 for 89%…)
     */
    public int getPercentage() {
        return this.percentage;
    }

    /**
     * @return Percents as double (0.03 for 3%, 0.89 for 89%…)
     */
    public double get() {
        return this.percentage / 100.0;
    }

    /**
     * @return Percents as string (“3%”, “89%”…)
     */
    @Override
    public String toString() {
        return this.percentage + "%";
    }

    /**
     * @return ID that won't be changed (for JSON etc.)
     */
    public int getID() {
        return this.id;
    }

    /**
     * @return Percents as string padded with spaces to 3 characters (“ 3%”, “89%”…)
     */
    public String getPaddedPercentage() {
        return (this.percentage >= 10 ? "" : " ") + this.toString();
    }
}
