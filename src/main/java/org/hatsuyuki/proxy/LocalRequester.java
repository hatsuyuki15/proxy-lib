package org.hatsuyuki.proxy;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Hatsuyuki.
 */
public class LocalRequester extends Requester {
    private final static Logger LOGGER = LoggerFactory.getLogger(LocalRequester.class);
    private int maxNumOfRetry;

    public LocalRequester(int maxNumOfRetry) {
        super(null);
        this.maxNumOfRetry = maxNumOfRetry;
    }

    public LocalRequester() {
        this(0);
    }


    @Override
    public Response forward(Request request) throws IOException {
        Connection jsoupConnection = Jsoup.connect(request.url)
                .method(request.method)
                .ignoreContentType(request.ignoreContentType)
                .ignoreHttpErrors(request.ignoreHttpErrors)
                .timeout(request.timeout)
                .maxBodySize(request.maxBodySize)
                .followRedirects(request.followRedirects())
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

        try {
            return new Response(jsoupConnection.execute());
        } catch (IOException e) {
            throw new IOException(String.format("request=[%s] proxy=[LOCAL] error=[%s]", request.url(), e.getMessage()), e);
        }
    }
}
