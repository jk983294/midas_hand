package com.victor.spider.core.scheduler;

import com.victor.spider.core.Task;

/**
 * The scheduler whose requests can be counted for monitor.
 */
public interface MonitorableScheduler extends Scheduler {

    public int getLeftRequestsCount(Task task);

    public int getTotalRequestsCount(Task task);

}