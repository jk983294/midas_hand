package com.victor.midas.train.score;

import com.victor.midas.model.vo.score.StockScore;
import com.victor.midas.model.vo.score.StockScoreRecord;
import com.victor.midas.train.perf.PerfCollector;
import com.victor.midas.util.MidasException;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * helper for score method
 */
public class ScoreHelper {

    public static void perfCollect(List<StockScore> stockScores, int cob, PerfCollector perfCollector, List<StockScoreRecord> scoreRecords) throws MidasException {
        if(CollectionUtils.isNotEmpty(stockScores)){
            StockScoreRecord stockScoreRecord = new StockScoreRecord(cob, stockScores);
            scoreRecords.add(stockScoreRecord);
            perfCollector.addRecord(stockScoreRecord);
        }
    }
}
