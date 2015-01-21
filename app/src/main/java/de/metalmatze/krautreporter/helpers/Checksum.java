package de.metalmatze.krautreporter.helpers;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Checksum {

    public static String md5(String string) throws NoSuchAlgorithmException {

        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.update(string.getBytes());
        byte[] digest = messageDigest.digest();

        StringBuffer stringBuffer = new StringBuffer();
        for (byte b : digest) {
            stringBuffer.append(String.format("%02x", b & 0xff));
        }

        return stringBuffer.toString();
    }

}
