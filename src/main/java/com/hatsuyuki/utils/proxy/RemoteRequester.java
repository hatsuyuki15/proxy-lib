package com.hatsuyuki.utils.proxy;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Hatsuyuki.
 */
public class RemoteRequester extends Requester {
    private final String proxyHost;
    private final int proxyPort;

    public RemoteRequester(String proxyHost, int proxyPort) {
        super(null);
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
    }

    @Override
    public Response forward(Request request) throws IOException {
        Connection jsoupConnection = Jsoup.connect(request.url)
                .method(request.method)
                .ignoreContentType(request.ignoreContentType)
                .ignoreHttpErrors(request.ignoreHttpErrors)
                .timeout(request.timeout)
                .maxBodySize(request.maxBodySize)
                .cookies(request.cookies)
                .requestBody(request.requestBody);
        if (request.headers != null) {
            for (Map.Entry<String, String> h: request.headers.entrySet()) {
                jsoupConnection.header(h.getKey(), h.getValue());
            }
        }
        for (Request.KeyVal keyVal : request.data) {
            jsoupConnection = jsoupConnection.data(keyVal.key, keyVal.value);
        }

        jsoupConnection = jsoupConnection.proxy(proxyHost, proxyPort);

        Connection.Response jsoupResponse = jsoupConnection.execute();
        return new Response(jsoupResponse);
    }
}
