package org.hatsuyuki.proxy.pool;

import org.hatsuyuki.proxy.Pipeline;
import org.hatsuyuki.proxy.Request;
import org.hatsuyuki.proxy.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Hatsuyuki on 2017/04/30.
 */
public class RotationSelectionAlgorithm implements SelectionAlgorithm {
    private List<Pipeline> allPipelines = new ArrayList<>();
    private int currentIndex;

    @Override
    public void onPipelineAdded(Pipeline pipeline) {
        allPipelines.add(pipeline);
    }

    @Override
    public void onResponseReceive(Request request, Response response, Pipeline pipelineUsed) {
    }

    @Override
    public synchronized Pipeline selectBestPipeline(Set<Pipeline> pipelines) {
        if (pipelines.isEmpty()) {
            throw new RuntimeException("Out of pipelines for this pool");
        }

        Pipeline bestPipeline;
        while (true) {
            currentIndex = (currentIndex + 1) % allPipelines.size();
            Pipeline currentPipeline = allPipelines.get(currentIndex);
            if (pipelines.contains(currentPipeline)) {
                bestPipeline = currentPipeline;
                break;
            }
        }
        return bestPipeline;
    }
}
