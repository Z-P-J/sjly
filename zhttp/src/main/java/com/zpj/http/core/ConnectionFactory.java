package com.zpj.http.core;

import java.net.URL;

public class ConnectionFactory {

    private ConnectionFactory() {

    }

    public static Connection createHttpConnection(String url) {
        Connection con = new HttpConnection();
        con.url(url);
        return con;
    }

    public static Connection createHttpConnection(URL url) {
        Connection con = new HttpConnection();
        con.url(url);
        return con;
    }

}
