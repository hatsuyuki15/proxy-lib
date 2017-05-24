package org.hatsuyuki.proxy;

import com.google.common.base.Stopwatch;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Hatsuyuki.
 */
public class LocalRequester extends Requester {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public LocalRequester() {
        super(null);
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
                .requestBody(request.requestBody)
                .ignoreHttpErrors(true);
        if (request.headers != null) {
            for (Map.Entry<String, String> h: request.headers.entrySet()) {
                jsoupConnection.header(h.getKey(), h.getValue());
            }
        }
        for (Request.KeyVal keyVal : request.data) {
            jsoupConnection = jsoupConnection.data(keyVal.key, keyVal.value);
        }

        try {
            Stopwatch stopwatch = Stopwatch.createStarted();
            Connection.Response rawResponse = jsoupConnection.execute();
            stopwatch.stop();

            Response response = new Response(rawResponse);
            response.metadata = new HashMap<>();
            response.metadata.put("requestTime", String.valueOf(stopwatch.elapsed(TimeUnit.SECONDS)));
            response.metadata.put("ip", "local");
            return response;
        } catch (IOException e) {
            throw new IOException(String.format("request=[%s] proxy=[LOCAL] error=[%s]", request.url(), e.getMessage()), e);
        }
    }
}
