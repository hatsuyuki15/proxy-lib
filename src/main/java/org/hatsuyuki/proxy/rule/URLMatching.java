package org.hatsuyuki.proxy.rule;

import org.hatsuyuki.proxy.Request;

import java.util.regex.Pattern;

/**
 * Created by Hatsuyuki.
 */
public class URLMatching extends Rule {
    private final Pattern pattern;

    public URLMatching(Pattern pattern) {
        this.pattern = pattern;
    }

    public URLMatching(String sPattern) {
        this.pattern = Pattern.compile(sPattern);
    }

    @Override
    public boolean match(Request request) {
        return pattern.matcher(request.url).matches();
    }
}
