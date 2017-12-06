package org.hatsuyuki.proxy;

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
    public boolean followRedirects;
    public String source;
    public int priority;
    public Long sessionId;

    public Request() {}

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
        this.followRedirects   = args.followRedirects();
        for (Connection.KeyVal keyVal: args.data()) {
            data.add(new KeyVal(keyVal.key(), keyVal.value()));
        }
    }

    public String url() {
        return url;
    }

    public void url(String url) {
        this.url = url;
    }

    public Connection.Method method() {
        return method;
    }

    public void method(Connection.Method method) {
        this.method = method;
    }

    public boolean ignoreContentType() {
        return ignoreContentType;
    }

    public void ignoreContentType(boolean ignoreContentType) {
        this.ignoreContentType = ignoreContentType;
    }

    public boolean ignoreHttpErrors() {
        return ignoreHttpErrors;
    }

    public void  ignoreHttpErrors(boolean ignoreHttpErrors) {
        this.ignoreHttpErrors = ignoreHttpErrors;
    }

    public int timeout() {
        return timeout;
    }

    public void timeout(int timeout) {
        this.timeout = timeout;
    }

    public int maxBodySize() {
        return maxBodySize;
    }

    public void maxBodySize(int maxBodySize) {
        this.maxBodySize = maxBodySize;
    }

    public String body() {
        return requestBody;
    }

    public void body(String requestBody) {
        this.requestBody = requestBody;
    }

    public List<KeyVal> data() {
        return data;
    }

    public void data(List<KeyVal> data) {
        this.data = data;
    }

    public Map<String, String> headers() {
        return headers;
    }

    public void headers(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> cookies() {
        return cookies;
    }

    public void cookies(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public boolean followRedirects() {
        return followRedirects;
    }

    public void followRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
    }

    public String source() {
        return source;
    }

    public void source(String source) {
        this.source = source;
    }

    public int priority() {
        return priority;
    }

    public void priority(int priority) {
        this.priority = priority;
    }

    public String requestBody() {
        return requestBody;
    }

    public void requestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public void sessonId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long sessionId() {
        return sessionId;
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
