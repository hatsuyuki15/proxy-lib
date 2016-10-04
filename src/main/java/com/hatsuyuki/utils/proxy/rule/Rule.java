package com.hatsuyuki.utils.proxy.rule;

import com.hatsuyuki.utils.proxy.Request;

/**
 * Created by Hatsuyuki.
 */
public abstract class Rule {
    public abstract boolean match(Request request);
}
