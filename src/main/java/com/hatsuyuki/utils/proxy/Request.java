package com.hatsuyuki.utils.proxy;

import org.jsoup.Connection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Hatsuyuki.
 */
public class Request {
    public String url;
    public Connection.Method method;
    public boolean ignoreContentType;
    public boolean ignoreHttpErrors;
    public int timeout;
    public int maxBodySize;
    public String requestBody;
    public List<KeyVal> data;
    public Map<String, String> headers;
    public Map<String, String> cookies;
    public String source;
    public int priority;

    private Request() {}

    public Request(Connection jsoupConnection) {
        Connection.Request args = jsoupConnection.request();
        this.url               = args.url().toString();
        this.method            = args.method();
        this.ignoreContentType = args.ignoreContentType();
        this.ignoreHttpErrors  = args.ignoreHttpErrors();
        this.timeout           = args.timeout();
        this.maxBodySize       = args.maxBodySize();
        this.headers           = args.headers();
        this.cookies           = args.cookies();
        this.requestBody       = args.requestBody();
        this.data              = new ArrayList<>();
        for (Connection.KeyVal keyVal: args.data()) {
            data.add(new KeyVal(keyVal.key(), keyVal.value()));
        }
    }

    public static class KeyVal {
        public String key;
        public String value;

        private KeyVal() {}

        public KeyVal(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
