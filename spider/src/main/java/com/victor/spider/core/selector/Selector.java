package com.victor.spider.core.selector;

import java.util.List;

/**
 * Selector(extractor) for text.
 */
public interface Selector {

    /**
     * Extract single result in text.<br>
     * If there are more than one result, only the first will be chosen.
     *
     * @param text
     * @return result
     */
    public String select(String text);

    /**
     * Extract all results in text.<br>
     *
     * @param text
     * @return results
     */
    public List<String> selectList(String text);

}
