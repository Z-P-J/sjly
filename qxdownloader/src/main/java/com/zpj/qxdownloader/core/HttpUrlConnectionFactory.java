package com.zpj.qxdownloader.core;

import android.text.TextUtils;

import com.zpj.qxdownloader.util.ssl.SSLContextUtil;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

class HttpUrlConnectionFactory {

    private static final String COOKIE = "Cookie";
    private static final String USER_AGENT = "User-Agent";
    private static final String REFERER = "Referer";


    static HttpURLConnection getConnection(DownloadMission mission, long start, long end) throws Exception {
        URL url = new URL(mission.url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        wrapConnection(conn, mission);
        conn.setRequestProperty("Range", "bytes=" + start + "-" + end);
        return conn;
    }

    static HttpURLConnection getConnection(DownloadMission mission) throws Exception {
        URL url = new URL(mission.url);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        wrapConnection(conn, mission);
        return conn;
    }

    private static void wrapConnection(HttpURLConnection conn, DownloadMission mission) {
        if (conn instanceof HttpsURLConnection) {
//			HttpsURLConnection httpsURLConnection = (HttpsURLConnection) conn;
            SSLContext sslContext =
                    SSLContextUtil.getSSLContext(DownloadManagerImpl.getInstance().getContext(), SSLContextUtil.CA_ALIAS, SSLContextUtil.CA_PATH);
            if (sslContext == null) {
                sslContext = SSLContextUtil.getDefaultSLLContext();
            }
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            ((HttpsURLConnection) conn).setSSLSocketFactory(ssf);
            ((HttpsURLConnection) conn).setHostnameVerifier(SSLContextUtil.HOSTNAME_VERIFIER);
        }
//        conn.setInstanceFollowRedirects(false);
        conn.setConnectTimeout(mission.getConnectOutTime());
        conn.setReadTimeout(mission.getReadOutTime());
        if (!TextUtils.isEmpty(mission.getCookie().trim())) {
            conn.setRequestProperty(COOKIE, mission.getCookie());
        }
        conn.setRequestProperty(USER_AGENT, mission.getUserAgent());
//        conn.setRequestProperty("Accept", "*/*");
        conn.setRequestProperty(REFERER, mission.url);
        Map<String, String> headers = mission.getHeaders();
        if (!headers.isEmpty()) {
            for (String key : headers.keySet()) {
                conn.setRequestProperty(key, headers.get(key));
            }
        }
    }

}
