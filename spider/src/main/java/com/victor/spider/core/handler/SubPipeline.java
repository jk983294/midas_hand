package com.victor.spider.core.handler;

import com.victor.spider.core.ResultItems;
import com.victor.spider.core.Task;

public interface SubPipeline extends RequestMatcher {

    /**
     * process the page, extract urls to fetch, extract the data and store
     *
     * @param task
     * @return whether continue to match
     */
    public MatchOther processResult(ResultItems resultItems, Task task);

}
