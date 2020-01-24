package com.zpj.shouji.market.model.article;

import com.zpj.http.parser.html.nodes.Element;

public class ArticleInfo {

    private String image;
    private String title;
    private String url;

    public static ArticleInfo from(Element element) {
        ArticleInfo info = new ArticleInfo();
        Element a = element.select("a").get(1);
        info.url = a.attr("href");
        info.title = a.attr("title");
        info.image = a.selectFirst("img").attr("src");
        return info;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return "ArticleInfo{" +
                "image='" + image + '\'' +
                ", title='" + title + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

}
