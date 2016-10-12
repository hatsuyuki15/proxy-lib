package org.hatsuyuki.proxy;

import java.io.IOException;

/**
 * Created by Hatsuyuki.
 */
public class Delayer extends Pipeline {
    long milliseconds;

    public Delayer(Pipeline nextPipeline, long milliseconds) {
        super(nextPipeline);
        this.milliseconds = milliseconds;
    }

    public Response forward(Request request) throws IOException {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ignored) {}
        return nextPipeline.forward(request);
    }
}
