package com.hatsuyuki.utils.proxy.rule;

import com.hatsuyuki.utils.proxy.Request;

/**
 * Created by Hatsuyuki.
 */
public class Or extends Rule{
    private Rule rule1;
    private Rule rule2;

    public Or(Rule rule1, Rule rule2) {
        this.rule1 = rule1;
        this.rule2 = rule2;
    }

    @Override
    public boolean match(Request request) {
        return rule1.match(request) || rule2.match(request);
    }
}
