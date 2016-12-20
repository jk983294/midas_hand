package com.victor.midas.train.score;

import com.victor.midas.model.vo.score.StockScore;
import com.victor.midas.model.vo.score.StockScoreRecord;
import com.victor.midas.model.vo.score.StockSeverity;
import com.victor.midas.train.perf.PerfCollector;
import com.victor.midas.util.MidasException;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * helper for score method
 */
public class ScoreHelper {

    public static void perfCollect(List<StockScore> stockScores, int cob, PerfCollector perfCollector, List<StockScoreRecord> scoreRecords) throws MidasException {
        StockScoreRecord stockScoreRecord = new StockScoreRecord(cob, stockScores);
        scoreRecords.add(stockScoreRecord);
        perfCollector.addRecord(stockScoreRecord);
    }

    /**
     * no matter what severity is that cob, StockScoreRecord will always be recorded,
     * but only those normal cob's data will be performance collected
     */
    public static void perfCollect(List<StockScore> stockScores, int cob, PerfCollector perfCollector,
                                   List<StockScoreRecord> scoreRecords, StockSeverity severity) throws MidasException {
        StockScoreRecord stockScoreRecord = new StockScoreRecord(cob, stockScores);
        scoreRecords.add(stockScoreRecord);
        stockScoreRecord.setSeverity(severity);
        if(severity.ordinal() <= StockSeverity.Normal.ordinal()){
            perfCollector.addRecord(stockScoreRecord);
        }
    }
}
