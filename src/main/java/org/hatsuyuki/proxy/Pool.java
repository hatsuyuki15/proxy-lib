package org.hatsuyuki.proxy;

import java.io.IOException;
import java.util.*;

/**
 * Created by Hatsuyuki on 2017/04/25.
 */
public class Pool extends Splitter {
    private Set<Pipeline> pipelines = new LinkedHashSet<>();
    private Map<Pipeline, Integer> activeConnectionCountMap = new HashMap<>();
    private Map<Pipeline, Date> lastUsedTimeMap = new HashMap<>();
    private Set<Pipeline> blockedPipelines = new HashSet<>();
    private BlockingPolicy blockingPolicy = new BlockingPolicy() {
        @Override
        public long getBlockingDuration() {
            return 0;
        }

        @Override
        public boolean match(Response response) {
            return false;
        }
    };

    public void addPipeline(Pipeline pipeline) {
        pipelines.add(pipeline);
        activeConnectionCountMap.put(pipeline, 0);
        lastUsedTimeMap.put(pipeline, new Date());
    }

    public void setBlockingPolicy(BlockingPolicy blockingPolicy) {
        this.blockingPolicy = blockingPolicy;
    }

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

        if (blockingPolicy.match(response)) {
            blockedPipelines.add(pipeline);
        }

        return response;
    }

    @Override
    protected Pipeline selectPipeline(Request request) {
        // unblock
        blockedPipelines.removeIf(pipe -> {
            long now = new Date().getTime();
            long lastUsedTime = lastUsedTimeMap.get(pipe).getTime();
            long elapsedTime = now - lastUsedTime;
            return elapsedTime >= blockingPolicy.getBlockingDuration();
        });

        Pipeline bestPipeline =  pipelines.stream()
                .filter(pipe -> !blockedPipelines.contains(pipe))
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

        if (bestPipeline == null) {
            throw new RuntimeException("Out of available pipelines for this pool");
        }
        return bestPipeline;
    }

    public interface BlockingPolicy {
        long getBlockingDuration();
        boolean match(Response response);
    }

}
