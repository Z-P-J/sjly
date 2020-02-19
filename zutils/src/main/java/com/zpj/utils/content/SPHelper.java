package com.zpj.utils.content;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * @author Z-P-J
 * */
public class SPHelper {

    private static SPEnum sp;

    private static SharedPreferences sharedPreferences;

    private SPHelper() {

    }

    public static void init(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sp = SPEnum.defaults;
    }

    static SharedPreferences getPrefs() {
        return sharedPreferences;
    }

    public static String getString(String key) {
        return sp.getString(key);
    }

    public static String getString(String key, String defaultString) {
        return sp.getString(key, defaultString);
    }

    public static void putString(String key, String value) {
        sp.putString(key, value);
    }

    public static int getInt(String key) {
        return sp.getInt(key);
    }

    public static int getInt(String key, int value) {
        return sp.getInt(key, value);
    }

    public static void putInt(String key, int value) {
        sp.putInt(key, value);
    }

    public static long getLong(String key) {
        return sp.getLong(key, 0);
    }

    public static long getLong(String key, long defaultLong) {
        return sp.getLong(key, defaultLong);
    }

    public static void putLong(String key, long value) {
        sp.putLong(key, value);
    }


    public static boolean getBoolean(String key, boolean defaultValue) {
        return sp.getBoolean(key, defaultValue);
    }

    public static boolean getBoolean(String key) {
        return sp.getBoolean(key, false);
    }

    public static void putBoolean(String key, boolean value) {
        sp.putBoolean(key, value);
    }

    public static void remove(String key) {
        sp.remove(key);
    }

    public static boolean contains(String key) {
        return sharedPreferences.contains(key);
    }

}
