package com.zpj.http.core;

import android.text.TextUtils;
import android.util.Log;

import com.zpj.http.ZHttp;
import com.zpj.http.exception.HttpStatusException;
import com.zpj.http.exception.UncheckedIOException;
import com.zpj.http.exception.UnsupportedMimeTypeException;
import com.zpj.http.io.ConstrainableInputStream;
import com.zpj.http.parser.html.Parser;
import com.zpj.http.parser.html.TokenQueue;
import com.zpj.http.parser.html.nodes.Document;
import com.zpj.http.utils.DataUtil;
import com.zpj.http.utils.StringUtil;
import com.zpj.http.utils.UrlUtil;
import com.zpj.http.utils.Validate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import static com.zpj.http.core.Connection.Method.HEAD;
import static com.zpj.http.utils.Normalizer.lowerCase;

/**
 * Implementation of {@link Connection}.
 * @see ZHttp#connect(String)
 */
public class HttpConnection extends AbstractConnection {

    @Override
    public Request createRequest() {
        return new HttpRequest();
    }

    @Override
    public Response onExecute() throws IOException {
        return HttpResponse.execute(req);
    }

}
