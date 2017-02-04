package com.victor.midas.services.worker.task;

import com.victor.midas.calculator.IndexCalculator;
import com.victor.midas.model.db.misc.MiscGenericObject;
import com.victor.midas.model.vo.AipResult;
import com.victor.midas.model.vo.AipResults;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.services.worker.common.TaskBase;
import com.victor.midas.util.MidasConstants;
import com.victor.utilities.model.SimpleStatisticObject;
import com.victor.utilities.utils.PerformanceUtil;
import com.victor.utilities.utils.TimeHelper;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Automatic Investment Plan
 */
@Component
@Scope("prototype")
public class AipTask extends TaskBase {

    private static final Logger logger = Logger.getLogger(AipTask.class);
    private static final String description = "Automatic Investment Plan Calculation Task";

    private static final double monthlyInvestMoney = 10000.0;

    private int cobFrom, cobTo;

    @Override
    public void doTask() throws Exception {
        parseOptions();

        List<AipResult> results = new ArrayList<>();
        List<StockVo> stocks = stocksService.queryAllStock();
        IndexCalculator calculator = new IndexCalculator(stocks, "weekly");

        for (StockVo stock : calculator.getFilterUtil().getTradableStocks()){
            AipResult result = simulate(stock);
            if(result != null && result.endCob > 0){
                results.add(result);
            }
        }

        saveResults(results);

        PerformanceUtil.manuallyGC(stocks);
        logger.info( description + " complete...");
    }

    private AipResult simulate(StockVo stock){
        int[] dates = stock.getDatesInt();
        double[] start = stock.queryCmpIndexDoubleWithNull(MidasConstants.INDEX_NAME_START);
        double[] end = stock.queryCmpIndexDoubleWithNull(MidasConstants.INDEX_NAME_END);
        int len = dates.length;
        if(len < 10) return null;

        int startMonth, currentMonth;
        startMonth = TimeHelper.cob2month(dates[0]);
        AipResult result = new AipResult(stock.getStockName());

        for (int i = 0; i < len - 1; i++) {
            if(cobFrom > 0 && dates[i] < cobFrom) continue;
            if(cobTo > 0 && dates[i] > cobTo) continue;

            currentMonth = TimeHelper.cob2month(dates[i]);
            // ignore first two month in case it is new stock
            if(currentMonth - startMonth < 2) continue;

            // automatic investment at month end
            if(currentMonth != TimeHelper.cob2month(dates[i + 1])){
                result.buyStock(start[i], monthlyInvestMoney, dates[i]);
            }
        }

        result.calculatePerformance(end[len - 1]);
        return result;
    }

    private void saveResults(List<AipResult> results) {
        int maxMonth = getMaxMonth(results);
        if(maxMonth <= 0) return;

        List<SimpleStatisticObject> statisticObjects = new ArrayList<>();

        for (int i = 1; i < maxMonth; i++) {
            List<AipResult> filtered = getFiltered(results, i);
            DescriptiveStatistics perf = new DescriptiveStatistics();
            for (AipResult result : filtered){
                perf.addValue(result.performanceMonthly);
            }

            if(perf.getN() > 0){
                statisticObjects.add(new SimpleStatisticObject(i, perf.getMean(), perf.getStandardDeviation()));
            }
        }

        Collections.sort(results);
        for (int i = 0; i < results.size(); i++) {
            results.get(i).performanceOrder = i;
        }

        AipResults aipResults = new AipResults(results, statisticObjects);
        miscDao.saveMiscGenericObject(new MiscGenericObject<>(MidasConstants.MISC_AIP_RESULT, aipResults));
    }

    List<AipResult> getFiltered(List<AipResult> results, int month){
        return results.stream().filter(result -> result.monthCount > month).collect(Collectors.toList());
    }

    int getMaxMonth(List<AipResult> results){
        int maxMonth = -1;
        for (AipResult result : results){
            if(result.monthCount > maxMonth){
                maxMonth = result.monthCount;
            }
        }
        return maxMonth;
    }

    private void parseOptions(){
        cobFrom = cobTo = -1;
        if(params.size() == 2){
            cobFrom = Integer.valueOf(params.get(0));
            cobTo = Integer.valueOf(params.get(1));
        } else if(params.size() == 1){
            cobFrom = Integer.valueOf(params.get(0));
        }
    }

    @Override
    public String getDescription() {
        return description;
    }
}
