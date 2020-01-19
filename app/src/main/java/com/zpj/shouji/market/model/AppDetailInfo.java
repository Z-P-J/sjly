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
                    info.appInfo = introduce.selectFirst("introduceContent").text().replaceAll(" ", "\n");
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
}
