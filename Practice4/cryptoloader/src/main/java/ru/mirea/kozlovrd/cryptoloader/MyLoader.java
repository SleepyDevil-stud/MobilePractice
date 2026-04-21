package ru.mirea.kozlovrd.cryptoloader;

import android.content.Context;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MyLoader extends AsyncTaskLoader<String> {
    private static final String TAG = "MyLoader";
    private String encryptedMessageBase64;
    private String keyBase64;

    public static final String ARG_ENCRYPTED_MSG = "encrypted_msg";
    public static final String ARG_KEY = "key";

    public MyLoader(@NonNull Context context, Bundle args) {
        super(context);
        if (args != null) {
            encryptedMessageBase64 = args.getString(ARG_ENCRYPTED_MSG);
            keyBase64 = args.getString(ARG_KEY);
        }
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Nullable
    @Override
    public String loadInBackground() {

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        try {
            if (encryptedMessageBase64 != null && keyBase64 != null) {
                byte[] encryptedBytes = Base64.decode(encryptedMessageBase64, Base64.DEFAULT);
                SecretKey secretKey = stringToSecretKey(keyBase64);
                String decryptedMessage = decryptMsg(encryptedBytes, secretKey);
                Log.d(TAG, "Расшифрованное сообщение: " + decryptedMessage);
                return decryptedMessage;
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при дешифровании", e);
        }
        return "Ошибка дешифрования";
    }

    public static SecretKey generateKey() {
        try {
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed("any data used as random seed".getBytes());
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(256, sr);
            return new SecretKeySpec((kg.generateKey()).getEncoded(), "AES");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] encryptMsg(String message, SecretKey secret) {
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            return cipher.doFinal(message.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static String decryptMsg(byte[] encryptedMessage, SecretKey secret) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secret);
            byte[] decryptedBytes = cipher.doFinal(encryptedMessage);
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static String secretKeyToString(SecretKey secretKey) {
        return Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT);
    }

    public static SecretKey stringToSecretKey(String keyString) {
        byte[] decodedKey = Base64.decode(keyString, Base64.DEFAULT);
        return new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
    }
}