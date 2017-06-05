package com.victor.spider.core.pipeline;

import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.digest.DigestUtils;
import com.victor.spider.core.ResultItems;
import com.victor.spider.core.Task;
import com.victor.spider.core.utils.FilePersistentBase;
import org.apache.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Store results to files in JSON format.<br>
 */
public class JsonFilePipeline extends FilePersistentBase implements Pipeline {

    private Logger logger = Logger.getLogger(getClass());

    /**
     * new JsonFilePageModelPipeline with default path "/data/spider/"
     */
    public JsonFilePipeline() {
        setPath("/data/spider");
    }

    public JsonFilePipeline(String path) {
        setPath(path);
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        String path = this.path + PATH_SEPERATOR + task.getUUID() + PATH_SEPERATOR;
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(getFile(path + DigestUtils.md5Hex(resultItems.getRequest().getUrl()) + ".json")));
            printWriter.write(JSON.toJSONString(resultItems.getAll()));
            printWriter.close();
        } catch (IOException e) {
            logger.warn("write file error", e);
        }
    }
}
