package com.victor.midas.crawl.model;

import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.victor.utilities.utils.ArrayHelper;
import com.victor.utilities.utils.MathHelper;
import com.victor.utilities.utils.StringHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class GuBaUrlInfo {

    private GuBaPageType pageType;

    private String stockCode;

    private Integer pageIndex;

    private Long pageId;

    /**
     * example :
     * http://guba.eastmoney.com/list,000702.html
     * http://guba.eastmoney.com/list,000702_1.html
     * http://guba.eastmoney.com/news,000702,211665695.html
     * http://guba.eastmoney.com/news,000702,211665695_1.html
     * http://guba.eastmoney.com/news,cjpl,211591648.html
     * http://guba.eastmoney.com/news,cjpl,211591648_1.html
     */
    public static GuBaUrlInfo analysis(String url){
        if(StringUtils.isNotEmpty(url)){
            GuBaUrlInfo info = new GuBaUrlInfo();
            String metadata = url;
            if(url.lastIndexOf("/") >= 0){
                metadata = url.substring(url.lastIndexOf("/") + 1);
            }
            List<String> results = StringHelper.split(metadata, "/,._");

            if(results.size() > 0){
                results.remove(results.size() - 1);
                int i = 0;
                String data = ArrayHelper.get(results, i++);
                try {
                    info.pageType = GuBaPageType.valueOf(data);
                    data = ArrayHelper.get(results, i++);
                    if(GuBaPageType.news == info.pageType && GuBaPageType.cjpl.toString().equalsIgnoreCase(data)){
                        info.setPageType(GuBaPageType.cjpl);
                    }
                } catch (IllegalArgumentException e){
                    info.setPageType(GuBaPageType.unknown);
                }

                if(GuBaPageType.unknown == info.pageType){
                    return info;
                } else if(GuBaPageType.cjpl == info.pageType){
                    info.pageId = MathHelper.tryParse2Long(ArrayHelper.get(results, i++));
                    info.pageIndex = MathHelper.tryParse2Int(ArrayHelper.get(results, i++));
                } else {
                    info.stockCode = data;
                    if(GuBaPageType.list == info.pageType){
                        info.pageIndex = MathHelper.tryParse2Int(ArrayHelper.get(results, i++));
                    } else if(GuBaPageType.news == info.pageType){
                        info.pageId = MathHelper.tryParse2Long(ArrayHelper.get(results, i++));
                        info.pageIndex = MathHelper.tryParse2Int(ArrayHelper.get(results, i++));
                    }
                }
            }
            return info;
        }
        return null;
    }

    public GuBaPageType getPageType() {
        return pageType;
    }

    public void setPageType(GuBaPageType pageType) {
        this.pageType = pageType;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Long getPageId() {
        return pageId;
    }

    public void setPageId(Long pageId) {
        this.pageId = pageId;
    }

    public static void main(String[] args) {
        GuBaUrlInfo info;
        info = analysis("http://guba.eastmoney.com/list,000702.html");
        info = analysis("http://guba.eastmoney.com/list,000702_1.html");
        info = analysis("http://guba.eastmoney.com/news,000702,211665695.html");
        info = analysis("http://guba.eastmoney.com/news,000702,211665695_1.html");
        info = analysis("http://guba.eastmoney.com/news,cjpl,211591648.html");
        info = analysis("http://guba.eastmoney.com/news,cjpl,211591648_1.html");
    }
}
