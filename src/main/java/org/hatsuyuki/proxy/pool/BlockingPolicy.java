package org.hatsuyuki.proxy.pool;

import org.hatsuyuki.proxy.Response;

/**
 * Created by Hatsuyuki on 2017/04/30.
 */
public interface BlockingPolicy {
    long getBlockingDuration();
    boolean match(Response response);
}

