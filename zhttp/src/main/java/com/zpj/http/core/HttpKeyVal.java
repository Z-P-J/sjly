package com.zpj.http.core;

import com.zpj.http.utils.Validate;

import java.io.InputStream;

public class HttpKeyVal implements Connection.KeyVal {

    private String key;
    private String value;
    private InputStream stream;
    private String contentType;
    private IHttp.OnStreamWriteListener listener;

    public static HttpKeyVal create(String key, String value) {
        return new HttpKeyVal().key(key).value(value);
    }

    public static HttpKeyVal create(String key, String filename, InputStream stream) {
        return new HttpKeyVal().key(key).value(filename).inputStream(stream);
    }

    public static HttpKeyVal create(String key, String filename, InputStream stream, IHttp.OnStreamWriteListener listener) {
        return new HttpKeyVal().key(key).value(filename).inputStream(stream).setListener(listener);
    }

    private HttpKeyVal() {}

    public HttpKeyVal key(String key) {
        Validate.notEmpty(key, "Data key must not be empty");
        this.key = key;
        return this;
    }

    public String key() {
        return key;
    }

    public HttpKeyVal value(String value) {
        Validate.notNull(value, "Data value must not be null");
        this.value = value;
        return this;
    }

    public String value() {
        return value;
    }

    public HttpKeyVal inputStream(InputStream inputStream) {
        Validate.notNull(value, "Data input stream must not be null");
        this.stream = inputStream;
        return this;
    }

    public InputStream inputStream() {
        return stream;
    }

    public boolean hasInputStream() {
        return stream != null;
    }

    @Override
    public Connection.KeyVal contentType(String contentType) {
        Validate.notEmpty(contentType);
        this.contentType = contentType;
        return this;
    }

    @Override
    public String contentType() {
        return contentType;
    }

    @Override
    public IHttp.OnStreamWriteListener getListener() {
        return listener;
    }

    @Override
    public HttpKeyVal setListener(IHttp.OnStreamWriteListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }

}
