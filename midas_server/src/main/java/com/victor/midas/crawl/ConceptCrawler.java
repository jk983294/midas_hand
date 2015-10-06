package com.victor.midas.crawl;

import com.victor.midas.model.vo.concept.StockConcept;
import com.victor.midas.model.vo.concept.StockCrawlData;
import com.victor.utilities.utils.CrawlHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * used to crawl stock concept
 */
public class ConceptCrawler {
    private static final Logger logger = Logger.getLogger(ConceptCrawler.class);

    private final static String urlPattern = "http://ifzq.gtimg.cn/stock/relate/data/plate?code=%s&_var=_IFLOAD_2";

    private List<StockCrawlData> crawlDatas;

    private List<String> stockNames;

    public ConceptCrawler(List<String> stockNames) {
        this.stockNames = stockNames;
        crawlDatas = new ArrayList<>();
    }

    public void crawl(){
        for(String stockName : stockNames){
            if(stockName.startsWith("IDX")) continue;
            try {
                String response = CrawlHelper.crawl(String.format(urlPattern, stockName.toLowerCase()));
                if(StringUtils.isNotEmpty(response)){
                    parse(response, stockName);
                } else {
                    logger.error("no crawled data return for stock : " + stockName);
                }
            } catch (IOException e) {
                logger.error("issue happen when crawling data for stock : " + stockName);
            }
        }
    }

    /**
     * result like :
     * {"code":0,"msg":"","data":[{"code":"012007","name":"\u6709\u8272\u91d1\u5c5e"},{"code":"021170","name":"\u878d\u8d44\u878d\u5238"},{"code":"028300","name":"\u4e2d\u5b57\u5934"},{"code":"031100","name":"\u5317\u4eac"},{"code":"04ZC60","name":"\u91d1\u5c5e\u3001\u975e\u91d1\u5c5e"},{"code":"061050","name":"\u6709\u8272\u91d1\u5c5e"},{"code":"621510","name":"\u539f\u6750\u6599"}]}
     * @param result
     * @param stockName
     */
    private void parse(String result, String stockName){
        StockCrawlData stockCrawlData = new StockCrawlData(stockName);
        JSONObject json= new JSONObject(result.substring(result.indexOf("{")));
        if((Integer)json.get("code") < 0){
            logger.error("crawled data return for stock : " + stockName + " error : " + json.get("msg"));
            return;
        }
        JSONArray jsonArray=json.getJSONArray("data");
        if(jsonArray.length() == 0){
            logger.error("crawled data return for stock : " + stockName + " error : 0 concept found");
            return;
        }
        for(int i=0;i < jsonArray.length(); i++){
            JSONObject concept =(JSONObject) jsonArray.get(i);
            stockCrawlData.addConcept(new StockConcept((String)concept.get("code"), (String)concept.get("name")));
        }
        crawlDatas.add(stockCrawlData);
    }

    public List<StockCrawlData> getCrawlDatas() {
        return crawlDatas;
    }

    public void setCrawlDatas(List<StockCrawlData> crawlDatas) {
        this.crawlDatas = crawlDatas;
    }
}
