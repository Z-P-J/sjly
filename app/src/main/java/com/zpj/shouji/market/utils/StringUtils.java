package com.zpj.shouji.market.utils;

import android.text.TextUtils;
import android.util.Base64;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringUtils {

    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(string.getBytes());

            BigInteger integer = new BigInteger(1, md5.digest());
            return integer.toString(16);

//            byte[] bytes = md5.digest(string.getBytes());
//            StringBuilder result = new StringBuilder();
//            for (byte b : bytes) {
//                String temp = Integer.toHexString(b & 0xff);
//                if (temp.length() == 1) {
//                    temp = "0" + temp;
//                }
//                result.append(temp);
//            }
//            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String str2HexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            // sb.append(' ');
        }
        return sb.toString().trim();
    }

    public static String jiami(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(string.getBytes(StandardCharsets.UTF_8));
            return new String(com.zpj.shouji.market.utils.Base64.encode(md5.digest()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String base64Encode(String s) {
        if (TextUtils.isEmpty(s)) {
            return "";
        }
        return Base64.encodeToString(s.getBytes(), Base64.DEFAULT);
    }

    public static String base64Decode(String s) {
        if (TextUtils.isEmpty(s)) {
            return "";
        }
        return new String(Base64.decode(s.getBytes(), Base64.DEFAULT));
    }

}
