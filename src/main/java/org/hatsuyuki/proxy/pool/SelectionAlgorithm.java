package org.hatsuyuki.proxy.pool;

import org.hatsuyuki.proxy.Pipeline;
import org.hatsuyuki.proxy.Request;
import org.hatsuyuki.proxy.Response;

import java.util.Set;

/**
 * Created by Hatsuyuki on 2017/04/30.
 */
public interface SelectionAlgorithm {
    void onPipelineAdded(Pipeline pipeline);
    void onResponseReceive(Request request, Response response, Pipeline pipelineUsed);
    Pipeline selectBestPipeline(Set<Pipeline> pipelines);
}
