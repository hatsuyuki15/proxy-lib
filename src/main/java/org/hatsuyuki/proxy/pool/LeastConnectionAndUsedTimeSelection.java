package org.hatsuyuki.proxy.pool;

import org.hatsuyuki.proxy.Pipeline;
import org.hatsuyuki.proxy.Request;
import org.hatsuyuki.proxy.Response;

import java.util.*;

/**
 * Created by Hatsuyuki on 2017/04/30.
 *
 * Algorithm: Prefer the pipeline with least active connections
 * If there are more than one pipeline with the same number of active connections
 * Choose the one that has not been used for the longest time
 */

public class LeastConnectionAndUsedTimeSelection implements SelectionAlgorithm {
    private Map<Pipeline, Integer> activeConnectionCountMap = new HashMap<>();
    private Map<Pipeline, Date> lastUsedTimeMap = new HashMap<>();

    @Override
    public void onPipelineAdded(Pipeline pipeline) {
        activeConnectionCountMap.put(pipeline, 0);
        lastUsedTimeMap.put(pipeline, new Date());
    }

    @Override
    public void onResponseReceive(Request request, Response response, Pipeline pipelineUsed) {
        synchronized (activeConnectionCountMap) {
            int activeConnection = activeConnectionCountMap.get(pipelineUsed);
            activeConnection -= 1;
            activeConnectionCountMap.put(pipelineUsed, activeConnection);
        }
    }

    @Override
    public Pipeline selectBestPipeline(Set<Pipeline> pipelines) {
        Pipeline bestPipeline;

        synchronized (activeConnectionCountMap) {
            bestPipeline =  pipelines.stream()
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
                    }).orElse(null);

            // validation
            if (bestPipeline == null) {
                throw new RuntimeException("Out of available pipelines for this pool");
            }

            // update meta data
            int activeConnection = activeConnectionCountMap.get(bestPipeline);
            activeConnection += 1;
            activeConnectionCountMap.put(bestPipeline, activeConnection);
            lastUsedTimeMap.put(bestPipeline, new Date());
        }
        return bestPipeline;
    }
}
