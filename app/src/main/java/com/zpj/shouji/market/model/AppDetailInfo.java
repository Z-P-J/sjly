package com.zpj.shouji.market.model;

import android.text.TextUtils;
import android.util.Log;

import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.shouji.market.download.MissionBinder;
import com.zpj.shouji.market.utils.BeanUtils;
import com.zpj.shouji.market.utils.BeanUtils.Select;

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
    private String editorComment;
    private String specialStatement;
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

    @Select(selector = "ratingvalue")
    private String ratingValue;
    @Select(selector = "scoreInfo")
    private String scoreInfo;
    @Select(selector = "scoreState")
    private boolean scoreState;
    @Select(selector = "favState")
    private boolean favState;
    @Select(selector = "reviewcount")
    private int reviewCount;
    @Select(selector = "relativecommunitycount")
    private int discoverCount;

    private String otherAppUrl;

    private List<AppInfo> otherAppList;

    private final List<AppUrlInfo> appUrlInfoList = new ArrayList<>();

    public static class AppUrlInfo extends MissionBinder {
        @Select(selector = "urltype")
        private String urlType;
        @Select(selector = "urlname")
        private String urlName;
        @Select(selector = "urlpackage")
        private String urlPackage;
        @Select(selector = "minsdk")
        private String minSdk;
        @Select(selector = "urladress")
        private String urlAdress;
        @Select(selector = "yunUrl")
        private String yunUrl;
        @Select(selector = "md5")
        private String md5;
        @Select(selector = "more")
        private String more;

        private String id;
        private String appName;
        private String packageName;
        private String appType;
        private String appIcon;

        public String getUrlType() {
            return urlType;
        }

        public String getUrlName() {
            return urlName;
        }

        public String getUrlPackage() {
            return urlPackage;
        }

        public String getMinSdk() {
            return minSdk;
        }

        public String getUrlAdress() {
            return urlAdress;
        }

        @Override
        public String getYunUrl() {
            return yunUrl;
        }

        @Override
        public String getAppId() {
            return id;
        }

        @Override
        public String getAppName() {
            return packageName;
        }

        @Override
        public String getAppType() {
            return appType;
        }

        @Override
        public String getPackageName() {
            return packageName;
        }

        @Override
        public String getAppIcon() {
            return appIcon;
        }

        @Override
        public boolean isShareApp() {
            return false;
        }

        public String getMd5() {
            return md5;
        }

        public String getMore() {
            return more;
        }
    }

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

        info.ratingValue = doc.selectFirst("ratingvalue").text();
        info.scoreInfo = doc.selectFirst("scoreInfo").text();
        info.scoreState = "1".equals(doc.selectFirst("scoreState").text());
        info.favState = "1".equals(doc.selectFirst("favState").text());
        info.reviewCount = Integer.parseInt(doc.selectFirst("reviewcount").text());
        info.discoverCount = Integer.parseInt(doc.selectFirst("relativecommunitycount").text());

        elements = doc.select("introduces").select("introduce");
        for (Element introduce : elements) {
            String introduceType = introduce.selectFirst("introducetype").text();
            Log.d("getAppInfo", "introduceType=" + introduceType);
            String introduceTitle = introduce.selectFirst("introducetitle").text();
            Log.d("getAppInfo", "introduceTitle=" + introduceTitle);
            if ("permission".equals(introduceType.toLowerCase())) {
                Log.d("getAppInfo", "permissions111=" + introduce.selectFirst("permissions"));
                Elements permissions = introduce.selectFirst("permissions").select("permission");
                Log.d("getAppInfo", "permissions222=" + permissions.toString());
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
                    info.packageName = introduceContent.substring(
                            introduceContent.indexOf("包名：") + 3,
                            introduceContent.indexOf("版本：")
                    ).trim();
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
//                            introduceContent.indexOf("资费：")
                            introduceContent.indexOf("广告：")
                    ).trim();
//                    info.fee = introduceContent.substring(
//                            introduceContent.indexOf("资费：") + 3,
//                            introduceContent.indexOf("广告：")
//                    ).trim();
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
//                    info.appInfo = introduceContent.replaceAll(" ", "\n");
                    info.appInfo = "包名：" + info.packageName + "\n" +
                            "版本：" + info.version + "\n" +
                            "大小：" + info.size + "\n" +
                            "更新：" + info.updateTime + "\n" +
                            "语言：" + info.language + "\n" +
                            "广告：" + info.ads + "\n" +
                            "固件：" + info.firmware + "\n" +
                            "作者：" + info.author;
                } else if ("软件简介".equals(introduceTitle) || "游戏简介".equals(introduceTitle)) {
                    info.appIntroduceContent = introduce.selectFirst("introduceContent").text();
                    Log.d("getAppInfo", "appIntroduceContent=" + info.appIntroduceContent);
                } else if ("更新内容".equals(introduceTitle)) {
                    info.updateContent = introduce.selectFirst("introduceContent").text();
                    Log.d("getAppInfo", "updateContent=" + info.updateContent);
                } else if ("小编点评".equals(introduceTitle) || "游戏点评".equals(introduceTitle)) {
                    info.editorComment = introduce.selectFirst("introduceContent").text();
                } else if ("特别说明".equals(introduceTitle)) {
                    info.specialStatement = introduce.selectFirst("introduceContent").text();
                }
            } else if ("app".equals(introduceType)) {
                info.otherAppList = new ArrayList<>();
                info.otherAppUrl = introduce.selectFirst("introduceurl").text();
                for (Element element : introduce.selectFirst("apps").select("app")) {
                    AppInfo appInfo = new AppInfo();
                    appInfo.setAppId(element.selectFirst("appid").text());
                    appInfo.setAppTitle(element.selectFirst("appname").text());
                    appInfo.setAppType(element.selectFirst("recommendapptype").text());
                    appInfo.setAppSize(element.selectFirst("appnsize").text());
                    appInfo.setAppIcon(element.selectFirst("appicon").text());
                    info.otherAppList.add(appInfo);
                }
            }
        }

        for (Element url : doc.selectFirst("urls").select("url")) {
            AppUrlInfo appUrlInfo = BeanUtils.createBean(url, AppUrlInfo.class);
            if (appUrlInfo != null) {
                appUrlInfo.id = appUrlInfo.getUrlAdress().substring(appUrlInfo.getUrlAdress().lastIndexOf("id=") + 3);
                appUrlInfo.appName = info.name;
                appUrlInfo.appIcon = info.iconUrl;
                appUrlInfo.appType = info.appType;
                appUrlInfo.packageName = info.packageName;
                info.appUrlInfoList.add(appUrlInfo);
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
        if (TextUtils.isEmpty(permissionContent)) {
            return "无";
        }
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

    public String getEditorComment() {
        return editorComment;
    }

    public String getSpecialStatement() {
        return specialStatement;
    }

    public String getRatingValue() {
        return ratingValue;
    }

    public String getScoreInfo() {
        return scoreInfo;
    }

    public boolean isScoreState() {
        return scoreState;
    }

    public void setFavState(boolean favState) {
        this.favState = favState;
    }

    public boolean isFavState() {
        return favState;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setDiscoverCount(int discoverCount) {
        this.discoverCount = discoverCount;
    }

    public int getDiscoverCount() {
        return discoverCount;
    }

    public String getOtherAppUrl() {
        if (TextUtils.isEmpty(otherAppUrl)) {
            return "http://tt.shouji.com.cn/androidv4/app_auther_xml.jsp?author=" + getAuthor() + "&t=" + getAppType();
        }
        return otherAppUrl;
    }

    public List<AppInfo> getOtherAppList() {
        return otherAppList;
    }

    public List<AppUrlInfo> getAppUrlInfoList() {
        return appUrlInfoList;
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
