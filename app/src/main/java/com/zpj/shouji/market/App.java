package com.zpj.shouji.market;

import android.app.Application;
import android.graphics.BitmapFactory;

import com.bumptech.glide.request.target.ViewTarget;
import com.felix.atoast.library.AToast;
import com.zpj.downloader.ZDownloader;
import com.zpj.shouji.market.utils.ExecutorHelper;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class App extends Application {

//    private static final String HASH_ALGORITHM = "MD5";
//    private static final int RADIX = 10 + 26;
//
//    private static String IMAGE_CACHE_PATH;

    @Override
    public void onCreate() {
        super.onCreate();
//        IMAGE_CACHE_PATH = getExternalCacheDir().getPath();
        AToast.onInit(this);
        ZDownloader.init(this);
        ExecutorHelper.init();
        ViewTarget.setTagId(R.id.glide_tag_id);
    }

//    public static String generate(String imageUri) {
//        byte[] md5 = getMD5(imageUri.getBytes());
//        BigInteger bi = new BigInteger(md5).abs();
//        if (imageUri.toLowerCase().endsWith(".gif")) {
//            return bi.toString(RADIX) + ".itgif";
//        }
//        return bi.toString(RADIX) + ".it";
//    }

//    private static byte[] getMD5(byte[] data) {
//        byte[] hash = null;
//        try {
//            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
//            digest.update(data);
//            hash = digest.digest();
//        } catch (NoSuchAlgorithmException e) {
//        }
//        return hash;
//    }

//    public static String getImageCachePath() {
//        return IMAGE_CACHE_PATH;
//    }

//    public static String getCachedPath(String url) {
//        String key = generate(url);
//        String destUrl = getImageCachePath() + "/" + key;
//        return destUrl;
//    }
//
//    public static int getMaxSizeOfBitMap(String path) {
//        BitmapFactory.Options op = new BitmapFactory.Options();
//        op.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(path, op);
//        return Math.max(op.outWidth, op.outHeight);
//    }

}
