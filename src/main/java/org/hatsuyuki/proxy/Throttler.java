package org.hatsuyuki.proxy;

/**
 * Created by Hatsuyuki on 12/15/2016.
 */
public abstract class Throttler extends Pipeline {
    public Throttler(Pipeline nextPipeline) {
        super(nextPipeline);
    }
}
