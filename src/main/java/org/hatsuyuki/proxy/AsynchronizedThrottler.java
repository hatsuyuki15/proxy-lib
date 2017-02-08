package org.hatsuyuki.proxy;

import com.google.common.collect.EvictingQueue;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Hatsuyuki on 12/15/2016.
 */
public class AsynchronizedThrottler extends Throttler {
    private EvictingQueue<Date> requestDateHistory;
    private long interval;

    public AsynchronizedThrottler(Pipeline nextPipeline, int maxRequestsPerInterval, long interval, TimeUnit timeUnit) {
        super(nextPipeline);
        this.interval = TimeUnit.MILLISECONDS.convert(interval, timeUnit);
        this.requestDateHistory = EvictingQueue.create(maxRequestsPerInterval); // only keep k (=maxRequestsPerInterval) most recent request dates
    }

    @Override
    public Response forward(Request request) throws IOException {
        long waitTime;
        synchronized (this) {
            Date now = new Date();
            Date oldestRequestDate = requestDateHistory.peek();
            if (oldestRequestDate == null) {
                waitTime = 0;
            } else {
                long elapsedTime = now.getTime() - oldestRequestDate.getTime();
                waitTime = elapsedTime > interval
                         ? 0
                         : interval - elapsedTime;
            }
            Date scheduledDate = new Date(now.getTime() + waitTime);
            requestDateHistory.offer(scheduledDate);
        }

        if (waitTime > 0) {
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException ignored) {}
        }

        return nextPipeline.forward(request);
    }
}
