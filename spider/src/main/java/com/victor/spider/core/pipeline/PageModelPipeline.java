package com.victor.spider.core.pipeline;

import com.victor.spider.core.Task;

/**
 * Implements PageModelPipeline to persistent your page model.
 */
public interface PageModelPipeline<T> {

    public void process(T t, Task task);

}
