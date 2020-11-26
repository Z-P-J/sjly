package com.zpj.shouji.market.api;

public enum PreloadApi {

    HOME_BANNER("http://tt.tljpxm.com/androidv3/app_index_xml.jsp?index=1"),
    HOME_RECENT("http://tt.shouji.com.cn/androidv3/app_list_xml.jsp?index=1"),
    HOME_COLLECTION("http://tt.shouji.com.cn/androidv3/yyj_tj_xml.jsp"),
    HOME_SOFT("http://tt.shouji.com.cn/androidv3/special_list_xml.jsp?id=-9998"),
    HOME_GAME("http://tt.shouji.com.cn/androidv3/game_index_xml.jsp?sdk=100&sort=day"),
    HOME_SUBJECT("http://tt.shouji.com.cn/androidv3/special_index_xml.jsp?jse=yes"),
    UPDATE_SOFT("http://tt.shouji.com.cn/androidv3/soft_index_xml.jsp?sort=time"),
    UPDATE_GAME("http://tt.shouji.com.cn/androidv3/game_index_xml.jsp?sort=time"),
    NET_GAME("http://tt.shouji.com.cn/androidv3/netgame.jsp");


    private final String url;

    PreloadApi(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
