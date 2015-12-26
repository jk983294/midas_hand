package com.victor.midas.train.score;


import com.victor.midas.model.vo.score.StockScoreRecord;

import java.util.List;

public interface ScoreManager {

    public void process() throws Exception;

    public List<StockScoreRecord> getScoreRecords();
}
