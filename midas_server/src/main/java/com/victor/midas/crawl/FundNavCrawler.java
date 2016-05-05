package com.victor.midas.crawl;

import com.victor.midas.model.vo.StockVo;
import com.victor.midas.model.vo.concept.StockCrawlData;
import com.victor.midas.util.MidasConstants;
import com.victor.utilities.utils.CrawlHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * used to crawl fund nav
 */
public class FundNavCrawler {
    private static final Logger logger = Logger.getLogger(FundNavCrawler.class);

    private final static String urlPattern = "http://stockjs.finance.qq.com/fundUnitNavAll/data/year_all/%s.js";

    public List<StockVo> stocks;

    public List<String> stockNames;

    public FundNavCrawler(List<String> stockNames) {
        this.stockNames = stockNames;
        stocks = new ArrayList<>();
    }

    public void crawl(){
        logger.error("Crawl fund nav starts...");
        for(String stockName : stockNames){
            if(stockName.startsWith("IDX")) continue;
            try {
                String response = CrawlHelper.crawl(String.format(urlPattern, stockName));
                if(StringUtils.isNotEmpty(response)){
                    parse(response, stockName);
                } else {
                    logger.error("no crawled data return for stock : " + stockName);
                }
            } catch (IOException e) {
                logger.error("issue happen when crawling data for stock : " + stockName);
            }
        }
        logger.error("Crawl fund nav finished.");
    }

    /**
     * result like :
     * {"code":0,"msg":"","data":[{"code":"012007","name":"\u6709\u8272\u91d1\u5c5e"},{"code":"021170","name":"\u878d\u8d44\u878d\u5238"},{"code":"028300","name":"\u4e2d\u5b57\u5934"},{"code":"031100","name":"\u5317\u4eac"},{"code":"04ZC60","name":"\u91d1\u5c5e\u3001\u975e\u91d1\u5c5e"},{"code":"061050","name":"\u6709\u8272\u91d1\u5c5e"},{"code":"621510","name":"\u539f\u6750\u6599"}]}
     * @param result
     * @param stockName
     */
    private void parse(String result, String stockName){
        if(StringUtils.isEmpty(result) || !result.contains("{")) return;
        JSONObject json= new JSONObject(result.substring(result.indexOf("{")));
        if(json.get("data") != null){
            StockVo stock = new StockVo();
            stock.setStockName(stockName);
            stock.setDesp((String)json.get("name"));
            JSONArray jsonArray=json.getJSONArray("data");
            if(jsonArray.length() == 0){
                logger.error("crawled data return for stock : " + stockName + " error : 0 concept found");
                return;
            }
            int[] cobs = new int[jsonArray.length()];
            double[] navs = new double[jsonArray.length()];
            double[] cumulativeNavs = new double[jsonArray.length()];
            for(int i=0; i < jsonArray.length(); i++){
                JSONArray navTuple = (JSONArray) jsonArray.get(i);
                cobs[i] = Integer.valueOf((String)navTuple.get(0));
                navs[i] = Double.valueOf((String)navTuple.get(1));
                cumulativeNavs[i] = Double.valueOf((String)navTuple.get(2));
            }
            stock.setDatesInt(cobs);
            stock.addIndex(MidasConstants.INDEX_NAME_NAV, navs);
            stock.addIndex(MidasConstants.INDEX_NAME_CUMULATIVE_NAV, cumulativeNavs);
            stocks.add(stock);
        }
    }


//    public static void main(String[] args) {
//        List<String> names = new ArrayList<>();
//        names.add("150216");
//        FundNavCrawler crawler = new FundNavCrawler(names);
//        crawler.crawl();
//    }
}
