package com.victor.midas.crawl.model;

import com.victor.spider.core.selector.Selectable;
import com.victor.utilities.utils.RegExpHelper;

import java.sql.Timestamp;

/**
 * gu ba topic
 */
public class GuBaTopic {

    private String user;

    private Timestamp time;

    private String title;

    private String content;

    private String stockCode;

    private String topicId;

    public static GuBaTopic generate(Selectable selectable, GuBaUrlInfo urlInfo){
        if(selectable == null) return null;
        GuBaTopic topic = new GuBaTopic();
        topic.user = selectable.xpath("/div/div[@id=\"zwcontt\"]/div[@id=\"zwconttb\"]//span[@class=\"gray\"]/text()").toString();
        topic.time = Timestamp.valueOf(RegExpHelper.extractTimeStr(selectable.xpath("/div/div[@id=\"zwcontt\"]/div[@id=\"zwconttb\"]/div[@class=\"zwfbtime\"]/text()").toString()));
        topic.title = selectable.xpath("/div/div[@class=\"zwcontentmain\"]/div[@id=\"zwconttbt\"]/text()").toString();
        topic.content = selectable.xpath("/div/div[@class=\"zwcontentmain\"]/div[@id=\"zwconbody\"]/div/text()").toString();
        if(urlInfo != null){
            topic.stockCode = urlInfo.getStockCode();
            topic.topicId = urlInfo.getPageId();
        }
        return topic;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }
}
