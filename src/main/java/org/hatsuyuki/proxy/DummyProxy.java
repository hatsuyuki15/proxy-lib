package org.hatsuyuki.proxy;

import org.hatsuyuki.proxy.exception.NetworkError;
import org.jsoup.Connection;

/**
 * Created by Hatsuyuki on 12/27/2016.
 */
public class DummyProxy extends Proxy {
    private Pipeline pipeline = new LocalRequester();

    @Override
    public Response request(Connection jsoupConnection) throws Exception {
        Request request = new Request(jsoupConnection);
        request.source = "local";
        Response response = pipeline.forward(request);
        response.request(request);
        return response;
    }
}
