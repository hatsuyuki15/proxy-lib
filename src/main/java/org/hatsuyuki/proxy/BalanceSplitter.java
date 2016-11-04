package org.hatsuyuki.proxy;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Hatsuyuki.
 */
public class BalanceSplitter extends Splitter{
    private Map<Pipeline, Integer> activeConnectionsPerPipeline = new HashMap<>();

    @Override
    public Response forward(Request request) throws IOException {
        Pipeline pipeline;
        synchronized (activeConnectionsPerPipeline) {
            pipeline = selectPipeline(request);
            int activeConnection = activeConnectionsPerPipeline.get(pipeline);
            activeConnection += 1;
            activeConnectionsPerPipeline.put(pipeline, activeConnection);
        }
        Response response = pipeline.forward(request);
        synchronized (activeConnectionsPerPipeline) {
            int activeConnection = activeConnectionsPerPipeline.get(pipeline);
            activeConnection -= 1;
            activeConnectionsPerPipeline.put(pipeline, activeConnection);
        }
        return response;
    }

    @Override
    protected Pipeline selectPipeline(Request request) {
        int minActiveConnection = Collections.min(activeConnectionsPerPipeline.values());
        for (Pipeline pipeline: activeConnectionsPerPipeline.keySet()) {
            int activeConnection = activeConnectionsPerPipeline.get(pipeline);
            if (activeConnection == minActiveConnection) {
                return pipeline;
            }
        }
        return null;
    }

    public void addPipeline(Pipeline pipeline) {
        activeConnectionsPerPipeline.put(pipeline, 0);
    }
}
