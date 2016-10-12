package org.hatsuyuki.proxy;

import java.io.IOException;

/**
 * Created by Hatsuyuki.
 */
public class Counter extends Pipeline{

    private volatile int count = 0;

    public Counter(Pipeline nextPipeline) {
        super(nextPipeline);
    }

    @Override
    public Response forward(Request request) throws IOException {
        count += 1;
        return nextPipeline.forward(request);
    }

    public int getCount() {
        return count;
    }

}
