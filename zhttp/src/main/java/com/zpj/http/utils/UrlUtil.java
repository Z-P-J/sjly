package com.zpj.http.utils;

import com.zpj.http.core.Connection;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

public class UrlUtil {

    /**
     * Encodes the input URL into a safe ASCII URL string
     * @param url unescaped URL
     * @return escaped URL
     */
    public static String encodeUrl(String url) {
        try {
            URL u = new URL(url);
            return encodeUrl(u).toExternalForm();
        } catch (Exception e) {
            return url;
        }
    }

    public static URL encodeUrl(URL u) {
        try {
            //  odd way to encode urls, but it works!
            String urlS = u.toExternalForm(); // URL external form may have spaces which is illegal in new URL() (odd asymmetry)
            urlS = urlS.replaceAll(" ", "%20");
            final URI uri = new URI(urlS);
            return new URL(uri.toASCIIString());
        } catch (URISyntaxException | MalformedURLException e) {
            // give up and return the original input
            return u;
        }
    }

    // for get url reqs, serialise the data map into the url
    public static void serialiseRequestUrl(Connection.Request req) throws IOException {
        URL in = req.url();
        StringBuilder url = StringUtil.borrowBuilder();
        boolean first = true;
        // reconstitute the query, ready for appends
        url
                .append(in.getProtocol())
                .append("://")
                .append(in.getAuthority()) // includes host, port
                .append(in.getPath())
                .append("?");
        if (in.getQuery() != null) {
            url.append(in.getQuery());
            first = false;
        }
        for (Connection.KeyVal keyVal : req.data()) {
            Validate.isFalse(keyVal.hasInputStream(), "InputStream data not supported in URL query string.");
            if (!first)
                url.append('&');
            else
                first = false;
            url
                    .append(URLEncoder.encode(keyVal.key(), DataUtil.defaultCharset))
                    .append('=')
                    .append(URLEncoder.encode(keyVal.value(), DataUtil.defaultCharset));
        }
        req.url(new URL(StringUtil.releaseBuilder(url)));
        req.data().clear(); // moved into url as get params
    }

}
