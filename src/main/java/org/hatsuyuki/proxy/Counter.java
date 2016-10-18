package org.hatsuyuki.proxy;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Hatsuyuki.
 */
public class Counter extends Pipeline {
    private final AtomicInteger count = new AtomicInteger();

    public Counter(Pipeline nextPipeline) {
        super(nextPipeline);
    }

    public Response forward(Request request) throws IOException {
        count.getAndIncrement();
        return this.nextPipeline.forward(request);
    }

    public int getCount() {
        return count.get();
    }
}
