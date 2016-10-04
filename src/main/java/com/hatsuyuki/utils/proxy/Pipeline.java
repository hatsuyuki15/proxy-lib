package com.hatsuyuki.utils.proxy;

import java.io.IOException;

/**
 * Created by Hatsuyuki.
 */
public abstract class Pipeline {
    protected Pipeline nextPipeline;

    public Pipeline(Pipeline nextPipeline) {
        this.nextPipeline = nextPipeline;
    }

    public abstract Response forward(Request request) throws IOException;
}
