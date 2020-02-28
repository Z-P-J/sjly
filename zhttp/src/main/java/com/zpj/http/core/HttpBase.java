package com.zpj.http.core;

import android.text.TextUtils;

import com.zpj.http.utils.StringUtil;
import com.zpj.http.utils.Validate;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.zpj.http.utils.Normalizer.lowerCase;

public abstract class HttpBase<T extends Connection.Base> implements Connection.Base<T> {

    protected URL url;
    protected Connection.Method method;
    protected final Map<String, List<String>> headers;
    protected final Map<String, String> cookies;

    public HttpBase() {
        headers = new LinkedHashMap<>();
        cookies = new LinkedHashMap<>();
    }

    public URL url() {
        return url;
    }

    public T url(URL url) {
        Validate.notNull(url, "URL must not be null");
        this.url = url;
        return (T) this;
    }

    public Connection.Method method() {
        return method;
    }

    public T method(Connection.Method method) {
        Validate.notNull(method, "Method must not be null");
        this.method = method;
        return (T) this;
    }

    public String header(String name) {
        List<String> vals = getHeadersCaseInsensitive(name);
        if (vals.size() > 0) {
            // https://www.w3.org/Protocols/rfc2616/rfc2616-sec4.html#sec4.2
            return StringUtil.join(vals, ", ");
        }
        return null;
    }

    @Override
    public T addHeader(String name, String value) {
        Validate.notEmpty(name);
        value = value == null ? "" : value;

        List<String> values = headers(name);
        if (values.isEmpty()) {
            values = new ArrayList<>();
            headers.put(name, values);
        }
        values.add(fixHeaderEncoding(value));

        return (T) this;
    }

    @Override
    public List<String> headers(String name) {
        return getHeadersCaseInsensitive(name);
    }

    private static String fixHeaderEncoding(String val) {
        try {
            byte[] bytes = val.getBytes("ISO-8859-1");
            if (!looksLikeUtf8(bytes))
                return val;
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // shouldn't happen as these both always exist
            return val;
        }
    }

    private static boolean looksLikeUtf8(byte[] input) {
        int i = 0;
        // BOM:
        if (input.length >= 3 && (input[0] & 0xFF) == 0xEF
                && (input[1] & 0xFF) == 0xBB & (input[2] & 0xFF) == 0xBF) {
            i = 3;
        }

        int end;
        for (int j = input.length; i < j; ++i) {
            int o = input[i];
            if ((o & 0x80) == 0) {
                continue; // ASCII
            }

            // UTF-8 leading:
            if ((o & 0xE0) == 0xC0) {
                end = i + 1;
            } else if ((o & 0xF0) == 0xE0) {
                end = i + 2;
            } else if ((o & 0xF8) == 0xF0) {
                end = i + 3;
            } else {
                return false;
            }

            if (end >= input.length)
                return false;

            while (i < end) {
                i++;
                o = input[i];
                if ((o & 0xC0) != 0x80) {
                    return false;
                }
            }
        }
        return true;
    }

    public T header(String name, String value) {
        Validate.notEmpty(name, "Header name must not be empty");
        removeHeader(name); // ensures we don't get an "accept-encoding" and a "Accept-Encoding"
        addHeader(name, value);
        return (T) this;
    }

    public boolean hasHeader(String name) {
        return !getHeadersCaseInsensitive(name).isEmpty();
    }

    /**
     * Test if the request has a header with this value (case insensitive).
     */
    public boolean hasHeaderWithValue(String name, String value) {
        Validate.notEmpty(name);
        Validate.notEmpty(value);
        List<String> values = headers(name);
        for (String candidate : values) {
            if (value.equalsIgnoreCase(candidate))
                return true;
        }
        return false;
    }

    public T removeHeader(String name) {
        Validate.notEmpty(name, "Header name must not be empty");
        Map.Entry<String, List<String>> entry = scanHeaders(name); // remove is case insensitive too
        if (entry != null)
            headers.remove(entry.getKey()); // ensures correct case
        return (T) this;
    }

    public Map<String, String> headers() {
        LinkedHashMap<String, String> map = new LinkedHashMap<>(headers.size());
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            String header = entry.getKey();
            List<String> values = entry.getValue();
            if (values.size() > 0)
                map.put(header, values.get(0));
        }
        return map;
    }

    @Override
    public Map<String, List<String>> multiHeaders() {
        return headers;
    }

    private List<String> getHeadersCaseInsensitive(String name) {
        if (TextUtils.isEmpty(name)) {
            return Collections.emptyList();
        }
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            if (name.equalsIgnoreCase(entry.getKey()))
                return entry.getValue();
        }
        return Collections.emptyList();
    }

    private Map.Entry<String, List<String>> scanHeaders(String name) {
        String lc = lowerCase(name);
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            if (lowerCase(entry.getKey()).equals(lc))
                return entry;
        }
        return null;
    }

    public String cookie(String name) {
        Validate.notEmpty(name, "Cookie name must not be empty");
        return cookies.get(name);
    }

    public T cookie(String name, String value) {
        Validate.notEmpty(name, "Cookie name must not be empty");
        Validate.notNull(value, "Cookie value must not be null");
        cookies.put(name, value);
        return (T) this;
    }

    public boolean hasCookie(String name) {
        Validate.notEmpty(name, "Cookie name must not be empty");
        return cookies.containsKey(name);
    }

    public T removeCookie(String name) {
        Validate.notEmpty(name, "Cookie name must not be empty");
        cookies.remove(name);
        return (T) this;
    }

    public Map<String, String> cookies() {
        return cookies;
    }

    @Override
    public String cookieStr() {
        StringBuilder sb = StringUtil.borrowBuilder();
        boolean first = true;
        for (Map.Entry<String, String> cookie : cookies.entrySet()) {
            if (!first)
                sb.append("; ");
            else
                first = false;
            sb.append(cookie.getKey()).append('=').append(cookie.getValue());
            // todo: spec says only ascii, no escaping / encoding defined. validate on set? or escape somehow here?
        }
        return StringUtil.releaseBuilder(sb);
    }
}
