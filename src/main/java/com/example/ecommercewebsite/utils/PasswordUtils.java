package com.example.ecommercewebsite.utils;

import org.springframework.stereotype.Component;

import java.security.MessageDigest;

@Component
public class PasswordUtils {
    private PasswordUtils(){}

    public static String endCodeMD5(final String s){
        final var MD5 = "MD5";
        try {
            var digest = MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();

            var hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                StringBuilder h = new StringBuilder(Integer.toHexString(0xFF & aMessageDigest));
                while (h.length() < 2)
                    h.insert(0, "0");
                hexString.append(h);
            }
            return hexString.toString();
        } catch (Exception e){
            return null;
        }
    }
}
