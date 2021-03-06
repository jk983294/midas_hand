package com.victor.spider.core.pipeline;

import com.victor.spider.core.ResultItems;
import com.victor.spider.core.Task;

/**
 * Pipeline is the persistent and offline process part of crawler.<br>
 * The interface Pipeline can be implemented to customize ways of persistent.
 * @see ConsolePipeline
 * @see FilePipeline
 */
public interface Pipeline {

    /**
     * Process extracted results.
     *
     * @param resultItems
     * @param task
     */
    public void process(ResultItems resultItems, Task task);
}
