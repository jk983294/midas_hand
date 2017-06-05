package com.victor.spider.core.pipeline;

import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import com.victor.spider.core.Task;
import com.victor.spider.core.model.HasKey;
import com.victor.spider.core.utils.FilePersistentBase;
import org.apache.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Store results objects (page models) to files in JSON format.<br>
 * Use model.getKey() as file name if the model implements HasKey.<br>
 * Otherwise use SHA1 as file name.
 */
public class JsonFilePageModelPipeline extends FilePersistentBase implements PageModelPipeline {

    private Logger logger = Logger.getLogger(getClass());

    /**
     * new JsonFilePageModelPipeline with default path "/data/spider/"
     */
    public JsonFilePageModelPipeline() {
        setPath("/data/spider/");
    }

    public JsonFilePageModelPipeline(String path) {
        setPath(path);
    }

    @Override
    public void process(Object o, Task task) {
        String path = this.path + PATH_SEPERATOR + task.getUUID() + PATH_SEPERATOR;
        try {
            String filename;
            if (o instanceof HasKey) {
                filename = path + ((HasKey) o).key() + ".json";
            } else {
                filename = path + DigestUtils.md5Hex(ToStringBuilder.reflectionToString(o)) + ".json";
            }
            PrintWriter printWriter = new PrintWriter(new FileWriter(getFile(filename)));
            printWriter.write(JSON.toJSONString(o));
            printWriter.close();
        } catch (IOException e) {
            logger.warn("write file error", e);
        }
    }
}
