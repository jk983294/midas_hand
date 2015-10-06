package com.victor.midas.calculator.util;

import com.victor.midas.calculator.AggregationCalculator;
import com.victor.midas.calculator.common.IndexCalcbase;
import com.victor.midas.calculator.index.IndexBadState;
import com.victor.midas.calculator.indicator.IndexChangePct;
import com.victor.midas.calculator.indicator.IndexPriceMA;
import com.victor.midas.calculator.indicator.IndexVolumeMa;
import com.victor.midas.calculator.indicator.kline.*;
import com.victor.midas.calculator.indicator.trend.IndexPriceDelta;
import com.victor.midas.calculator.indicator.trend.IndexSupportResist;
import com.victor.midas.calculator.score.StockScoreRank;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.utilities.math.stats.ma.SMA;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Create index calculators
 */
public class IndexFactory {

    /** all index calculators */
    private static List<IndexCalcbase> indexCalcbases = new ArrayList<>();
    /** all common index calculators that apply for tradable and Index stocks */
    private static List<IndexCalcbase> indexCalcbasesCommonForIndex = new ArrayList<>();
    /** all common index calculators that only apply for Index stocks not for tradable stocks */
    private static List<IndexCalcbase> indexCalcbasesForIndex = new ArrayList<>();
    private static List<String> indexNames = new ArrayList<>();
    private static CalcParameter parameter = new CalcParameter();
    private static HashMap<String, IndexCalcbase> indexName2Calculator = new HashMap<>();

    static {
        /*** prepare all index calculator*/
        addCalculator(new IndexChangePct(parameter), true);
        //addCalculator(new ChartTimeFrame(parameter));
//        addCalculator(new IndexPriceMA(parameter, new SMA()), true);
        //addCalculator(new IndexVolumeMa(parameter, new SMA()), true);
        //addCalculator(new PriceMaTangle(parameter));
//        addCalculator(new IndexPriceDelta(parameter), true);
//        addCalculator(new ChartTimeFrameWithVolume(parameter));
//        addCalculator(new IndexKLine(parameter), true);
        //addCalculator(new IndexVolumePriceCorr(parameter));
//        addCalculator(new IndexKLineMa(parameter));
//        addCalculator(new IndexKState(parameter), true);
//        addCalculator(new IndexKLineSignals(parameter));
//        addCalculator(new IndexRebounce(parameter));
//        addCalculator(new IndexLongGoodTrend(parameter));
//        addCalculator(new IndexGoodPeriod(parameter));
//        addCalculator(new IndexSupportResist(parameter), false);
        addCalculator(new StockScoreRank(parameter), true);

        /** index calculator for Index*/
//        indexCalcbasesForIndex.add(new IndexBadState(parameter));
    }

    public static void addCalculator(IndexCalcbase indexCalcbase, boolean isCommonIndex){
        indexCalcbases.add(indexCalcbase);
        indexCalcbase.applyParameter();
        indexNames.add(indexCalcbase.getIndexName());
        indexName2Calculator.put(indexCalcbase.getIndexName(), indexCalcbase);
        if(isCommonIndex){
            indexCalcbasesCommonForIndex.add(indexCalcbase);
        }
    }

    public static void applyNewParameter(CalcParameter param){
        parameter = param;
        applyNewParameter(param, indexCalcbases);
    }

    public static void applyNewParameter(CalcParameter param, List<IndexCalcbase> calcbaseList){
        for (IndexCalcbase calcbase : calcbaseList){
            calcbase.setParameter(parameter);
            calcbase.applyParameter();
        }
    }

    public static List<IndexCalcbase> getIndexCalcbases() {
        return indexCalcbases;
    }

    public static List<IndexCalcbase> getIndexCalcbasesForBigDataSet() {
        List<IndexCalcbase> bigDataSet = new ArrayList<>();
        bigDataSet.add(new IndexChangePct(parameter));
        bigDataSet.add(new StockScoreRank(parameter));
        return bigDataSet;
    }

    public static List<String> getIndexNames() {
        return indexNames;
    }

    public static void setIndexCalcbases(List<IndexCalcbase> indexCalcbases) {
        IndexFactory.indexCalcbases = indexCalcbases;
    }

    public static void setAggregationCalculator(List<IndexCalcbase> calcbases, AggregationCalculator aggregationCalculator) {
        for(IndexCalcbase indexCalcbase : calcbases){
            indexCalcbase.setAggregationCalculator(aggregationCalculator);
        }
    }

    public static void setIndexNames(List<String> indexNames) {
        IndexFactory.indexNames = indexNames;
    }

    public static CalcParameter getParameter() {
        return parameter;
    }

    public static void setParameter(CalcParameter parameter) {
        IndexFactory.parameter = parameter;
    }

    public static HashMap<String, IndexCalcbase> getIndexName2Calculator() {
        return indexName2Calculator;
    }

    public static void setIndexName2Calculator(HashMap<String, IndexCalcbase> indexName2Calculator) {
        IndexFactory.indexName2Calculator = indexName2Calculator;
    }

    public static List<IndexCalcbase> getIndexCalcbasesForIndex() {
        return indexCalcbasesForIndex;
    }

    public static List<IndexCalcbase> getIndexCalcbasesCommonForIndex() {
        return indexCalcbasesCommonForIndex;
    }
}
