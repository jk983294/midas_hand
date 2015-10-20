package com.victor.spider.app.work;

import com.victor.spider.core.Page;
import com.victor.spider.core.Site;
import com.victor.spider.core.Spider;
import com.victor.spider.core.processor.PageProcessor;

import java.util.List;


public class GuBaPageProcessor implements PageProcessor {

    private Site site = Site.me().setDomain("guba.eastmoney.com");

    @Override
    public void process(Page page) {
        String topicNumberString = page.getHtml().css("div.pager/text()").toString();
        List<String> urls = page.getHtml().css("span.pagernums").links().all();
        urls = page.getHtml().css("span.pagernums").links().regex(".*/list,\\d{6}_\\d+.html").all();

        List<String> links = page.getHtml().xpath("//*[@id=\"articlelistnew\"]/div[87]/span").all();
        page.addTargetRequests(links);
        page.putField("title", page.getHtml().xpath("//div[@class='BlogEntity']/div[@class='BlogTitle']/h1/text()").toString());
        if (page.getResultItems().get("title") == null) {
            //skip this page
            page.setSkip(true);
        }
        page.putField("content", page.getHtml().smartContent().toString());
        page.putField("tags", page.getHtml().xpath("//div[@class='BlogTags']/a/text()").all());
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new GuBaPageProcessor()).addUrl("http://guba.eastmoney.com/list,000702.html").run();
    }
}
