package tomas_vycital.eet.android_app.error;

public class UnsupportedImportItemsVersion extends Throwable {
    private final int version;

    public UnsupportedImportItemsVersion(int version) {
        super();
        this.version = version;
    }

    public int getVersion() {
        return this.version;
    }
}
