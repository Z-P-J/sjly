package com.zpj.shouji.market.utils;

import android.graphics.Bitmap;

import com.zpj.shouji.market.bean.AppItem;

public class TransportUtil {

    private static final TransportUtil mInstance = new TransportUtil();

    private AppItem appItem;

    private Bitmap iconBitmap;

    private TransportUtil() {
    }

    public synchronized static TransportUtil getInstance() {
        return mInstance;
    }

    public void setAppItem(AppItem appItem) {
        this.appItem = appItem;
    }

    public void setIconBitmap(Bitmap iconBitmap) {
        this.iconBitmap = iconBitmap;
    }

    public AppItem getAppItem() {
        return appItem;
    }

    public Bitmap getIconBitmap() {
        return iconBitmap;
    }
}
