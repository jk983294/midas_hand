package com.victor.midas.train.score;


import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.model.vo.score.StockScoreRecord;

import java.util.List;

public interface ScoreManager {

    void process(CalcParameter parameter) throws Exception;

    List<StockScoreRecord> getScoreRecords();

    List<StockVo> getStocks();

    boolean isBigDataSet();
}
