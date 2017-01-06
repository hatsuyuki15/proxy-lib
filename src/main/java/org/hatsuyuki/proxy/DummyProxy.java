package org.hatsuyuki.proxy;

import org.jsoup.Connection;

/**
 * Created by Hatsuyuki on 12/27/2016.
 */
public class DummyProxy extends AbstractProxy {
    private Pipeline pipeline = new LocalRequester();

    @Override
    public Response request(Connection jsoupConnection) throws Exception {
        Request request = new Request(jsoupConnection);
        request.source = "local";
        return pipeline.forward(request);
    }
}
