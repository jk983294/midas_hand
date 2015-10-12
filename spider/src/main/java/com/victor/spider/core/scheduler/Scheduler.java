package com.victor.spider.core.scheduler;


import com.victor.spider.core.Request;
import com.victor.spider.core.Task;

/**
 * Scheduler is the part of url management.<br>
 * You can implement interface Scheduler to do:
 * manage urls to fetch
 * remove duplicate urls
 */
public interface Scheduler {

    /**
     * add a url to fetch
     *
     * @param request
     * @param task
     */
    public void push(Request request, Task task);

    /**
     * get an url to crawl
     *
     * @param task the task of spider
     * @return the url to crawl
     */
    public Request poll(Task task);

}
