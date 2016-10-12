package org.hatsuyuki.proxy.rule;

import org.hatsuyuki.proxy.Request;

import java.util.Objects;

/**
 * Created by Hatsuyuki.
 */
public class SourceMatching extends Rule {
    private String source;

    public SourceMatching(String source) {
        this.source = source;
    }

    @Override
    public boolean match(Request request) {
        return Objects.equals(this.source, request.source);
    }
}
