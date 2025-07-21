package com.dev.util;

import java.security.MessageDigest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuthUtils {
    private static final String TAG = AuthUtils.class.getSimpleName();
    private static final Logger logger = LogManager.getLogger(TAG);
    private static MessageDigest messageDigest;

    private static MessageDigest getMessageDigest(){
        try{
            messageDigest = MessageDigest.getInstance("SHA-256");
            return messageDigest;
        } catch (Exception e) {
            logger.error("{}: Failed to get MessageDigest instance", TAG, e);
            throw new RuntimeException("Failed to get MessageDigest instance", e);
        }
    }

    public static String hashedPassword(String password){
        if(messageDigest == null) {
            messageDigest = getMessageDigest();
        }
        byte[] hashedBytes = messageDigest.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashedBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
