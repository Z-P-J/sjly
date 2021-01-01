package com.zpj.shouji.market.model;

import android.text.TextUtils;

import com.zpj.http.parser.html.nodes.Element;

import java.util.ArrayList;
import java.util.List;

public class ClassificationItem {

    private String icon;

    private String title;

    private List<SubItem> subItemList = new ArrayList<>();

    private String tags;

    public static ClassificationItem create(Element element) {
        ClassificationItem item = new ClassificationItem();
        item.icon = element.selectFirst("icon").text();
        item.title = element.selectFirst("title").text();
        item.tags = "";
        for (Element sub : element.selectFirst("subs").select("sub")) {
            SubItem subItem = new SubItem();
            subItem.title = sub.selectFirst("stitle").text();
            subItem.url = sub.selectFirst("surl").text();
            item.subItemList.add(subItem);
            if (!TextUtils.isEmpty(item.tags)) {
                item.tags += " ";
            }
            item.tags += String.format("[%s](%s)", subItem.title, subItem.url);
        }
        return item;
    }

    public String getIcon() {
        return icon;
    }

    public String getTitle() {
        return title;
    }

    public List<SubItem> getSubItemList() {
        return subItemList;
    }

    public String getTags() {
        return tags;
    }

    public static class SubItem {
        private String title;
        private String url;

        public String getTitle() {
            return title;
        }

        public String getUrl() {
            return url;
        }
    }

}
