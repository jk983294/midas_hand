package com.victor.spider.core.model;

import com.victor.spider.core.Page;

/**
 * Interface to be implemented by page models that need to do something after fields are extracted.<br>
 */
public interface AfterExtractor {

    public void afterProcess(Page page);
}
