package org.hatsuyuki.proxy;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Hatsuyuki.
 */
public class RemoteRequester extends Requester {
    private final static Logger LOGGER = LoggerFactory.getLogger(RemoteRequester.class);

    private int maxRetry= 3;

    private final String proxyHost;
    private final int proxyPort;

    public RemoteRequester(String proxyHost, int proxyPort) {
        super(null);
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
    }

    public void setMaxRetry(int maxRetry) {
        this.maxRetry = maxRetry;
    }

    public int getMaxRetry(int maxRetry) {
        return maxRetry;
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

        int retry = 0;
        while (retry < maxRetry) {
            retry++;
            try {
                Connection.Response jsoupResponse = jsoupConnection.execute();
                return new Response(jsoupResponse);
            } catch (Exception e) {
                if (retry < maxRetry) {
                    LOGGER.info(e.getMessage() + " -> RETRY");
                } else {
                    throw e;
                }
            }
        }

        return null;
    }
}