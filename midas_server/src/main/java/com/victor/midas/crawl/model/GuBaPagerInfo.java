package com.victor.midas.crawl.model;

import com.victor.midas.util.MidasConstants;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * extract those info
 * example : list,000702_|25292|80|1
 * <span class="pagernums" data-pager="list,000702_|25292|80|1"><span></span>
 */
public class GuBaPagerInfo {

    private static final Pattern pagerNumberPattern = Pattern.compile("(list),(\\d{6})_\\|(\\d+)\\|(\\d+)\\|(\\d+)");

    private Integer topicTotalCnt, topicPageCnt, pagerIndex;

    private String stockCode;

    public static GuBaPagerInfo analysis(String pagerNumberString){
        if(StringUtils.isEmpty(pagerNumberString)){
            return null;
        }
        Matcher matcher = pagerNumberPattern.matcher(pagerNumberString);
        if(matcher.matches()){
            GuBaPagerInfo info = new GuBaPagerInfo();
            info.stockCode = matcher.group(2);
            info.topicTotalCnt = Integer.valueOf(matcher.group(3));
            info.topicPageCnt = Integer.valueOf(matcher.group(4));
            info.pagerIndex = Integer.valueOf(matcher.group(5));
            return info;
        } else {
            return null;
        }
    }

    /**
     * when it is first pager, add remaining pages to crawl
     */
    public List<String> getTargetRequestsList(){
        List<String> urls = new ArrayList<>();
        if(pagerIndex == 1){
            if(topicPageCnt > 0){
                int possiblePageCnt = topicTotalCnt / topicPageCnt + 1;
                for (int i = 2; i <= possiblePageCnt; i++){
                    urls.add(String.format(MidasConstants.gubaUrlTemplate, stockCode, i));
                }
            }
        }
        return urls;
    }

    public Integer getTopicTotalCnt() {
        return topicTotalCnt;
    }

    public void setTopicTotalCnt(Integer topicTotalCnt) {
        this.topicTotalCnt = topicTotalCnt;
    }

    public Integer getTopicPageCnt() {
        return topicPageCnt;
    }

    public void setTopicPageCnt(Integer topicPageCnt) {
        this.topicPageCnt = topicPageCnt;
    }

    public Integer getPagerIndex() {
        return pagerIndex;
    }

    public void setPagerIndex(Integer pagerIndex) {
        this.pagerIndex = pagerIndex;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }
}
