package org.hatsuyuki.proxy;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Hatsuyuki.
 */
public class Buffer extends Pipeline {
    private LinkedBlockingQueue<Request> queue;

    public Buffer(Pipeline nextPipeline, int maxSize) {
        super(nextPipeline);
        this.queue = new LinkedBlockingQueue<>(maxSize);
    }

    @Override
    public Response forward(Request request) throws IOException {
        try {
            queue.put(request);
            return nextPipeline.forward(request);
        } catch (InterruptedException ignored) {
            throw new IOException("Problem adding connection to buffer");
        } finally {
            queue.remove(request);
        }
    }
}
