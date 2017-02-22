package org.hatsuyuki.proxy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URL;
import java.util.Map;

/**
 * Created by Hatsuyuki.
 */
public class Response {
    public int statusCode;
    public String statusMessage;
    public String contentType;
    public String body;
    public Map<String, String> headers;
    public Map<String, String> cookies;

    @JsonIgnore
    public Request request;

    Response() {}

    public Response(Connection.Response jsoupResponse) {
        this.statusCode    = jsoupResponse.statusCode();
        this.statusMessage = jsoupResponse.statusMessage();
        this.contentType   = jsoupResponse.contentType();
        this.body          = jsoupResponse.body();
        this.headers       = jsoupResponse.headers();
        this.cookies       = jsoupResponse.cookies();
    }

    public int statusCode() {
        return statusCode;
    }

    public void statusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String statusMessage() {
        return statusMessage;
    }

    public void statusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public String contentType() {
        return contentType;
    }

    public void contentType(String contentType) {
        this.contentType = contentType;
    }

    public String body() {
        return body;
    }

    public void body(String body) {
        this.body = body;
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

    @JsonIgnore
    public Request request() {
        return request;
    }

    @JsonIgnore
    public void request(Request request) {
        this.request = request;
    }

    @JsonIgnore
    public Document parse() {
        return Jsoup.parse(this.body);
    }
}
