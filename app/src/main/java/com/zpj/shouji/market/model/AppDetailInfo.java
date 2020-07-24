package com.zpj.shouji.market.model;

import android.util.Log;

import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class AppDetailInfo {

    private final List<String> imgUrlList = new ArrayList<>();
    private String id;
    private String packageName;
    private String appType;
    private String name;
    private String iconUrl;
//    private String shortInfo;
    private String baseInfo;
    private String lineInfo;
    private String appIntroduceContent;
    private String updateContent;
    private String downloadUrl;
    private String permissionContent;
    private String appInfo;

    private String version;
    private String size;
    private String updateTime;
    private String language;
    private String fee;
    private String ads;
    private String firmware;
    private String author;

    public static AppDetailInfo create(Document doc) {
        AppDetailInfo info = new AppDetailInfo();
        Elements elements = doc.select("pics").select("pic");
        for (Element element : elements){
            info.imgUrlList.add(element.text());
        }
        Log.d("getAppInfo", "imgUrlList=" + info.imgUrlList);
        info.name = doc.selectFirst("name").text();
        info.packageName = doc.selectFirst("package").text();
        info.appType = doc.selectFirst("apptype").text();
        Log.d("getAppInfo", "app_name=" + info.name);
        info.iconUrl = doc.selectFirst("icon").text();
        Log.d("getAppInfo", "app_icon_site=" + info.iconUrl);
//        if (doc.selectFirst("baseinfof").hasText()) {
//            info.shortInfo = doc.selectFirst("baseinfof").text();
//        } else {
//            info.shortInfo = doc.selectFirst("lineinfo").text();
//        }
        info.baseInfo = doc.selectFirst("baseinfof").text();
        info.lineInfo = doc.selectFirst("lineinfo").text();
//        Log.d("getAppInfo", "app_info=" + info.shortInfo);

        elements = doc.select("introduces").select("introduce");
        for (Element introduce : elements) {
            String introduceType = introduce.selectFirst("introducetype").text();
            Log.d("getAppInfo", "introduceType=" + introduceType);
            String introduceTitle = introduce.selectFirst("introducetitle").text();
            Log.d("getAppInfo", "introduceTitle=" + introduceTitle);
            if ("permission".equals(introduceType.toLowerCase())) {
                Elements permissions = introduce.select("permissions").select("permission");
                StringBuilder permissionContent = new StringBuilder();
                for (Element permission : permissions) {
                    if (permissionContent.toString().equals("")) {
                        permissionContent.append(permission.text());
                    }else {
                        permissionContent.append("\n").append(permission.text());
                    }
                }
                Log.d("getAppInfo", "permissionContent=" + permissionContent);
                info.permissionContent = permissionContent.toString();
            } else if ("text".equals(introduceType)) {
                if ("软件信息".equals(introduceTitle) || "游戏信息".equals(introduceTitle)) {
                    String introduceContent = introduce.selectFirst("introduceContent").text();
                    info.version = introduceContent.substring(
                            introduceContent.indexOf("版本：") + 3,
                            introduceContent.indexOf("大小：")
                    ).trim();
                    info.size = introduceContent.substring(
                            introduceContent.indexOf("大小：") + 3,
                            introduceContent.indexOf("更新：")
                    ).trim();
                    info.updateTime = introduceContent.substring(
                            introduceContent.indexOf("更新：") + 3,
                            introduceContent.indexOf("语言：")
                    ).trim();
                    info.language = introduceContent.substring(
                            introduceContent.indexOf("语言：") + 3,
                            introduceContent.indexOf("资费：")
                    ).trim();
                    info.fee = introduceContent.substring(
                            introduceContent.indexOf("资费：") + 3,
                            introduceContent.indexOf("广告：")
                    ).trim();
                    info.ads = introduceContent.substring(
                            introduceContent.indexOf("广告：") + 3,
                            introduceContent.indexOf("固件：")
                    ).trim();
                    info.firmware = introduceContent.substring(
                            introduceContent.indexOf("固件：") + 3,
                            introduceContent.indexOf("作者：")
                    ).trim();
                    info.author = introduceContent.substring(
                            introduceContent.indexOf("作者：") + 3
                    ).trim();
                    info.appInfo = introduceContent.replaceAll(" ", "\n");
                } else if ("软件简介".equals(introduceTitle) || "游戏简介".equals(introduceTitle)) {
                    info.appIntroduceContent = introduce.selectFirst("introduceContent").text();
                    Log.d("getAppInfo", "appIntroduceContent=" + info.appIntroduceContent);
                } else if ("更新内容".equals(introduceTitle)) {
                    info.updateContent = introduce.selectFirst("introduceContent").text();
                    Log.d("getAppInfo", "updateContent=" + info.updateContent);
                }
            }
        }

        info.id = doc.selectFirst("id").text();
        info.downloadUrl = "http://tt.shouji.com.cn/wap/down/soft?id=" + info.id;
        Log.d("downloadUrl", info.downloadUrl);
        return info;
    }

    public List<String> getImgUrlList() {
        return imgUrlList;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getAppIntroduceContent() {
        return appIntroduceContent;
    }

    public String getUpdateContent() {
        return updateContent;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getPermissionContent() {
        return permissionContent;
    }

    public String getAppInfo() {
        return appInfo;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getAppType() {
        return appType;
    }

    public String getBaseInfo() {
        return baseInfo;
    }

    public String getLineInfo() {
        return lineInfo;
    }

    public String getVersion() {
        return version;
    }

    public String getSize() {
        return size;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public String getLanguage() {
        return language;
    }

    public String getFee() {
        return fee;
    }

    public String getAds() {
        return ads;
    }

    public String getFirmware() {
        return firmware;
    }

    public String getAuthor() {
        return author;
    }

    @Override
    public String toString() {
        return "AppDetailInfo{" +
                "imgUrlList=" + imgUrlList +
                ", id='" + id + '\'' +
                ", packageName='" + packageName + '\'' +
                ", appType='" + appType + '\'' +
                ", name='" + name + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", baseInfo='" + baseInfo + '\'' +
                ", lineInfo='" + lineInfo + '\'' +
                ", appIntroduceContent='" + appIntroduceContent + '\'' +
                ", updateContent='" + updateContent + '\'' +
                ", downloadUrl='" + downloadUrl + '\'' +
                ", permissionContent='" + permissionContent + '\'' +
                ", appInfo='" + appInfo + '\'' +
                ", version='" + version + '\'' +
                ", size='" + size + '\'' +
                ", updateTime='" + updateTime + '\'' +
                ", language='" + language + '\'' +
                ", fee='" + fee + '\'' +
                ", ads='" + ads + '\'' +
                ", firmware='" + firmware + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}
