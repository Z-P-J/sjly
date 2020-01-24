package com.zpj.shouji.market.model.article;

import android.util.Log;

import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.nodes.NullElement;
import com.zpj.http.parser.html.nodes.TextNode;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.shouji.market.model.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class ArticleDetailInfo {

    private static final String TAG = "ArticleDetailInfo";

    private AppInfo appInfo;
    private String title;
    private String articleInfo;
    private String writer;
    private String time;

    private final List<HtmlElement> contentElementList = new ArrayList<>();
    private final List<ArticleInfo> relateArticleList = new ArrayList<>();
    private final List<AppInfo> relateAppList = new ArrayList<>();

    private static ArticleDetailInfo parse0(String type, Document doc) {
        ArticleDetailInfo info = new ArticleDetailInfo();

        Element article = doc.selectFirst("div.Min-cent.W1200");
        // 文章信息
        Element content = article.selectFirst("div.Min_L");
        Log.d(TAG, "content=" + content);
        Element leftTop = content.selectFirst("div.Left_top");
        info.title = leftTop.selectFirst("h1.bt").text();
        Log.d(TAG, "title=" + info.title);
        Element articleInfo = leftTop.selectFirst("div.info");
        info.articleInfo = articleInfo.text();
        Log.d(TAG, "articleInfo=" + articleInfo);
        info.time = articleInfo.select("p").get(1).text();
        info.writer = articleInfo.selectFirst("p").text();
        Log.d(TAG, "time=" + info.time + " writer=" + info.writer);

        AppInfo appInfo = new AppInfo();
        Log.d(TAG, "appInfo111=" + appInfo);
        Element appInfoElement = article.selectFirst("div.Min_R").selectFirst("div.Rsty_4").selectFirst("div.info");
        Log.d(TAG, "appInfoElement=" + appInfoElement);
        appInfo.setAppType(type);
        String url = appInfoElement.selectFirst("a.img").attr("href");
        Log.d(TAG, "url=" + url);
        String id = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
        Log.d(TAG, "id=" + id);
        appInfo.setAppId(id);
        appInfo.setAppIcon(appInfoElement.selectFirst("a.img").selectFirst("img").attr("src"));
        Log.d(TAG, "icon=" + id);
        appInfo.setAppTitle(appInfoElement.selectFirst("p").text());
        Log.d(TAG, "appTitle=" + id);
        appInfo.setAppComment(appInfoElement.selectFirst("span").text());
        Log.d(TAG, "comment=" + id);
        info.appInfo = appInfo;

        Log.d(TAG, "appInfo=" + appInfo);

        // 文章内容
        for (Element element : content.selectFirst("div.Left_cent").selectFirst("div#content").select("p")) {
            if (element.select("img").isEmpty()) {
                if (element.select("a").isEmpty()) {
                    TextElement textElement = new TextElement();
                    textElement.isStrong = !element.select("strong").isEmpty();
                    textElement.text = element.text();
                    textElement.setSourceCode(element.toString());
                    info.contentElementList.add(textElement);
                } else {
                    LinkElement linkElement = new LinkElement();
                    linkElement.linkText = element.selectFirst("a").text();
                    linkElement.linkUrl = element.selectFirst("a").attr("href");
                    linkElement.isStrong = !element.select("strong").isEmpty();
                    linkElement.text = element.text();
                    linkElement.setSourceCode(element.toString());
                    info.contentElementList.add(linkElement);
                }
            } else {
                ImageElement imageElement = new ImageElement();
                imageElement.setSourceCode(element.toString());
                imageElement.imageUrl = element.selectFirst("img").attr("src");
                info.contentElementList.add(imageElement);
            }
        }

        Log.d(TAG, "contentElementList.size=" + info.contentElementList.size());

        // 相关文章
        for (Element element : doc.selectFirst("div.Lef_2").selectFirst("div.Lsty_1").select("a")) {
            ArticleInfo articleInfo1 = new ArticleInfo();
            articleInfo1.setTitle(element.text());
            articleInfo1.setUrl("https://" + type + ".shouji.com.cn" + element.attr("href"));
            info.relateArticleList.add(articleInfo1);
        }

        Log.d(TAG, "relateArticleList.size=" + info.relateArticleList.size());

        // 相关应用
        for (Element element : doc.selectFirst("div.Lef_4").select("li")) {
            AppInfo item = new AppInfo();
            String link = element.selectFirst("a.img").attr("href");
            item.setAppId(link.replace("/down/", "").replace(".html", ""));
            item.setAppType(type);
            item.setAppIcon(element.selectFirst("a.img").selectFirst("img").attr("src"));
            item.setAppTitle(element.selectFirst("a.name").text());
            item.setAppSize(element.selectFirst("p.bq").text());
            item.setAppComment(element.select("p.bq").get(1).text());
            info.relateAppList.add(item);
        }

        Log.d(TAG, "relateAppList.size=" + info.relateAppList.size());

        return info;
    }

    private static ArticleDetailInfo parse1(String type, Document doc) {
        ArticleDetailInfo info = new ArticleDetailInfo();

        Element article = doc.selectFirst("div.main_cont.mb20.clearfix").selectFirst("div.html_box");
        Log.d(TAG, "parse article=" + article);
        Element top = article.selectFirst("div#menu_art");
        info.title = top.selectFirst("h1").text();
        Log.d(TAG, "parse title=" + info.title);
        Element articleInfo = top.selectFirst("div#author");
        info.articleInfo = articleInfo.text();
        Log.d(TAG, "parse articleInfo=" + articleInfo);
        List<TextNode> textNodes = articleInfo.textNodes();
        if (textNodes.size() >= 2) {
            info.writer = textNodes.get(0).text();
            info.time = textNodes.get(1).text();
        }
        Log.d(TAG, "parse time=" + info.time + " writer=" + info.writer);

        Element app = article.selectFirst("div#qbdid");
        if (!app.isNull()) {
            AppInfo appInfo = new AppInfo();
            appInfo.setAppType(type);
            appInfo.setAppIcon(app.selectFirst("div.thumb").selectFirst("img").attr("src"));
            Element entryDiv = app.selectFirst("div.entry");
            Element a = entryDiv.selectFirst("a");
            appInfo.setAppTitle(a.text());
            String url = a.attr("href");
            String id = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("."));
            Log.d(TAG, "parse0 appId=" + id);
            appInfo.setAppId(id);
            appInfo.setAppComment(entryDiv.selectFirst("p.p").text());
            appInfo.setAppSize(entryDiv.selectFirst("p.p2").text());
            appInfo.setAppInfo(entryDiv.selectFirst("p.p1").text() + " | " + appInfo.getAppSize());
            info.appInfo = appInfo;
            app.remove();
        }
        Log.d(TAG, "appInfo=" + info.appInfo);

        for (Element element : article.selectFirst("div.htmlcontent").select("p")) {
            if (element.select("img").isEmpty()) {
                if (element.select("a").isEmpty()) {
                    TextElement textElement = new TextElement();
                    textElement.isStrong = !element.select("strong").isEmpty();
                    if (textElement.isStrong) {
                        textElement.strongText = element.select("strong").text();
                    }
                    textElement.text = element.text();
                    textElement.setSourceCode(element.toString());
                    info.contentElementList.add(textElement);
                } else {
                    LinkElement linkElement = new LinkElement();
                    linkElement.linkText = element.selectFirst("a").text();
                    linkElement.linkUrl = element.selectFirst("a").attr("href");
                    linkElement.isStrong = !element.select("strong").isEmpty();
                    if (linkElement.isStrong) {
                        linkElement.strongText = element.select("strong").text();
                    }
                    linkElement.text = element.text();
                    linkElement.setSourceCode(element.toString());
                    info.contentElementList.add(linkElement);
                }
            } else {
                ImageElement imageElement = new ImageElement();
                imageElement.setSourceCode(element.toString());
                imageElement.imageUrl = element.selectFirst("img").attr("src");
                info.contentElementList.add(imageElement);
            }
        }
        Log.d(TAG, "contentElementList.size=" + info.contentElementList.size());

        for (Element element : doc.selectFirst("div#guessWrap").selectFirst("div.zt_list").select("li")) {
            AppInfo item = new AppInfo();
            String url = element.selectFirst("a.ztgimg").attr("href");
            item.setAppId(url.replace("/down/", "").replace("/game/", "").replace(".html", ""));
            item.setAppType(type);
            item.setAppIcon(element.selectFirst("a.ztgimg").selectFirst("img").attr("src"));
            item.setAppTitle(element.selectFirst("a.ztgname").text());
            item.setAppSize(element.selectFirst("span").text());
            item.setAppComment(element.select("span").get(1).text());
            info.relateAppList.add(item);
        }

        Log.d(TAG, "relateAppList.size=" + info.relateAppList.size());

        for (Element element : doc.selectFirst("div#otherarticleWrap").select("li")) {
            ArticleInfo articleInfo1 = new ArticleInfo();
            articleInfo1.setTitle(element.text());
            articleInfo1.setUrl("https://" + type + ".shouji.com.cn" + element.selectFirst("a").attr("href"));
            articleInfo1.setImage(element.selectFirst("img").attr("src"));
            info.relateArticleList.add(articleInfo1);
        }

        Log.d(TAG, "relateArticleList.size=" + info.relateArticleList.size());

        return info;
    }

    public static ArticleDetailInfo parse(String type, Document doc) {
        if (!doc.select("div.Min-cent.W1200").isEmpty()) {
            return parse0(type, doc);
        }

        if (!doc.select("div.main_cont.mb20.clearfix").isEmpty()) {
            return parse1(type, doc);
        }
        return null;
    }

    public static String getTAG() {
        return TAG;
    }

    public String getTitle() {
        return title;
    }

    public String getArticleInfo() {
        return articleInfo;
    }

    public String getWriter() {
        return writer;
    }

    public String getTime() {
        return time;
    }

    public List<HtmlElement> getContentElementList() {
        return contentElementList;
    }

    public List<ArticleInfo> getRelateArticleList() {
        return relateArticleList;
    }

    public List<AppInfo> getRelateAppList() {
        return relateAppList;
    }

    public AppInfo getAppInfo() {
        return appInfo;
    }
}
