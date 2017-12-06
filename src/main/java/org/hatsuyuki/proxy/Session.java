package org.hatsuyuki.proxy;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Session extends Splitter {
    private Pipeline defaultPipeline;
    private List<Pipeline> pipelines;
    private Random random;

    private LoadingCache<Long, Pipeline> sessions;

    public Session(Pipeline defaultPipeline, int concurrency, long expire, TimeUnit timeUnit) {
        this.defaultPipeline = defaultPipeline;
        this.pipelines = new ArrayList<>();
        this.sessions = CacheBuilder.newBuilder()
                .concurrencyLevel(concurrency)
                .expireAfterAccess(expire, timeUnit)
                .build(new CacheLoader<Long, Pipeline>() {
                    @Override
                    public Pipeline load(Long key) throws Exception {
                        return randomPipeline();
                    }
                });
        this.random = new Random();
    }



    @Override
    protected Pipeline selectPipeline(Request request) {
        if (request.sessionId() == null) {
            return defaultPipeline;
        }

        try {
            return sessions.get(request.sessionId());
        } catch (ExecutionException e) {
            return null;
        }
    }

    public Session addPipeline(Pipeline pipeline) {
        pipelines.add(pipeline);
        return this;
    }

    private Pipeline randomPipeline() {
        List<Pipeline> available = new ArrayList<>(pipelines);
        available.removeAll(sessions.asMap().values());
        int index = random.nextInt(available.size());
        return available.get(index);
    }
}
