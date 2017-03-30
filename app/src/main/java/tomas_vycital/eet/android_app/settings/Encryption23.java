package tomas_vycital.eet.android_app.settings;

import android.annotation.TargetApi;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;

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

/**
 * Android 23 encryption (AES)
 */
@TargetApi(Build.VERSION_CODES.M)
class Encryption23 implements Encryption {
    private static final String transformation = "AES/GCM/NoPadding";
    private static final String type = "AndroidKeyStore";
    private static final String alias = "settings";
    private KeyStore keyStore;

    Encryption23() {
        try {
            initKeyStore();
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IVE encryptText(String textToEncrypt) throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException, InvalidAlgorithmParameterException, SignatureException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(Encryption23.transformation);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
        return new IVE(cipher.getIV(), cipher.doFinal(textToEncrypt.getBytes("UTF-8")));
    }

    @Override
    public String decryptData(IVE ive) throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance(Encryption23.transformation);
        GCMParameterSpec spec = new GCMParameterSpec(128, ive.iv);
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec);

        return new String(cipher.doFinal(ive.encrypted), "UTF-8");
    }

    @NonNull
    private SecretKey getSecretKey() throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException, UnrecoverableEntryException, KeyStoreException {
        if (this.keyStore.containsAlias(Encryption23.alias)) {
            return ((KeyStore.SecretKeyEntry) this.keyStore.getEntry(Encryption23.alias, null)).getSecretKey();
        } else {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, Encryption23.type);

            keyGenerator.init(
                    new KeyGenParameterSpec.Builder(Encryption23.alias, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                            .build()
            );

            return keyGenerator.generateKey();
        }
    }

    private void initKeyStore() throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
        this.keyStore = KeyStore.getInstance(Encryption23.type);
        this.keyStore.load(null);
    }

}