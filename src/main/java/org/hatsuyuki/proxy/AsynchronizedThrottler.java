package org.hatsuyuki.proxy;

import com.google.common.collect.EvictingQueue;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Hatsuyuki on 12/15/2016.
 */
public class AsynchronizedThrottler extends Throttler {
    private EvictingQueue<Date> lastRequestDateQueue;
    private int maxRequestsPerInterval;
    private long interval;

    public AsynchronizedThrottler(Pipeline nextPipeline, int maxRequestsPerInterval, long interval, TimeUnit timeUnit) {
        super(nextPipeline);
        this.maxRequestsPerInterval = maxRequestsPerInterval;
        this.interval = TimeUnit.MILLISECONDS.convert(interval, timeUnit);
        this.lastRequestDateQueue = EvictingQueue.create(maxRequestsPerInterval);
    }

    @Override
    public Response forward(Request request) throws IOException {
        long waitTime;
        synchronized (this) {
            Date now = new Date();
            Date requestDate = lastRequestDateQueue.peek();
            if (requestDate == null) {
                waitTime = 0;
            } else {
                long passageTime = now.getTime() - requestDate.getTime();
                waitTime = passageTime > interval
                         ? 0
                         : interval - passageTime;
            }
            Date scheduledDate = new Date(now.getTime() + waitTime);
            lastRequestDateQueue.offer(scheduledDate);
        }

        if (waitTime > 0) {
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException ignored) {}
        }

        return nextPipeline.forward(request);
    }
}
