package com.victor.spider.app.extension;

import com.victor.spider.app.example.GithubRepoPageProcessor;
import com.victor.spider.app.example.OschinaBlogPageProcessor;
import com.victor.spider.core.Spider;
import com.victor.spider.core.monitor.SpiderMonitor;

public class MonitorExample {

    public static void main(String[] args) throws Exception {

        Spider oschinaSpider = Spider.create(new OschinaBlogPageProcessor())
                .addUrl("http://my.oschina.net/flashsword/blog");
        Spider githubSpider = Spider.create(new GithubRepoPageProcessor())
                .addUrl("https://github.com/code4craft");

        SpiderMonitor.instance().register(oschinaSpider);
        SpiderMonitor.instance().register(githubSpider);
        oschinaSpider.start();
        githubSpider.start();
    }
}
