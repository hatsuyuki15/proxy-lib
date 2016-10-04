package com.hatsuyuki.utils.proxy;

import com.hatsuyuki.utils.proxy.rule.Rule;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Hatsuyuki.
 */
public class RuleMatchingSplitter extends Splitter {
    private Map<Rule, Pipeline> pipelineByRule = new LinkedHashMap<>();
    private Pipeline defaultPipeline;

    @Override
    protected Pipeline selectPipeline(Request request) {
        for (Map.Entry<Rule, Pipeline> entry: pipelineByRule.entrySet()) {
            Rule rule = entry.getKey();
            Pipeline pipeline = entry.getValue();
            if (rule.match(request)) {
                return pipeline;
            }
        }

        return defaultPipeline;
    }

    public void addRule(Rule rule, Pipeline pipeline) {
        pipelineByRule.put(rule, pipeline);
    }

    public void addDefaultPipeline(Pipeline pipeline) {
        defaultPipeline = pipeline;
    }
}
