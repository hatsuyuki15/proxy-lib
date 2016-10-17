package org.hatsuyuki.proxy;

import org.jsoup.Connection;

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

    private Response() {}

    public Response(Connection.Response jsoupResponse) {
        this.statusCode    = jsoupResponse.statusCode();
        this.statusMessage = jsoupResponse.statusMessage();
        this.contentType   = jsoupResponse.contentType();
        this.body          = jsoupResponse.body();
        this.headers       = jsoupResponse.headers();
        this.cookies       = jsoupResponse.cookies();
    }
}
