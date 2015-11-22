package com.victor.midas.crawl.model;

import com.victor.spider.core.selector.Selectable;
import com.victor.utilities.utils.MathHelper;
import com.victor.utilities.utils.RegExpHelper;
import com.victor.utilities.utils.TimeHelper;

import java.sql.Timestamp;

/**
 * gu ba topic
 */
public class GuBaTopic {

    private String authorName;

    private Timestamp time;

    private String title;

    private String content;

    private String stockCode;

    private Long topicId, authorId;

    public static GuBaTopic generate(Selectable selectable, GuBaUrlInfo urlInfo){
        if(selectable == null) return null;
        GuBaTopic topic = new GuBaTopic();
        topic.authorName = selectable.xpath("/div/div[@id=\"zwcontt\"]/div[@id=\"zwconttb\"]//span[@class=\"gray\"]/text()").toString();
        if(topic.authorName == null){
            topic.authorName = selectable.xpath("/div/div[@id=\"zwcontt\"]/div[@id=\"zwconttb\"]/div[@id=\"zwconttbn\"]//a/text()").toString();
            topic.authorId = MathHelper.tryParse2Long(selectable.xpath("/div/div[@id=\"zwcontt\"]/div[@id=\"zwconttb\"]/div[@id=\"zwconttbn\"]//a/@data-popper").toString());
        }
        topic.time = TimeHelper.tryParse2Timestamp(RegExpHelper.extractTimeStr(selectable.xpath("/div/div[@id=\"zwcontt\"]/div[@id=\"zwconttb\"]/div[@class=\"zwfbtime\"]/text()").toString()));
        topic.title = selectable.xpath("/div/div[@class=\"zwcontentmain\"]/div[@id=\"zwconttbt\"]/text()").toString();
        topic.content = selectable.xpath("/div/div[@class=\"zwcontentmain\"]/div[@id=\"zwconbody\"]/div/text()").toString();
        topic.content = topic.content == null ? null : topic.content.trim();
        if(urlInfo != null){
            topic.stockCode = urlInfo.getStockCode();
            topic.topicId = urlInfo.getPageId();
        }
        return topic;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
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

    public Long getTopicId() {
        return topicId;
    }

    public void setTopicId(Long topicId) {
        this.topicId = topicId;
    }
}
