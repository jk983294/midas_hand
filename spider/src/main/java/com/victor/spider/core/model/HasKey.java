package com.victor.spider.core.model;

import com.victor.spider.core.utils.Experimental;

/**
 * Interface to be implemented by page mode.<br>
 * Can be used to identify a page model, or be used as name of file storing the object.<br>
 */
@Experimental
public interface HasKey {

    /**
     *
     *
     * @return key
     */
    public String key();
}
