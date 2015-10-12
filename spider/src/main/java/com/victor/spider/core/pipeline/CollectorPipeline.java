package com.victor.spider.core.pipeline;

import java.util.List;

/**
 * Pipeline that can collect and store results. <br>
 * Used for {@link com.victor.spider.core.Spider#getAll(java.util.Collection)}
 */
public interface CollectorPipeline<T> extends Pipeline {

    /**
     * Get all results collected.
     *
     * @return collected results
     */
    public List<T> getCollected();
}
