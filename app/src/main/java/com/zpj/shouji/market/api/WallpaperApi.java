package com.zpj.shouji.market.api;

import android.util.Log;

import com.felix.atoast.library.AToast;
import com.zpj.http.ZHttp;
import com.zpj.http.core.Connection;
import com.zpj.http.core.IHttp;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.parser.html.nodes.Element;
import com.zpj.http.parser.html.select.Elements;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.event.HideLoadingEvent;
import com.zpj.shouji.market.event.ShowLoadingEvent;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.model.WallpaperTag;
import com.zpj.shouji.market.utils.Callback;
import com.zpj.utils.ContextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class WallpaperApi {

    private static final String USER_AGENT = "okhttp/3.0.1";
    private static final List<WallpaperTag> wallpaperTags = new ArrayList<>(0);
    private static boolean flag;

    private WallpaperApi() {

    }

    public static void getWallpaperTags(Callback<List<WallpaperTag>> callback) {
        if (flag && !wallpaperTags.isEmpty()) {
            callback.onCallback(wallpaperTags);
            return;
        }
        HttpApi.get("http://tt.shouji.com.cn/app/bizhi_tags.jsp")
                .onSuccess(data -> {
                    Elements elements = data.select("item");
                    wallpaperTags.clear();
                    for (Element item : elements) {
                        wallpaperTags.add(WallpaperTag.create(item));
                    }
                    callback.onCallback(wallpaperTags);
                    flag = true;
                })
                .onError(throwable -> {
                    String[] tags = ContextUtils.getApplicationContext().getResources().getStringArray(R.array.default_wallpaper_tags);
                    wallpaperTags.clear();
                    for (int i = 0; i < tags.length; i++) {
                        wallpaperTags.add(WallpaperTag.create(Integer.toString(i + 1), tags[i]));
                    }
                    callback.onCallback(wallpaperTags);
                })
                .subscribe();
    }


    public static void shareWallpaperApi(File file, String content, String tag, boolean isPrivate, Runnable runnable, IHttp.OnStreamWriteListener listener) {
        Log.d("shareWallpaperApi", "file=" + file.getPath());
        Log.d("shareWallpaperApi", "content=" + content + " tag=" + tag);
        ShowLoadingEvent.post("上传乐图...");
//        图片只能上传png,gif,jpg,png格式
        try {
            ZHttp.post(String.format("http://tt.shouji.com.cn/app/bizhi_publish_v5.jsp?jsessionid=%s", UserManager.getInstance().getSessionId()))
                    .data("tagurl", "http://tt.shouji.com.cn/app/faxian.jsp?index=faxian")
                    .data("sn", UserManager.getInstance().getSn())
                    .data("phone", "MI 5s")
                    .data("replyid", "0")
                    .data("gkbz", isPrivate ? "0" : "1")
                    .data("tag", tag)
                    .data("content", content)
                    .data("image", "image.png", new FileInputStream(file), listener)
                    .validateTLSCertificates(false)
                    .userAgent(USER_AGENT)
                    .onRedirect(redirectUrl -> {
                        Log.d("connect", "onRedirect redirectUrl=" + redirectUrl);
                        return true;
                    })
                    .cookie(UserManager.getInstance().getCookie())
                    .ignoreContentType(true)
                    .toXml()
                    .onSuccess(data -> {
                        Log.d("shareWallpaperApi", "data=" + data);
                        String info = data.selectFirst("info").text();
                        if ("success".equals(data.selectFirst("result").text())) {
                            AToast.success(info);
                            runnable.run();
                        } else {
                            AToast.error(info);
                        }
                        HideLoadingEvent.postDelayed(500);
                    })
                    .onError(throwable -> {
                        AToast.error("上传失败！" + throwable.getMessage());
                        HideLoadingEvent.postDelayed(500);
                    })
                    .subscribe();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            AToast.error("上传失败！" + e.getMessage());
            HideLoadingEvent.postDelayed(500);
        }
    }

}
