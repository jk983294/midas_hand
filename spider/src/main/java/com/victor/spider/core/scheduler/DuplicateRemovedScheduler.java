package com.victor.spider.core.scheduler;

import com.victor.spider.core.Request;
import com.victor.spider.core.Task;
import com.victor.spider.core.scheduler.component.DuplicateRemover;
import com.victor.spider.core.scheduler.component.HashSetDuplicateRemover;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Remove duplicate urls and only push urls which are not duplicate.<br></br>
 */
public abstract class DuplicateRemovedScheduler implements Scheduler {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private DuplicateRemover duplicatedRemover = new HashSetDuplicateRemover();

    public DuplicateRemover getDuplicateRemover() {
        return duplicatedRemover;
    }

    public DuplicateRemovedScheduler setDuplicateRemover(DuplicateRemover duplicatedRemover) {
        this.duplicatedRemover = duplicatedRemover;
        return this;
    }

    @Override
    public void push(Request request, Task task) {
        logger.trace("get a candidate url {}", request.getUrl());
        if (!duplicatedRemover.isDuplicate(request, task) || shouldReserved(request)) {
            pushWhenNoDuplicate(request, task);
        }
    }

    protected boolean shouldReserved(Request request) {
        return request.getExtra(Request.CYCLE_TRIED_TIMES) != null;
    }

    protected void pushWhenNoDuplicate(Request request, Task task) {

    }
}
