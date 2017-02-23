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
        return new Response(jsoupConnection.execute());
    }
}
