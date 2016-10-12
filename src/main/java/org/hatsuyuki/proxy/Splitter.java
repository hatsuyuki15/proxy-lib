package org.hatsuyuki.proxy;

import java.io.IOException;

/**
 * Created by Hatsuyuki.
 */
public abstract class Splitter extends Pipeline {

    public Splitter() {
        super(null);
    }

    @Override
    public Response forward(Request request) throws IOException {
        Pipeline pipeline = selectPipeline(request);
        if (pipeline != null) {
            return pipeline.forward(request);
        } else {
            throw new IOException("Request was rejected since splitter could not find a suitable pipeline for it.");
        }
    }

    protected abstract Pipeline selectPipeline(Request request);

}
