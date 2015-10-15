package com.victor.spider.core.model;

import com.victor.spider.core.pipeline.PageModelPipeline;
import org.apache.commons.lang3.builder.ToStringBuilder;
import com.victor.spider.core.Task;


/**
 * Print page model in console.<br>
 * Usually used in test.<br>
 */
public class ConsolePageModelPipeline implements PageModelPipeline {
    @Override
    public void process(Object o, Task task) {
        System.out.println(ToStringBuilder.reflectionToString(o));
    }
}
