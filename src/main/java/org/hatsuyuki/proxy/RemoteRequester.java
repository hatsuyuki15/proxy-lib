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
public class RemoteRequester extends Requester {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final int maxNumOfRetry;

    private final String proxyHost;
    private final int proxyPort;

    public RemoteRequester(String proxyHost, int proxyPort, int maxNumOfRetry) {
        super(null);
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.maxNumOfRetry = maxNumOfRetry;
    }

    public RemoteRequester(String proxyHost, int proxyPort) {
        this(proxyHost, proxyPort, 0);
    }

    public RemoteRequester(String proxyHost, int proxyPort, String username, String password, int maxNumOfRetry) {
        super(null);
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.maxNumOfRetry = maxNumOfRetry;
        ProxyAuthenticator.getInstance().addAuthentication(proxyHost, proxyPort, username, password);
    }

    public RemoteRequester(String proxyHost, int proxyPort, String username, String password) {
        this(proxyHost, proxyPort, username, password, 0);
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

        try {
            logger.debug(String.format("request=[%s] proxy=[%s:%d]", request.url(), this.proxyHost, this.proxyPort));
            return new Response(jsoupConnection.execute());
        } catch (IOException e) {
            throw new IOException(String.format("request=[%s] proxy=[%s:%d] error=[%s]", request.url(), this.proxyHost, this.proxyPort, e.getMessage()), e);
        }
    }
}