package tomas_vycital.eet.android_app.settings;

/**
 * Created by tom on 9.3.17.
 */
public enum Server {
    play(0), prod(1);

    private final int id;

    Server(int id) {
        this.id = id;
    }

    public static Server fromID(int id) {
        for (Server server : Server.values()) {
            if (server.id == id) {
                return server;
            }
        }
        return null;
    }

    public int getID() {
        return this.id;
    }
}
