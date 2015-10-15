package com.victor.spider.core.handler;

public abstract class PatternProcessor extends PatternRequestMatcher implements SubPipeline, SubPageProcessor {
    /**
     * @param pattern url pattern to handle
     */
    public PatternProcessor(String pattern) {
        super(pattern);
    }
}
