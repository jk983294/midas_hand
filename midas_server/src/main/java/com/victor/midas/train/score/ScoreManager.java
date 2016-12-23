package com.victor.midas.train.score;


import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.model.vo.score.StockScoreRecord;

import java.util.List;

public interface ScoreManager {

    public void process(CalcParameter parameter) throws Exception;

    public List<StockScoreRecord> getScoreRecords();

    public List<StockVo> getStocks();

    public boolean isBigDataSet();
}
