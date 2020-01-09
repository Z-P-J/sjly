package com.zpj.downloader.util.content;

import android.content.SharedPreferences;

import java.util.Map;

/**
 * @author Z-P-J
 * */
public enum SPEnum {
    /**
     *
     * */

    defaults();

    SPEnum() {
//        sharedPreferences = ContextHelper.getAppContext().getSharedPreferences(spFile, Context.MODE_PRIVATE);
    }

    public <T> void put(String key, T value) {
        if (value == null) {
            return;
        }
        SharedPreferences.Editor editor = SPHelper.getPrefs().edit();
        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        }
        editor.apply();
    }

    public Object get(String key) {
        Map<String, ?> values = SPEnum.defaults.getAll();
        Object result = null;
        if (values != null && values.containsKey(key)) {
            result = values.get(key);
        }
        return result;
    }

    public String getString(String key) {
        return SPHelper.getPrefs().getString(key, "");
    }

    public String getString(String key, String defaultString) {
        return SPHelper.getPrefs().getString(key, defaultString);
    }

    public void putString(String key, String value) {
        SPHelper.getPrefs().edit().putString(key, value).apply();
    }

    public int getInt(String key) {
        return SPHelper.getPrefs().getInt(key, 0);
    }

    public int getInt(String key, int value) {
        return SPHelper.getPrefs().getInt(key, value);
    }

    public void putInt(String key, int value) {
        SPHelper.getPrefs().edit().putInt(key, value).apply();
    }

    public long getLong(String key, long defaultLong) {
        return SPHelper.getPrefs().getLong(key, defaultLong);
    }

    public void putLong(String key, long value) {
        SPHelper.getPrefs().edit().putLong(key, value).apply();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return SPHelper.getPrefs().getBoolean(key, defaultValue);
    }

    public void putBoolean(String key, boolean value) {
        SPHelper.getPrefs().edit().putBoolean(key, value).apply();
    }

    public void remove(String key) {
        SPHelper.getPrefs().edit().remove(key).apply();
    }

    public Map<String, ?> getAll() {
        return SPHelper.getPrefs().getAll();
    }

    public void removeAll() {
        SPHelper.getPrefs().edit().clear().apply();
    }

}
