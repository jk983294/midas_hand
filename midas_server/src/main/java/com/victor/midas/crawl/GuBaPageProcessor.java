package com.victor.midas.crawl;

import com.victor.midas.crawl.model.*;
import com.victor.midas.util.MidasConstants;
import com.victor.spider.core.Page;
import com.victor.spider.core.Site;
import com.victor.spider.core.Spider;
import com.victor.spider.core.processor.PageProcessor;
import com.victor.spider.core.selector.Selectable;
import com.victor.utilities.utils.StringHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GuBaPageProcessor implements PageProcessor {

    private Site site = Site.me().setDomain("guba.eastmoney.com");
    /** pager section info */
    //private GuBaPagerInfo pagerInfo;
    /** crawl stock code */
    private String stockCode, targetStockCode;

    public GuBaPageProcessor(String toCrawlStockCode) {
        this.targetStockCode = toCrawlStockCode;
    }

    @Override
    public void process(Page page) {
        GuBaUrlInfo urlInfo = GuBaUrlInfo.analysis(page.getUrl().toString());
        switch (urlInfo.getPageType()){
            case list : dealWithListPage(page); break;
            case news : dealWithTopicPage(page, urlInfo); break;
            case cjpl : break;
            default:
        }


//        //String topicNumberString = page.getHtml().css("div.pager/text()").toString();
//        List<String> urls = page.getHtml().css("span.pagernums").links().all();
//        urls = page.getHtml().css("span.pagernums").links().regex(".*/list,\\d{6}_\\d+.html").all();
//
//        List<String> links = page.getHtml().xpath("//*[@id=\"articlelistnew\"]/div[87]/span").all();
//        page.addTargetRequests(links);
//        page.putField("title", page.getHtml().xpath("//div[@class='BlogEntity']/div[@class='BlogTitle']/h1/text()").toString());
//        if (page.getResultItems().get("title") == null) {
//            //skip this page
//            page.setSkip(true);
//        }
//        page.putField("content", page.getHtml().smartContent().toString());
//        page.putField("tags", page.getHtml().xpath("//div[@class='BlogTags']/a/text()").all());
    }

    /**
     * extract pager info, snapshot of each topic info
     */
    private void dealWithListPage(Page page){
        String pagerNumberString = page.getHtml().xpath("//div[@class=\"pager\"]/span/@data-pager").toString();
        if(StringUtils.isNotEmpty(pagerNumberString)){
            GuBaPagerInfo pagerInfo = GuBaPagerInfo.analysis(pagerNumberString);
            page.addTargetRequests(pagerInfo.getTargetRequestsList());
            if(targetStockCode.equals(pagerInfo.getStockCode())){
                List<Selectable> articleHeaders = page.getHtml().css("div.articleh").nodes();
                List<GuBaTopicSnapshot> snapshots = GuBaTopicSnapshot.generate(articleHeaders);
                for(GuBaTopicSnapshot snapshot : snapshots){
                    page.addTargetRequest(snapshot.getLink());
                }
            }
        }
    }

    /**
     * extract pager info, snapshot of each topic info
     */
    private void dealWithTopicPage(Page page, GuBaUrlInfo urlInfo){
        Selectable topicHtml = page.getHtml().xpath("//div[@id=\"zwcontent\"]");
        GuBaTopic topic = null;
        if(urlInfo.getPageIndex() == null || urlInfo.getPageIndex().equals(Integer.valueOf(1))){
            topic = GuBaTopic.generate(topicHtml, urlInfo);
            String pagerNumberString = page.getHtml().xpath("//div[@class=\"pager\"]/span/@data-page").toString();
            GuBaPagerInfo pagerInfo = GuBaPagerInfo.analysis(pagerNumberString);
            page.addTargetRequests(pagerInfo.getTargetRequestsList());
        }
        List<Selectable> commentHtmls = page.getHtml().xpath("//div[@id=\"zwlist\"]/div").nodes();
        List<GuBaComment> comments = GuBaComment.generate(commentHtmls);
        GuBaComment.setTopicId(comments, urlInfo.getPageId());
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        String stockCode = "000702";
//        String url = String.format(MidasConstants.gubaUrlListTemplate, stockCode, 1);
//        Spider.create(new GuBaPageProcessor(stockCode)).addUrl("http://guba.eastmoney.com/list,000702.html").run();
//        Spider.create(new GuBaPageProcessor(stockCode)).addUrl("http://guba.eastmoney.com/news,000702,211665695.html").run();
//        Spider.create(new GuBaPageProcessor(stockCode)).addUrl("http://guba.eastmoney.com/news,601918,201526977_2.html").run();
        Spider.create(new GuBaPageProcessor(stockCode)).addUrl("http://guba.eastmoney.com/news,601766,211862489_1.html").run();
//        Spider.create(new GuBaPageProcessor(stockCode)).addUrl("http://guba.eastmoney.com/news,cjpl,211591648.html").run();
    }
}
