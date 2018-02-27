package org.hatsuyuki.proxy;

import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Hatsuyuki on 12/27/2016.
 */
public abstract class Proxy {
    public Response request(org.jsoup.Connection jsoupConnection) throws Exception {
        return request(new Request(jsoupConnection));
    }

    public Connection connect(String url) {
        return new Connection(this, url);
    }

    public abstract Response request(Request request) throws Exception;

    public static class Connection {
        private final Proxy proxy;
        private final Request request;

        public Connection(Proxy proxy, String url) {
            this.proxy = proxy;
            this.request = new Request();
            request.url = url;
            request.ignoreContentType = true;
            request.ignoreHttpErrors = true;
            request.headers = new HashMap<>();
            request.cookies = new HashMap<>();
            request.data = new ArrayList<>();
            request.timeout = 1000;
            request.followRedirects = true;
        }

        public Connection requestBody(String body) {
            request.requestBody(body);
            return this;
        }

        public Connection requestJson(Object json) {
            request.requestBody(Json.toString(json));
            request.headers.put("Content-Type", "application/json");
            return this;
        }

        public Connection header(String name, String value) {
            request.headers.put(name, value);
            return this;
        }

        public Connection cookie(String name, String value) {
            request.cookies.put(name, value);
            return this;
        }

        public Connection data(String key, String value) {
            request.data.add(new Request.KeyVal(key, value));
            return this;
        }

        public Connection headers(Map<String, String> headers) {
            request.headers(headers);
            return this;
        }

        public Connection cookies(Map<String, String> cookies) {
            request.cookies(cookies);
            return this;
        }

        public Connection data(List<Request.KeyVal> data) {
            request.data(data);
            return this;
        }

        public Connection referrer(String referrer) {
            request.headers.put("Referer", referrer);
            return this;
        }

        public Connection userAgent(String userAgent) {
            request.headers.put("User-Agent", userAgent);
            return this;
        }

        public Connection timeout(int milliseconds) {
            request.timeout(milliseconds);
            return this;
        }

        public Connection followRedirects(boolean followRedirect) {
            request.followRedirects(followRedirect);
            return this;
        }

        public Response get() throws Exception {
            request.method(Method.GET);
            return proxy.request(request);
        }

        public Response post() throws Exception {
            request.method(Method.POST);
            return proxy.request(request);
        }

        public Response put() throws Exception {
            request.method(Method.PUT);
            return proxy.request(request);
        }
    }
}
