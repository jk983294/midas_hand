package com.victor.midas.train.perf;

import com.victor.midas.model.vo.score.StockScore;

import java.util.Comparator;


public class StockScoreComparator implements Comparator<StockScore> {
    @Override
    public int compare(StockScore o1, StockScore o2) {
        if(o1.getPerf() > o2.getPerf()) return 1;
        else if(o1.getPerf() < o2.getPerf()) return -1;
        else return 0;
    }
}
