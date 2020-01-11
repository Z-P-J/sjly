package com.zpj.shouji.market.utils;

import com.zpj.http.ZHttp;
import com.zpj.http.parser.html.nodes.Document;

import java.io.IOException;

public final class ConnectUtil {

    private static final String USER_AGENT = "okhttp/3.0.1";
    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String VALUE_ACCEPT_ENCODING = "gzip";

    private ConnectUtil() {

    }

    public static Document getDocument(String url) throws IOException {
        return ZHttp.get(url)
                .userAgent(USER_AGENT)
                .cookie(UserHelper.getCookie())
                .header(HEADER_ACCEPT_ENCODING, VALUE_ACCEPT_ENCODING)
                .referrer(url)
                .ignoreContentType(true)
                .toHtml();
    }

}
