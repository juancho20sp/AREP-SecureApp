package edu.escuelaing.arep.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordManager {
    /**
     * Hash the password using SHA-256
     * @param password
     * @return
     */
    public static String hashPassword(String password) {
        String hashedPassword = null;

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(password.getBytes(StandardCharsets.UTF_8));

            byte[] passwordBytes = messageDigest.digest();

            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i < passwordBytes.length; i++){
                stringBuilder.append(Integer.toString((passwordBytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            hashedPassword = stringBuilder.toString();
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }

        return hashedPassword;
    }
}
