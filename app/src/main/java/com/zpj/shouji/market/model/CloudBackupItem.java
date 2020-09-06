package com.zpj.shouji.market.model;

import com.zpj.http.parser.html.nodes.Element;
import com.zpj.shouji.market.utils.BeanUtils;
import com.zpj.shouji.market.utils.BeanUtils.Select;

public class CloudBackupItem {

    @Select(selector = "id")
    private String id;
    @Select(selector = "title")
    private String title;
    @Select(selector = "comment")
    private String comment;
    @Select(selector = "createdate")
    private String createDate;
    @Select(selector = "count")
    private String count;
    @Select(selector = "viewtype")
    private String viewType;

    public static CloudBackupItem from(Element element) {
        return BeanUtils.createBean(element, CloudBackupItem.class);
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getComment() {
        return comment;
    }

    public String getCreateDate() {
        return createDate;
    }

    public String getCount() {
        return count;
    }

    public String getViewType() {
        return viewType;
    }
}
