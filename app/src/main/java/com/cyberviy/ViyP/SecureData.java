package com.cyberviy.ViyP;

import android.util.Log;

import java.security.SecureRandom;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class SecureData {
    private static String PREF_NAME = "com.cyberviy.ViyP.SecureData";


//    ENCRYPT DATA
//    String string = "My sensitive string that I want to encrypt";
//    byte[] bytes = string.getBytes();
//    HashMap<String, byte[]> map = encryptBytes(bytes, "UserSuppliedPassword");

//    DECRYPT DATA
//    byte[] decrypted = decryptData(map, "UserSuppliedPassword");
//    if (decrypted != null) {
//        String decryptedString = new String(decrypted);
//        Log.e("MYAPP", "Decrypted String is : " + decryptedString);
//    }

    public static ArrayList<byte[]> encryptBytes(byte[] plainTextBytes, String passwordString) {
        ArrayList<byte[]> map = new ArrayList<byte[]>();

        try {
            //Random salt for next step
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[256];
            random.nextBytes(salt);

            //PBKDF2 - derive the key from the password, don't use passwords directly
            char[] passwordChar = passwordString.toCharArray(); //Turn password into char[] array
            PBEKeySpec pbKeySpec = new PBEKeySpec(passwordChar, salt, 1324, 256); //1324 iterations
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] keyBytes = secretKeyFactory.generateSecret(pbKeySpec).getEncoded();
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

            //Create initialization vector for AES
            SecureRandom ivRandom = new SecureRandom(); //not caching previous seeded instance of SecureRandom
            byte[] iv = new byte[16];
            ivRandom.nextBytes(iv);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);

            //Encrypt
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = cipher.doFinal(plainTextBytes);

            map.add(0, salt);
            map.add(1, iv);
            map.add(2, encrypted);
        } catch (Exception e) {
            Log.e(PREF_NAME, "encryption exception", e);
        }

        return map;
    }

    public static byte[] decryptData(String saltp, String ivp, String encp, String passwordString) {
        byte[] decrypted = null;
        try {
            byte[] salt = saltp.getBytes();
            byte[] iv = ivp.getBytes();
            byte[] encrypted = encp.getBytes();

            //regenerate key from password
            char[] passwordChar = passwordString.toCharArray();
            PBEKeySpec pbKeySpec = new PBEKeySpec(passwordChar, salt, 1324, 256);
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] keyBytes = secretKeyFactory.generateSecret(pbKeySpec).getEncoded();
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

            //Decrypt
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            decrypted = cipher.doFinal(encrypted);
        } catch (Exception e) {
            Log.e(PREF_NAME, "decryption exception", e);
        }

        return decrypted;
    }
}
