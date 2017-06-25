package org.hatsuyuki.proxy.pool;

import org.hatsuyuki.proxy.Pipeline;
import org.hatsuyuki.proxy.Request;
import org.hatsuyuki.proxy.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * Created by Hatsuyuki on 2017/04/25.
 */
public class Pool extends Pipeline {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private Set<Pipeline> pipelines = new LinkedHashSet<>();
    private Map<Pipeline, Long> lastUsedTimeMap = new HashMap<>();
    private Set<Pipeline> blockedPipelines = new HashSet<>();
    private BlockingPolicy blockingPolicy = new NonBlockingPolicy();
    private SelectionAlgorithm selectionAlgorithm = new LeastConnectionAndUsedTimeSelection();

    public Pool() {
        super(null);
    }

    public void addPipeline(Pipeline pipeline) {
        pipelines.add(pipeline);
        selectionAlgorithm.onPipelineAdded(pipeline);
    }

    public void setBlockingPolicy(BlockingPolicy blockingPolicy) {
        this.blockingPolicy = blockingPolicy;
    }

    public void setSelectionAlgorithm(SelectionAlgorithm selectionAlgorithm) {
        this.selectionAlgorithm = selectionAlgorithm;
        for (Pipeline pipeline: pipelines) {
            selectionAlgorithm.onPipelineAdded(pipeline);
        }
    }

    @Override
    public Response forward(Request request) throws IOException {
        Set<Pipeline> availablePipelines = getAvailablePipelines();
        if (availablePipelines.isEmpty()) {
            throw new IOException("No available pipelines at the moment. Possibly, all of them are being blocked.");
        }

        Pipeline bestPipeline = selectionAlgorithm.selectBestPipeline(availablePipelines);
        if (bestPipeline == null) {
            throw new IOException("No pipeline was selected. Something was wrong with the selection algorithm. Contact the author for more detail.");
        }

        Response response = bestPipeline.forward(request);
        selectionAlgorithm.onResponseReceive(request, response, bestPipeline);
        checkPolicyAndBlock(bestPipeline, response);
        return response;
    }

    private Set<Pipeline> getAvailablePipelines() {
        synchronized (blockedPipelines) {
            // unblock timeout pipelines
            blockedPipelines.removeIf(pipe -> {
                long now = new Date().getTime();
                long lastUsedTime = getLastUsedTime(pipe);
                long elapsedTime = now - lastUsedTime;
                return elapsedTime >= blockingPolicy.getBlockingDuration();
            });

            Set<Pipeline> availablePipelines = new HashSet<>(pipelines);
            availablePipelines.removeAll(blockedPipelines);
            return availablePipelines;
        }
    }

    private void checkPolicyAndBlock(Pipeline pipeline, Response response) {
        synchronized (blockedPipelines) {
            updateLastUsedTime(pipeline);
            if (blockingPolicy.match(response)) {
                blockedPipelines.add(pipeline);
            }
        }
    }

    private void updateLastUsedTime(Pipeline pipeline) {
        lastUsedTimeMap.put(pipeline, new Date().getTime());
    }

    private Long getLastUsedTime(Pipeline pipeline) {
        return lastUsedTimeMap.get(pipeline);
    }
}
