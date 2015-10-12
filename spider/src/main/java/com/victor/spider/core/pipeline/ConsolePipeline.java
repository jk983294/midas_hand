package com.victor.spider.core.pipeline;

import com.victor.spider.core.ResultItems;
import com.victor.spider.core.Task;

import java.util.Map;

/**
 * Write results in console.<br>
 * Usually used in test.
 */
public class ConsolePipeline implements Pipeline {

    @Override
    public void process(ResultItems resultItems, Task task) {
        System.out.println("get page: " + resultItems.getRequest().getUrl());
        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
            System.out.println(entry.getKey() + ":\t" + entry.getValue());
        }
    }
}
