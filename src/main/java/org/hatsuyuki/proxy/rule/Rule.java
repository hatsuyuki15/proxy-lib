package org.hatsuyuki.proxy.rule;

import org.hatsuyuki.proxy.Request;

/**
 * Created by Hatsuyuki.
 */
public abstract class Rule {
    public abstract boolean match(Request request);
}
