package org.hatsuyuki.proxy;

import java.io.IOException;
import java.util.*;

/**
 * Created by Hatsuyuki.
 */
public class BalanceSplitter extends Splitter{
    private List<Pipeline> pipelines = new ArrayList<>();
    private Map<Pipeline, Integer> activeConnectionCountMap = new HashMap<>();
    private Map<Pipeline, Date> lastUsedTimeMap = new HashMap<>();

    @Override
    public Response forward(Request request) throws IOException {
        Pipeline pipeline;
        synchronized (activeConnectionCountMap) {
            pipeline = selectPipeline(request);
            int activeConnection = activeConnectionCountMap.get(pipeline);
            activeConnection += 1;
            activeConnectionCountMap.put(pipeline, activeConnection);
            lastUsedTimeMap.put(pipeline, new Date());
        }
        Response response = pipeline.forward(request);
        synchronized (activeConnectionCountMap) {
            int activeConnection = activeConnectionCountMap.get(pipeline);
            activeConnection -= 1;
            activeConnectionCountMap.put(pipeline, activeConnection);
        }
        return response;
    }

    @Override
    protected Pipeline selectPipeline(Request request) {
        return pipelines.stream()
                .min((pipe1, pipe2) -> {
                    int activeConnectionsOfPipe1 = activeConnectionCountMap.get(pipe1);
                    int activeConnectionsOfPipe2 = activeConnectionCountMap.get(pipe2);
                    if (activeConnectionsOfPipe1 != activeConnectionsOfPipe2) {
                        // find one with least connections
                        return Integer.compare(activeConnectionsOfPipe1, activeConnectionsOfPipe2);
                    } else {
                        // tie-breaker: find one with oldest usage
                        Date lastUsedTimeOfPipe1 = lastUsedTimeMap.get(pipe1);
                        Date lastUsedTimeOfPipe2 = lastUsedTimeMap.get(pipe2);
                        return Long.compare(lastUsedTimeOfPipe1.getTime(), lastUsedTimeOfPipe2.getTime());
                    }
                }).get();
    }

    public void addPipeline(Pipeline pipeline) {
        pipelines.add(pipeline);
        activeConnectionCountMap.put(pipeline, 0);
        lastUsedTimeMap.put(pipeline, new Date());
    }
}
