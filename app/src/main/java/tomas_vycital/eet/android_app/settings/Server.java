package tomas_vycital.eet.android_app.settings;

import android.support.annotation.Nullable;

/**
 * Converts int to enum and back to save the server option in the settings
 */
public enum Server {
    play(0), prod(1);

    private final int id;

    Server(int id) {
        this.id = id;
    }

    /**
     * Returns an enum based on the ID (same as returned by getID())
     *
     * @param id The ID
     * @return The enum
     */
    @Nullable
    public static Server fromID(int id) {
        for (Server server : Server.values()) {
            if (server.id == id) {
                return server;
            }
        }
        return null;
    }

    /**
     * ID that can be saved (doesn't change)
     *
     * @return The ID
     */
    public int getID() {
        return this.id;
    }
}
