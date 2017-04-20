package tomas_vycital.eet.android_app;

import android.support.annotation.Nullable;

import tomas_vycital.eet.android_app.settings.Settings;

/**
 * Contains all possible VAT values
 */
public enum VAT {
    exempt(0), basic(1), reduced1(2), reduced2(3);

    private final int id;

    VAT(int id) {
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
        return Settings.getVAT(this);
    }

    /**
     * @return Percents as double (0.03 for 3%, 0.89 for 89%…)
     */
    public double get() {
        return this.getPercentage() / 100.0;
    }

    /**
     * @return Percents as string (“3%”, “89%”…)
     */
    @Override
    public String toString() {
        return this.getPercentage() + "%";
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
        return (this.getPercentage() >= 10 ? "" : " ") + this.toString();
    }
}
