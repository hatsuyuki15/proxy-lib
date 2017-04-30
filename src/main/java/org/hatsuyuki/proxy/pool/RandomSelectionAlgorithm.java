package org.hatsuyuki.proxy.pool;

import org.hatsuyuki.proxy.Pipeline;
import org.hatsuyuki.proxy.Request;
import org.hatsuyuki.proxy.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by Hatsuyuki on 2017/04/30.
 */
public class RandomSelectionAlgorithm implements SelectionAlgorithm {
    private Random random = new Random();
    private List<Pipeline> allPipelines = new ArrayList<>();

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
            throw new RuntimeException("Out of available pipelines for this socket");
        }

        int randomIndex = random.nextInt(pipelines.size());
        for (Pipeline pipeline: allPipelines) {
            if (pipelines.contains(pipeline)) {
                if (randomIndex == 0) {
                    return pipeline;
                }
                randomIndex -= 1;
            }
        }
        throw new RuntimeException("Something strange happened. It should not be able to reach this exception");
    }
}
