package com.victor.midas.util;

import com.victor.midas.model.common.CmdParameter;
import com.victor.midas.model.common.CmdType;
import com.victor.utilities.utils.ArrayHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * constant variable lives here
 */
public class MidasConstants {

    public enum CalculatorType {
        Tradable,
        Index,
        All,            // apply to both index and tradable
        Aggregation     // only apply to sh index
    }

    /**
     * misc collection MiscName for MongoDB index
     */
    public static final String MISC_ALL_STOCK_NAMES = "AllStockNames";
    public static final String MISC_SINGLE_TRAIN_RESULT = "SingleTrainResult";

    /**
     * collection name
     */
    public static final String STOCK_COLLECTION_NAME = "Stock";
    public static final String MISC_COLLECTION_NAME = "StockMisc";
    public static final String STOCKINFO_COLLECTION_NAME = "StockInfo";
    public static final String STOCK_CRAWL_DATA_COLLECTION_NAME = "StockCrawlData";
    public static final String DAY_FOCUS_COLLECTION_NAME = "StockDayFocus";
    public static final String TASK_COLLECTION_NAME = "StockTask";
    public static final String TRAIN_COLLECTION_NAME = "StockTrain";
    public static final String SCORE_COLLECTION_NAME = "StockScore";
    public static final String CONCEPT_SCORE_COLLECTION_NAME = "StockConceptScore";

    /**
     * sequence collection's document id
     */
    public static final String SEQUENCE_DOCUMENT_TRAIN = "train";

    /**
     * basic index name
     */
    public static final String INDEX_NAME_START = "start";
    public static final String INDEX_NAME_MAX = "max";
    public static final String INDEX_NAME_MIN = "min";
    public static final String INDEX_NAME_END = "end";
    public static final String INDEX_NAME_VOLUME = "volume";
    public static final String INDEX_NAME_TOTAL = "total";
    public static final String INDEX_NAME_CHANGEPCT = "changePct";

    public static String getIndexId(String stockName, String indexName){
        StringBuilder sb = new StringBuilder(stockName);
        sb.append("_").append(indexName);
        return sb.toString();
    }

    /**
     * response status
     */
    public static final String RESPONSE_SUCCESS = "SUCCESS";
    public static final String RESPONSE_FAIL = "FAIL";

    /**
     * training related
     */
    public static final double INITIATE_FUND = 500000;
    public static final double TAX_RATE = 0.0015;

    /**
     * SH index name
     */
    public static final String SH_INDEX_NAME = "IDX999999";

    /**
     * type ahead actions
     */
    public static List<String> actions;

    static {
        actions = new ArrayList<>();
        for(CmdType cmdType : CmdType.values()){
            actions.add(cmdType.toString());
        }
        for(CmdParameter cmdParameter : CmdParameter.values()){
            actions.add(cmdParameter.toString());
        }
    }


    /**
     * spider related
     */
    public static final String gubaUrlListTemplate = "http://guba.eastmoney.com/list,%s_%d.html";
    public static final String gubaUrlNewsTemplate = "http://guba.eastmoney.com/news,%s,%s_%d.html";
    public static final String gubaUrlCjplTemplate = "http://guba.eastmoney.com/news,cjpl,%s_%d.html";
}
