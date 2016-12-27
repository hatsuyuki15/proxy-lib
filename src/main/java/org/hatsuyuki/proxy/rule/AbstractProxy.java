package org.hatsuyuki.proxy.rule;

import org.hatsuyuki.proxy.Response;
import org.jsoup.Connection;

/**
 * Created by Hatsuyuki on 12/27/2016.
 */
public abstract class AbstractProxy {
    public abstract Response request(Connection jsoupConnection) throws Exception;
}
