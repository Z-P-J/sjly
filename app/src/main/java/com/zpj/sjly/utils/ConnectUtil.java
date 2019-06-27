package com.zpj.sjly.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public final class ConnectUtil {

    private static final String USER_AGENT = "okhttp/3.0.1";
    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String VALUE_ACCEPT_ENCODING = "gzip";

    private ConnectUtil() {

    }

    public static Document getDocument(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent(USER_AGENT)
                .header("Cookie", UserHelper.getCookie())
                .header(HEADER_ACCEPT_ENCODING, VALUE_ACCEPT_ENCODING)
                .header("referer", url)
                .ignoreContentType(true)
                .get();
    }

}
