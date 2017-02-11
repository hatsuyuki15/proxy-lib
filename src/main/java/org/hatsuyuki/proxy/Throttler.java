package org.hatsuyuki.proxy;

import com.google.common.collect.EvictingQueue;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Hatsuyuki on 12/15/2016.
 */
public class Throttler extends Pipeline {
    private EvictingQueue<Date> requestDateHistory;
    private long interval;
    private long timeout;
    private long maxRequestsPerInterval;

    public Throttler(Pipeline nextPipeline, int maxRequestsPerInterval, long interval, long timeout, TimeUnit timeUnit) {
        super(nextPipeline);
        this.maxRequestsPerInterval = maxRequestsPerInterval;
        this.timeout = TimeUnit.MILLISECONDS.convert(timeout, timeUnit);
        this.interval = TimeUnit.MILLISECONDS.convert(interval, timeUnit);
        this.requestDateHistory = EvictingQueue.create(maxRequestsPerInterval); // only keep k (=maxRequestsPerInterval) most recent request dates
    }

    public Throttler(Pipeline nextPipeline, int maxRequestsPerInterval, long interval, TimeUnit timeUnit) {
        this(nextPipeline, maxRequestsPerInterval, interval, Long.MAX_VALUE, timeUnit);
    }

    @Override
    public Response forward(Request request) throws IOException {
        long waitTime;
        synchronized (this) {
            Date now = new Date();

            // compute waitTime
            if (requestDateHistory.size() < maxRequestsPerInterval) {
                waitTime = 0;
            } else {
                Date oldestRequestDate = requestDateHistory.peek();
                long elapsedTime = now.getTime() - oldestRequestDate.getTime();
                waitTime = elapsedTime > interval
                         ? 0
                         : interval - elapsedTime;
            }

            // drop if waitTime exceed limit
            if (waitTime > timeout) {
                throw new IOException("Exceed timeout. Request was dropped by throttler");
            }

            // schedule executed time
            Date scheduledDate = new Date(now.getTime() + waitTime);
            requestDateHistory.offer(scheduledDate);
        }

        // stand by till scheduled time
        if (waitTime > 0) {
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException ignored) {}
        }

        return nextPipeline.forward(request);
    }
}
