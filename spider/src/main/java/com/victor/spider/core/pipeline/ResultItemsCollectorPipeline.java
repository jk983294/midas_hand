package com.victor.spider.core.pipeline;

import com.victor.spider.core.ResultItems;
import com.victor.spider.core.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * list as container
 */
public class ResultItemsCollectorPipeline implements CollectorPipeline<ResultItems> {

    private List<ResultItems> collector = new ArrayList<>();

    @Override
    public synchronized void process(ResultItems resultItems, Task task) {
        collector.add(resultItems);
    }

    @Override
    public List<ResultItems> getCollected() {
        return collector;
    }
}
