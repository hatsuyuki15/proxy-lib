package org.hatsuyuki.proxy.pool;

import org.hatsuyuki.proxy.Response;

/**
 * Created by Hatsuyuki on 2017/04/30.
 */
public class NonBlockingPolicy implements BlockingPolicy {
    @Override
    public long getBlockingDuration() {
        return 0;
    }

    @Override
    public boolean match(Response response) {
        return false;
    }
}
