package com.victor.midas.crawl.model;

import com.victor.midas.util.MidasConstants;
import com.victor.utilities.utils.ArrayHelper;
import com.victor.utilities.utils.MathHelper;
import com.victor.utilities.utils.StringHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * extract those info
 * <span class="pagernums" data-pager="list,000702_|25292|80|1"><span></span>
 * list,000702_|25292|80|1
 * news,601766,211862489_|309|30|1
 * news,cjpl,223920695_|843|30|1
 */
public class GuBaPagerInfo {

    private Integer topicTotalCnt, topicPageCnt, pagerIndex;

    private String stockCode;

    private String topicId;

    private GuBaPageType pageType;

    public static GuBaPagerInfo analysis(String pagerNumberString){
        if(StringUtils.isNotEmpty(pagerNumberString)){
            GuBaPagerInfo info = new GuBaPagerInfo();
            List<String> results = StringHelper.split(pagerNumberString, "|,_");
            if(results.size() > 0){
                int i = 0;
                String data = ArrayHelper.get(results, i++);
                try {
                    info.pageType = GuBaPageType.valueOf(data);
                    data = ArrayHelper.get(results, i++);
                    if(GuBaPageType.news == info.pageType && GuBaPageType.cjpl.toString().equalsIgnoreCase(data)){
                        info.pageType = GuBaPageType.cjpl;
                    }
                } catch (IllegalArgumentException e){
                    info.pageType = GuBaPageType.unknown;
                }

                if(GuBaPageType.unknown == info.pageType){
                    return info;
                } else if(GuBaPageType.list == info.pageType){
                    info.stockCode = data;

                } else if(GuBaPageType.news == info.pageType){
                    info.stockCode = data;
                    info.topicId = ArrayHelper.get(results, i++);
                } else if(GuBaPageType.cjpl == info.pageType){
                    info.topicId = ArrayHelper.get(results, i++);
                }
                info.topicTotalCnt = MathHelper.tryParse2Int(ArrayHelper.get(results, i++));
                info.topicPageCnt = MathHelper.tryParse2Int(ArrayHelper.get(results, i++));
                info.pagerIndex = MathHelper.tryParse2Int(ArrayHelper.get(results, i++));
            }
            return info;
        }
        return null;
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
                    switch (pageType){
                        case list: urls.add(String.format(MidasConstants.gubaUrlListTemplate, stockCode, i)); break;
                        case news: urls.add(String.format(MidasConstants.gubaUrlNewsTemplate, stockCode, topicId, i)); break;
                        case cjpl: urls.add(String.format(MidasConstants.gubaUrlCjplTemplate, topicId, i)); break;
                    }
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

    public static void main(String[] args) {
        GuBaPagerInfo info;
        List<String> urls;
        info = analysis("list,000702_|25292|80|1");
        urls = info.getTargetRequestsList();
        info = analysis("news,601766,211862489_|309|30|1");
        urls = info.getTargetRequestsList();
        info = analysis("news,cjpl,223920695_|843|30|1");
        urls = info.getTargetRequestsList();
    }
}
