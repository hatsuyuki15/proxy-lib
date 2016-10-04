package com.hatsuyuki.utils.proxy;

import org.jsoup.Connection;

/**
 * Created by Hatsuyuki.
 */
public class Response {
    public int statusCode;
    public String statusMessage;
    public String contentType;
    public String body;

    private Response() {}

    public Response(Connection.Response jsoupResponse) {
        this.statusCode    = jsoupResponse.statusCode();
        this.statusMessage = jsoupResponse.statusMessage();
        this.contentType   = jsoupResponse.contentType();
        this.body          = jsoupResponse.body();
    }
}
