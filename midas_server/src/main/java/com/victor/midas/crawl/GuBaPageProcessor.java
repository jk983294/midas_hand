package com.victor.midas.crawl;

import com.victor.spider.core.Page;
import com.victor.spider.core.Site;
import com.victor.spider.core.Spider;
import com.victor.spider.core.processor.PageProcessor;
import com.victor.utilities.utils.RegExpHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GuBaPageProcessor implements PageProcessor {

    private static final Pattern pagerNumberPattern = Pattern.compile("(list),(\\d{6})_\\|(\\d+)\\|(\\d+)\\|(\\d+)");

    private Site site = Site.me().setDomain("guba.eastmoney.com");

    private int topicTotalCnt, topicPageCnt, pagerIndex;

    private String stockCode;


    @Override
    public void process(Page page) {
        String pagerNumberString = page.getHtml().xpath("//div[@class=\"pager\"]/span/@data-pager").toString();
        if(StringUtils.isNotEmpty(pagerNumberString)){
            analysisPagerNumberString(pagerNumberString);
        }
        //String topicNumberString = page.getHtml().css("div.pager/text()").toString();
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

    private void analysisPagerNumberString(String pagerNumberString){
        Matcher matcher = pagerNumberPattern.matcher(pagerNumberString);
        if(matcher.matches()){
            stockCode = matcher.group(2);
            topicTotalCnt = Integer.valueOf(matcher.group(3));
            topicPageCnt = Integer.valueOf(matcher.group(4));
            pagerIndex = Integer.valueOf(matcher.group(5));
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new GuBaPageProcessor()).addUrl("http://guba.eastmoney.com/list,000702.html").run();
    }
}
