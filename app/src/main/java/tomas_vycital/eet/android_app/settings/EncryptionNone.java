package tomas_vycital.eet.android_app.settings;

import java.io.UnsupportedEncodingException;

/**
 * Fallback “encryption” class to retain the ability to save passwords on older Androids (root is necessary to read settings so it is not completely unsecured)
 */
class EncryptionNone implements Encryption {
    @Override
    public IVE encryptText(String textToEncrypt) throws UnsupportedEncodingException {
        return new IVE(null, textToEncrypt.getBytes("UTF-8"));
    }

    @Override
    public String decryptData(IVE ive) {
        return new String(ive.encrypted);
    }
}