package com.zpj.http.core;

import com.zpj.http.parser.html.Parser;
import com.zpj.http.utils.DataUtil;
import com.zpj.http.utils.Validate;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.ArrayList;
import java.util.Collection;

import javax.net.ssl.SSLSocketFactory;

public class HttpRequest extends HttpBase<Connection.Request> implements Connection.Request {

    /**
     * Many users would get caught by not setting a user-agent and therefore getting different responses on their desktop
     * vs in jsoup, which would otherwise default to {@code Java}. So by default, use a desktop UA.
     */
    public static final String DEFAULT_UA =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/53.0.2785.143 Safari/537.36";

    private Proxy proxy; // nullable
    private int timeoutMilliseconds;
    private int maxBodySizeBytes;
    //        private boolean followRedirects;
    private Collection<Connection.KeyVal> data;
    private String body = null;
    private boolean ignoreHttpErrors = false;
    private boolean ignoreContentType = false;
    private Parser parser;
    private boolean parserDefined = false; // called parser(...) vs initialized in ctor
    private String postDataCharset = DataUtil.defaultCharset;
    private SSLSocketFactory sslSocketFactory;
    private IHttp.OnRedirectListener onRedirectListener;

    HttpRequest() {
        timeoutMilliseconds = 30000; // 30 seconds
        maxBodySizeBytes = 1024 * 1024; // 1MB
//            followRedirects = true;
        data = new ArrayList<>();
        method = Connection.Method.GET;
        addHeader("Accept-Encoding", "gzip");
        addHeader(HttpHeader.USER_AGENT, DEFAULT_UA);
        parser = Parser.htmlParser();
    }

    public Proxy proxy() {
        return proxy;
    }

    public HttpRequest proxy(Proxy proxy) {
        this.proxy = proxy;
        return this;
    }

    public HttpRequest proxy(String host, int port) {
        this.proxy = new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(host, port));
        return this;
    }

    public int timeout() {
        return timeoutMilliseconds;
    }

    public HttpRequest timeout(int millis) {
        Validate.isTrue(millis >= 0, "Timeout milliseconds must be 0 (infinite) or greater");
        timeoutMilliseconds = millis;
        return this;
    }

    public int maxBodySize() {
        return maxBodySizeBytes;
    }

    public Connection.Request maxBodySize(int bytes) {
        Validate.isTrue(bytes >= 0, "maxSize must be 0 (unlimited) or larger");
        maxBodySizeBytes = bytes;
        return this;
    }

//        public boolean followRedirects() {
//            return followRedirects;
//        }

//        public Connection.Request followRedirects(boolean followRedirects) {
//            this.followRedirects = followRedirects;
//            return this;
//        }

    public boolean ignoreHttpErrors() {
        return ignoreHttpErrors;
    }

    public SSLSocketFactory sslSocketFactory() {
        return sslSocketFactory;
    }

    public void sslSocketFactory(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
    }

    public Connection.Request ignoreHttpErrors(boolean ignoreHttpErrors) {
        this.ignoreHttpErrors = ignoreHttpErrors;
        return this;
    }

    public boolean ignoreContentType() {
        return ignoreContentType;
    }

    public Connection.Request ignoreContentType(boolean ignoreContentType) {
        this.ignoreContentType = ignoreContentType;
        return this;
    }

    public HttpRequest data(Connection.KeyVal keyval) {
        Validate.notNull(keyval, "Key val must not be null");
        data.add(keyval);
        return this;
    }

    public Collection<Connection.KeyVal> data() {
        return data;
    }

    public Connection.Request requestBody(String body) {
        this.body = body;
        return this;
    }

    public String requestBody() {
        return body;
    }

    public HttpRequest parser(Parser parser) {
        this.parser = parser;
        parserDefined = true;
        return this;
    }

    public Parser parser() {
        return parser;
    }

    public Connection.Request postDataCharset(String charset) {
        Validate.notNull(charset, "Charset must not be null");
        if (!Charset.isSupported(charset)) throw new IllegalCharsetNameException(charset);
        this.postDataCharset = charset;
        return this;
    }

    public String postDataCharset() {
        return postDataCharset;
    }

    @Override
    public Connection.Request onRedirect(IHttp.OnRedirectListener listener) {
        this.onRedirectListener = listener;
        return this;
    }

    @Override
    public IHttp.OnRedirectListener getOnRedirectListener() {
        return onRedirectListener;
    }

    public boolean isParserDefined() {
        return parserDefined;
    }
}
