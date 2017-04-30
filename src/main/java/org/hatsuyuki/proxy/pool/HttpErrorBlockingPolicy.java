package org.hatsuyuki.proxy.pool;

import org.hatsuyuki.proxy.Response;

import java.util.concurrent.TimeUnit;

/**
 * Created by Hatsuyuki on 2017/04/30.
 */
public class HttpErrorBlockingPolicy implements BlockingPolicy {
    private long blockingDuration;

    public HttpErrorBlockingPolicy(long blockingDuration, TimeUnit timeUnit) {
        this.blockingDuration = TimeUnit.MILLISECONDS.convert(blockingDuration, timeUnit);
    }

    @Override
    public long getBlockingDuration() {
        return blockingDuration;
    }

    @Override
    public boolean match(Response response) {
        return response.statusCode() >= 400 && response.statusCode() < 600;
    }
}
