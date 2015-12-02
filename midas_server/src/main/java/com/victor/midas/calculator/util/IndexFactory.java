package com.victor.midas.calculator.util;

import com.victor.midas.calculator.AggregationCalculator;
import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.calculator.indicator.IndexChangePct;
import com.victor.midas.calculator.score.StockScoreRank;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.util.MidasException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create index calculators
 */
public class IndexFactory {

    private static final HashMap<String, IndexCalcBase> calcMap = new HashMap();

    /** all index calculators */
    private static List<IndexCalcBase> indexCalcBases = new ArrayList<>();
    /** all common index calculators that apply for tradable and Index stocks */
    private static List<IndexCalcBase> indexCalcbasesCommonForIndex = new ArrayList<>();
    /** all common index calculators that only apply for Index stocks not for tradable stocks */
    private static List<IndexCalcBase> indexCalcbasesForIndex = new ArrayList<>();
    private static List<String> indexNames = new ArrayList<>();
    public static CalcParameter parameter = new CalcParameter();
    private static Map<String, IndexCalcBase> indexName2Calculator = new HashMap<>();

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

    public static IndexCalcBase getCalculator(String calc) throws MidasException {
        IndexCalcBase calcbase = calcMap.get(calc);
        if(calcbase == null) {
            switch (calc){
                case IndexChangePct.INDEX_NAME : {
                    calcbase = new IndexChangePct(parameter);
                    calcMap.put(IndexChangePct.INDEX_NAME, calcbase);
                } break;
                default: throw new MidasException("no such calculator!");
            }
        }
        return calcbase;
    }

    public static void addCalculator(String calc, IndexCalcBase calcBase) {
        IndexCalcBase calcbase = calcMap.get(calc);
        if(calcbase == null) {
            calcMap.put(calc, calcbase);
        }
    }

    public static void addCalculator(IndexCalcBase indexCalcBase, boolean isCommonIndex){
        indexCalcBases.add(indexCalcBase);
        indexCalcBase.applyParameter();
        indexNames.add(indexCalcBase.getIndexName());
        indexName2Calculator.put(indexCalcBase.getIndexName(), indexCalcBase);
        if(isCommonIndex){
            indexCalcbasesCommonForIndex.add(indexCalcBase);
        }
    }

    public static void applyNewParameter(CalcParameter param){
        parameter = param;
        applyNewParameter(param, indexCalcBases);
    }

    public static void applyNewParameter(CalcParameter param, List<IndexCalcBase> calcbaseList){
        for (IndexCalcBase calcbase : calcbaseList){
            calcbase.setParameter(parameter);
            calcbase.applyParameter();
        }
    }

    public static List<IndexCalcBase> getIndexCalcBases() {
        return indexCalcBases;
    }

    public static List<IndexCalcBase> getIndexCalcbasesForBigDataSet() {
        List<IndexCalcBase> bigDataSet = new ArrayList<>();
        bigDataSet.add(new IndexChangePct(parameter));
        bigDataSet.add(new StockScoreRank(parameter));
        return bigDataSet;
    }

    public static List<String> getIndexNames() {
        return indexNames;
    }

    public static void setIndexCalcBases(List<IndexCalcBase> indexCalcBases) {
        IndexFactory.indexCalcBases = indexCalcBases;
    }

    public static void setAggregationCalculator(List<IndexCalcBase> calcbases, AggregationCalculator aggregationCalculator) {
        for(IndexCalcBase indexCalcBase : calcbases){
            indexCalcBase.setAggregationCalculator(aggregationCalculator);
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

    public static Map<String, IndexCalcBase> getIndexName2Calculator() {
        return indexName2Calculator;
    }

    public static void setIndexName2Calculator(HashMap<String, IndexCalcBase> indexName2Calculator) {
        IndexFactory.indexName2Calculator = indexName2Calculator;
    }

    public static List<IndexCalcBase> getIndexCalcbasesForIndex() {
        return indexCalcbasesForIndex;
    }

    public static List<IndexCalcBase> getIndexCalcbasesCommonForIndex() {
        return indexCalcbasesCommonForIndex;
    }
}
