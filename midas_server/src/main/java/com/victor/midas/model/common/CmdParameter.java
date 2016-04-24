package com.victor.midas.model.common;

import com.victor.midas.calculator.macd.IndexMacdAdvancedSignal;
import com.victor.midas.calculator.revert.PriceCrashRevertSignal;
import com.victor.midas.calculator.score.StockRevertScoreRank;
import com.victor.midas.calculator.score.StockScoreRank;
import com.victor.midas.calculator.score.StockSupportScoreRank;
import com.victor.midas.util.MidasException;

import java.util.List;

/**
 * cmd constant
 */
public enum CmdParameter {
    guba,
    concept,
    score_ma,
    score_revert,
    score_support,
    score_macd,
    score_pcrs,
    score_concept;

    public static CmdParameter getParameter(CmdParameter defaultPara, List<String> params, int index){
        if(params != null && params.size() > index){
            return CmdParameter.valueOf(params.get(index));
        } else {
            return defaultPara;
        }
    }

    public static String getIndexName(CmdParameter cmdParameter) throws Exception {
        switch(cmdParameter){
            case score_ma:  return StockScoreRank.INDEX_NAME;
            case score_revert: return StockRevertScoreRank.INDEX_NAME;
            case score_concept: return StockScoreRank.INDEX_NAME;
            case score_support: return StockSupportScoreRank.INDEX_NAME;
            case score_macd: return IndexMacdAdvancedSignal.INDEX_NAME;
            case score_pcrs: return PriceCrashRevertSignal.INDEX_NAME;
            default : throw new MidasException("no such IndexName in score task.");
        }
    }
}
