package org.hatsuyuki.proxy;

import org.jsoup.Connection;

/**
 * Created by Hatsuyuki on 12/27/2016.
 */
public abstract class Proxy {
    public Response request(Connection jsoupConnection) throws Exception {
        return request(jsoupConnection, 600000);
    }

    public abstract Response request(Connection jsoupConnection, int timeout) throws Exception;
}
