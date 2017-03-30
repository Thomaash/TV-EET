package tomas_vycital.eet.android_app.settings;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

class IVE {
    /**
     * Initialization vector, may be null
     */
    final byte[] iv;
    /**
     * Bytes of encrypted text
     */
    final byte[] encrypted;

    IVE(byte[] iv, byte[] encrypted) {
        this.iv = iv;
        this.encrypted = encrypted;
    }

    IVE(JSONObject json) throws JSONException {
        this.iv = json.has("iv") ? Base64.decode(json.getString("iv"), Base64.DEFAULT) : null;
        this.encrypted = Base64.decode(json.getString("encrypted"), Base64.DEFAULT);
    }

    JSONObject toJSON() throws JSONException {
        return (new JSONObject())
                .put("iv", this.iv == null ? null : Base64.encodeToString(this.iv, Base64.DEFAULT))
                .put("encrypted", Base64.encodeToString(this.encrypted, Base64.DEFAULT))
                ;
    }
}
