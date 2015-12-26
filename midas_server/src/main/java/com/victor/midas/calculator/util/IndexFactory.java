package com.victor.midas.calculator.util;

import com.victor.midas.calculator.common.ICalculator;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.util.MidasException;
import com.victor.utilities.datastructures.graph.*;
import com.victor.utilities.visual.VisualAssist;

import java.util.*;

/**
 * Create index calculators
 */
public class IndexFactory {

    private static final HashMap<String, ICalculator> calcName2calculator = new HashMap();

    public static CalcParameter parameter = new CalcParameter();

    static {
        /*** prepare all index calculator*/
        //addCalculator(new IndexChangePct(parameter), true);
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
        //addCalculator(new StockScoreRank(parameter), true);

        /** index calculator for Index*/
//        indexCalcbasesForIndex.add(new IndexBadState(parameter));
    }

    /**
     * use dependency to get all calculators
     */
    public static List<ICalculator> getAllNeededCalculators(String calc) throws MidasException {
        List<ICalculator> calculators = new ArrayList<>();
        Queue<String> toProcessCalcNames = new LinkedList<String>();
        Set<String> visited = new HashSet<>();
        toProcessCalcNames.add(calc);

        Graph<String> dependency = new Graph<>(GraphType.DIRECTED);

        while(!toProcessCalcNames.isEmpty()){
            String calcName = toProcessCalcNames.remove();
            if(!visited.contains(calcName)){
                visited.add(calcName);
                ICalculator current = calcName2calculator.get(calcName);
                Set<String> needed = current.getRequiredCalculators();
                for(String need : needed){
                    dependency.addEdge(calcName, need);
                    if(!visited.contains(need)){
                        toProcessCalcNames.add(need);
                    }
                }
            }
        }

        List<String> names = TopologicalSort.sortRevertThenGetRawData(dependency);
        VisualAssist.print("all calculators needed: ", names);
        if(names.size() > 0){
            for(String name : names){
                calculators.add(calcName2calculator.get(name));
            }
        } else {
            calculators.add(calcName2calculator.get(calc));
        }
        return calculators;
    }

    public static void addCalculator(String name, ICalculator calculator){
        calcName2calculator.put(name, calculator);
    }

    public static void applyNewParameter(CalcParameter param, List<ICalculator> calcList){
        for(ICalculator calc : calcList){
            calc.applyParameter(param);
        }
    }

    public static CalcParameter getParameter() {
        return parameter;
    }

    public static void setParameter(CalcParameter parameter) {
        IndexFactory.parameter = parameter;
    }

    public static HashMap<String, ICalculator> getCalcName2calculator() {
        return calcName2calculator;
    }
}
