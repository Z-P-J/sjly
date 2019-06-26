package com.zpj.qxdownloader.jsoup;

import com.zpj.qxdownloader.jsoup.connection.Connection;
import com.zpj.qxdownloader.jsoup.connection.HttpConnection;

/**
 The core public access point to the jsoup functionality.

 @author Jonathan Hedley */
public class Jsoup {
    private Jsoup() {}

    /**
     * Creates a new {@link Connection} to a URL. Use to fetch and parse a HTML page.
     * <p>
     * Use examples:
     * <ul>
     *  <li><code>Document doc = Jsoup.connect("http://example.com").userAgent("Mozilla").data("name", "jsoup").get();</code></li>
     *  <li><code>Document doc = Jsoup.connect("http://example.com").cookie("auth", "token").post();</code></li>
     * </ul>
     * @param url URL to connect to. The protocol must be {@code http} or {@code https}.
     * @return the connection. You can add data, cookies, and headers; set the user-agent, referrer, method; and then execute.
     */
    public static Connection connect(String url) {
        return HttpConnection.connect(url);
    }
    
}
