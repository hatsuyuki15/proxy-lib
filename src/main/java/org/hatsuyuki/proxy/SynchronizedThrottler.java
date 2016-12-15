package org.hatsuyuki.proxy;

import java.io.IOException;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by Hatsuyuki on 12/15/2016.
 */
public class SynchronizedThrottler extends Throttler {
    private LinkedTransferQueue<Request> queue = new LinkedTransferQueue<>();
    private QueueProcessor queueProcessor = new QueueProcessor();
    private int maxRequestsPerInterval;
    private long interval;

    public SynchronizedThrottler(Pipeline nextPipeline, int maxRequestsPerInterval, long interval, TimeUnit timeUnit) {
        super(nextPipeline);
        this.maxRequestsPerInterval = maxRequestsPerInterval;
        this.interval = TimeUnit.MILLISECONDS.convert(interval, timeUnit);

        queueProcessor.start();
    }

    @Override
    public Response forward(Request request) throws IOException {
        queue.offer(request);
        synchronized (request) {
            try {
                request.wait();
            } catch (InterruptedException ignored) {}
        }
        return nextPipeline.forward(request);
    }

    private class QueueProcessor extends Thread {
        private boolean active = true;

        @Override
        public void run() {
            while (active) {
                try {
                    Thread.sleep(interval);
                } catch (InterruptedException ignored) {}

                for (int i = 0; i < maxRequestsPerInterval; i++) {
                    Request request = queue.poll();
                    if (request != null) {
                        synchronized (request) {
                            request.notify();
                        }
                    }
                }
            }
        }
    }
}
