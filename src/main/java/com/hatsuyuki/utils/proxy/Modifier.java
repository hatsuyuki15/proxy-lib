package com.hatsuyuki.utils.proxy;

import java.io.IOException;

/**
 * Created by Hatsuyuki.
 */
public class Modifier extends Pipeline {
    private Function function;

    public Modifier(Function function, Pipeline nextPipeline) {
        super(nextPipeline);
        this.function = function;
    }

    @Override
    public Response forward(Request request) throws IOException {
        function.modify(request);
        return nextPipeline.forward(request);
    }

    public interface Function {
        void modify(Request request);
    }
}
