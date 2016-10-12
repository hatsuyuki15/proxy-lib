package org.hatsuyuki.proxy;

/**
 * Created by Hatsuyuki.
 */
public abstract class Requester extends Pipeline {
    public Requester(Pipeline nextPipeline) {
        super(nextPipeline);
    }
}
