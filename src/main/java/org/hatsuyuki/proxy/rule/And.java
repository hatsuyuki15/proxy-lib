package org.hatsuyuki.proxy.rule;

import org.hatsuyuki.proxy.Request;

/**
 * Created by Hatsuyuki.
 */
public class And extends Rule {
    private Rule rule1;
    private Rule rule2;

    public And(Rule rule1, Rule rule2) {
        this.rule1 = rule1;
        this.rule2 = rule2;
    }

    @Override
    public boolean match(Request request) {
        return rule1.match(request) && rule2.match(request);
    }
}
