package org.hatsuyuki.proxy;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Hatsuyuki on 2017/02/06.
 */
public class Stretcher extends Pipeline {
    private long milliseconds;

    public Stretcher(Pipeline nextPipeline, long time, TimeUnit timeUnit) {
        super(nextPipeline);
        this.milliseconds = TimeUnit.MILLISECONDS.convert(time, timeUnit);
    }

    @Override
    public Response forward(Request request) throws IOException {
        synchronized (this) {
            try {
                Thread.sleep(milliseconds);
            } catch (InterruptedException ignored) {}
            return nextPipeline.forward(request);
        }
    }
}
