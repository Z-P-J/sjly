package com.zpj.shouji.market.model;

import android.support.annotation.Keep;

import com.zpj.http.parser.html.nodes.Element;

public class WallpaperTag {

    private String id;
    private String name;

    public static WallpaperTag create(Element element) {
        WallpaperTag tag = new WallpaperTag();
        tag.id = element.selectFirst("id").text();
        tag.name = element.selectFirst("name").text();
        return tag;
    }

    public static WallpaperTag create(String id, String name) {
        WallpaperTag tag = new WallpaperTag();
        tag.id = id;
        tag.name = name;
        return tag;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
