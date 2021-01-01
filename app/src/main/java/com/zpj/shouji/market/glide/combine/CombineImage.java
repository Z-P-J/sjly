package com.zpj.shouji.market.glide.combine;

import android.text.TextUtils;

import com.zpj.utils.CipherUtils;
import com.zpj.utils.ContextUtils;
import com.zpj.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CombineImage {

    private final List<String> urls;
    private String key;
    private int size;
    private int gap;
    private int gapColor;
    private ICombineManager combineManager;

    private CombineImage(List<String> urls) {
        this.urls = urls;
    }

    public static CombineImage get() {
        return new CombineImage(new ArrayList<>());
    }

    public static CombineImage get(List<String> urls) {
        return new CombineImage(urls);
    }

    public static CombineImage get(String ... urls) {
        return new CombineImage(Arrays.asList(urls));
    }

    public CombineImage addUrl(String url) {
        this.urls.add(url);
        return this;
    }

    public CombineImage setSize(int size) {
        this.size = ScreenUtils.dp2pxInt(ContextUtils.getApplicationContext(), size);
        return this;
    }

    public CombineImage setGap(int gap) {
        this.gap = ScreenUtils.dp2pxInt(ContextUtils.getApplicationContext(), gap);
        return this;
    }

    public CombineImage setGapColor(int gapColor) {
        this.gapColor = gapColor;
        return this;
    }

    public CombineImage setCombineManager(ICombineManager combineManager) {
        this.combineManager = combineManager;
        return this;
    }

    public List<String> getUrls() {
        return urls;
    }

    public int getSize() {
        return size;
    }

    public int getGap() {
        return gap;
    }

    public int getGapColor() {
        return gapColor;
    }

    public ICombineManager getCombineManager() {
        return combineManager;
    }

    public String getKey() {
        if (TextUtils.isEmpty(key)) {
            StringBuilder builder = new StringBuilder("");
            for (String url : urls) {
                builder.append(url.trim());
            }
            key = CipherUtils.md5(builder.toString());
        }
        return key;
    }

}
