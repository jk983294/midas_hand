package com.victor.spider.app.example;

import com.victor.spider.core.Site;
import com.victor.spider.core.Page;
import com.victor.spider.core.ResultItems;

import com.victor.spider.core.Spider;
import com.victor.spider.core.processor.PageProcessor;

import java.util.ArrayList;
import java.util.List;


public class BaiduBaikePageProcessor implements PageProcessor {

    private Site site = Site.me()//.setHttpProxy(new HttpHost("127.0.0.1",8888))
            .setRetryTimes(3).setSleepTime(1000).setUseGzip(true);

    @Override
    public void process(Page page) {
        page.putField("name", page.getHtml().css("h1.title div.lemmaTitleH1","text").toString());
        page.putField("description", page.getHtml().xpath("//div[@id='lemmaContent-0']//div[@class='para']/allText()"));
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        //single download
        Spider spider = Spider.create(new BaiduBaikePageProcessor()).thread(2);
        String urlTemplate = "http://baike.baidu.com/search/word?word=%s&pic=1&sug=1&enc=utf8";
        ResultItems resultItems = spider.<ResultItems>get(String.format(urlTemplate, "水力发电"));
        System.out.println(resultItems);

        //multi-download synchronize
        List<String> list = new ArrayList<String>();
        list.add(String.format(urlTemplate, "风力发电"));
        list.add(String.format(urlTemplate, "太阳能"));
        list.add(String.format(urlTemplate, "地热发电"));
        list.add(String.format(urlTemplate, "地热发电"));
        List<ResultItems> resultItemses = spider.<ResultItems>getAll(list);
        for (ResultItems resultItemse : resultItemses) {
            System.out.println(resultItemse.getAll());
        }
        spider.close();
    }
}
