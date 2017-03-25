package tomas_vycital.eet.android_app.settings;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

class Encryption {
    private static final String transformation = "AES/GCM/NoPadding";
    private static final String type = "AndroidKeyStore";
    private static final String alias = "settings";
    private KeyStore keyStore;

    Encryption() {
        try {
            initKeyStore();
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }

    IVE encryptText(String textToEncrypt) throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException, InvalidAlgorithmParameterException, SignatureException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(Encryption.transformation);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
        return new IVE(cipher.getIV(), cipher.doFinal(textToEncrypt.getBytes("UTF-8")));
    }

    String decryptData(IVE ive) throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(Encryption.transformation);
        GCMParameterSpec spec = new GCMParameterSpec(128, ive.iv);
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec);

        return new String(cipher.doFinal(ive.encrypted), "UTF-8");
    }

    @NonNull
    private SecretKey getSecretKey() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, UnrecoverableEntryException, KeyStoreException {
        if (this.keyStore.containsAlias(Encryption.alias)) {
            return ((KeyStore.SecretKeyEntry) this.keyStore.getEntry(Encryption.alias, null)).getSecretKey();
        } else {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, Encryption.type);

            keyGenerator.init(
                    new KeyGenParameterSpec.Builder(Encryption.alias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                            .build()
            );

            return keyGenerator.generateKey();
        }
    }

    private void initKeyStore() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        this.keyStore = KeyStore.getInstance(Encryption.type);
        this.keyStore.load(null);
    }

    static class IVE {
        final byte[] iv;
        final byte[] encrypted;

        IVE(byte[] iv, byte[] encrypted) {
            this.iv = iv;
            this.encrypted = encrypted;
        }

        IVE(JSONObject json) throws JSONException {
            this.iv = Base64.decode(json.getString("iv"), Base64.DEFAULT);
            this.encrypted = Base64.decode(json.getString("encrypted"), Base64.DEFAULT);
        }

        JSONObject toJSON() throws JSONException {
            return (new JSONObject())
                    .put("iv", Base64.encodeToString(this.iv, Base64.DEFAULT))
                    .put("encrypted", Base64.encodeToString(this.encrypted, Base64.DEFAULT))
                    ;
        }
    }
}