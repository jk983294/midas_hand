package com.victor.spider.app.extension;

import com.victor.spider.core.Site;
import com.victor.spider.core.model.OOSpider;
import com.victor.spider.core.model.annotation.ExtractBy;
import com.victor.spider.core.model.annotation.TargetUrl;
import com.victor.spider.core.pipeline.JsonFilePageModelPipeline;

import java.util.Date;
import java.util.List;

@TargetUrl("http://my.oschina.net/flashsword/blog/\\d+")
public class OschinaBlog {

    @ExtractBy("//title/text()")
    private String title;

    @ExtractBy(value = "div.BlogContent", type = ExtractBy.Type.Css)
    private String content;

    @ExtractBy(value = "//div[@class='BlogTags']/a/text()", multi = true)
    private List<String> tags;

    @ExtractBy("//div[@class='BlogStat']/regex('\\d+-\\d+-\\d+\\s+\\d+:\\d+')")
    private Date date;

    public static void main(String[] args) {
        //results will be saved to "/data/spider/" in json format
        OOSpider.create(Site.me(), new JsonFilePageModelPipeline("/data/spider/"), OschinaBlog.class)
                .addUrl("http://my.oschina.net/flashsword/blog").run();
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public List<String> getTags() {
        return tags;
    }

    public Date getDate() {
        return date;
    }

}
