package org.hatsuyuki.proxy;

import java.io.IOException;
import java.util.PriorityQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Hatsuyuki.
 */
public class PriorityBuffer extends Pipeline{

    private PriorityQueue<Request> queue;
    private final int bufferSize;
    private int bufferCount;
    private Lock processingQueue = new ReentrantLock();

    public PriorityBuffer(Pipeline nextPipeline, int bufferSize) {
        super(nextPipeline);
        this.bufferSize = bufferSize;
        this.queue = new PriorityQueue<>((o1, o2) -> Integer.compare(o2.priority, o1.priority));
    }

    @Override
    public Response forward(Request request) throws IOException {
        waitTillBeProcessed(request);
        try {
            return nextPipeline.forward(request);
        } catch (IOException e) {
            throw e;
        } finally {
            processQueue();
        }
    }

    private void waitTillBeProcessed(Request request) {
        processingQueue.lock();
        if (bufferCount < bufferSize) {
            bufferCount += 1;
            processingQueue.unlock();
        } else {
            synchronized (request) {
                try {
                    queue.add(request);
                    processingQueue.unlock();
                    request.wait();
                } catch (InterruptedException ignored) {}
            }
        }
    }

    private void processQueue() {
        processingQueue.lock();
        Request request = queue.peek();
        if (request == null) {
            bufferCount -= 1;
            processingQueue.unlock();
        } else {
            synchronized (request) {
                queue.remove(request);
                processingQueue.unlock();
                request.notify();
            }
        }
    }
}
