package com.victor.midas.calculator.util;

import com.victor.midas.calculator.common.ICalculator;
import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.util.MidasException;
import com.victor.utilities.datastructures.graph.*;
import com.victor.utilities.visual.VisualAssist;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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

        List<String> names = TopologicalSort.sortThenGetRawData(dependency);
        VisualAssist.print("all calculators needed: ", names);
        if(names.size() == 0) names.add(calc);
        return getCalculatorCopy(names);
    }

    /**
     * instead of use Factory calculator instances
     * this function will reflect the constructor to create a new version of calculators to avoid training parameter pollution
     */
    private static List<ICalculator> getCalculatorCopy(List<String> names) throws MidasException {
        List<ICalculator> calculators = new ArrayList<>();
        CalcParameter parameterCopy = new CalcParameter();
        for(String name : names){
            try {
                Constructor constructor = null;
                constructor = calcName2calculator.get(name).getClass().getConstructor(new Class[]{CalcParameter.class});
                ICalculator calculator = (ICalculator)constructor.newInstance(parameterCopy);
                calculators.add(calculator);
            } catch (NoSuchMethodException e) {
                throw new MidasException("can not init calculator " + name, e);
            } catch (InvocationTargetException e) {
                throw new MidasException("can not init calculator " + name, e);
            } catch (InstantiationException e) {
                throw new MidasException("can not init calculator " + name, e);
            } catch (IllegalAccessException e) {
                throw new MidasException("can not init calculator " + name, e);
            }
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
