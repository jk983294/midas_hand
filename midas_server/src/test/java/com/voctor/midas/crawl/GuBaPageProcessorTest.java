package com.voctor.midas.crawl;

import com.victor.midas.crawl.GuBaPageProcessor;
import com.victor.midas.crawl.model.GuBaPagerInfo;
import com.victor.midas.crawl.model.GuBaUrlInfo;
import com.victor.midas.util.MidasException;
import com.victor.spider.core.Spider;
import com.victor.spider.core.pipeline.JsonFilePipeline;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

public class GuBaPageProcessorTest {

    @Ignore
    @Test
    public void testGuBaPageProcessor() throws MidasException {
        String stockCode = "000702";
//        String url = String.format(MidasConstants.gubaUrlListTemplate, stockCode, 1);
//        Spider.create(new GuBaPageProcessor(stockCode)).addUrl("http://guba.eastmoney.com/list,000702.html").run();
//        Spider.create(new GuBaPageProcessor(stockCode)).addUrl("http://guba.eastmoney.com/news,000702,211665695.html").run();
//        Spider.create(new GuBaPageProcessor(stockCode)).addUrl("http://guba.eastmoney.com/news,601918,201526977_2.html").run();
        Spider.create(new GuBaPageProcessor(stockCode)).addUrl("http://guba.eastmoney.com/news,601766,211862489_1.html")
                .addPipeline(new JsonFilePipeline("D:\\MktData\\guba")).run();
//        Spider.create(new GuBaPageProcessor(stockCode)).addUrl("http://guba.eastmoney.com/news,cjpl,211591648.html").run();
    }

    @Ignore
    @Test
    public void testGuBaPagerInfo() throws MidasException {
        GuBaPagerInfo info;
        List<String> urls;
        info = GuBaPagerInfo.analysis("list,000702_|25292|80|1");
        urls = info.getTargetRequestsList();
        info = GuBaPagerInfo.analysis("news,601766,211862489_|309|30|1");
        urls = info.getTargetRequestsList();
        info = GuBaPagerInfo.analysis("news,cjpl,223920695_|843|30|1");
        urls = info.getTargetRequestsList();
    }

    @Ignore
    @Test
    public void testGuBaUrlInfo() throws MidasException {
        GuBaUrlInfo info;
        info = GuBaUrlInfo.analysis("http://guba.eastmoney.com/list,000702.html");
        info = GuBaUrlInfo.analysis("http://guba.eastmoney.com/list,000702_1.html");
        info = GuBaUrlInfo.analysis("http://guba.eastmoney.com/news,000702,211665695.html");
        info = GuBaUrlInfo.analysis("http://guba.eastmoney.com/news,000702,211665695_1.html");
        info = GuBaUrlInfo.analysis("http://guba.eastmoney.com/news,cjpl,211591648.html");
        info = GuBaUrlInfo.analysis("http://guba.eastmoney.com/news,cjpl,211591648_1.html");
    }
}
