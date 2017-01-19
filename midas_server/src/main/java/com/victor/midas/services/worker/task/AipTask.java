package com.victor.midas.services.worker.task;

import com.victor.midas.calculator.IndexCalculator;
import com.victor.midas.model.vo.AipResult;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.services.worker.common.TaskBase;
import com.victor.midas.util.MidasConstants;
import com.victor.utilities.utils.PerformanceUtil;
import com.victor.utilities.utils.TimeHelper;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Automatic Investment Plan
 */
@Component
@Scope("prototype")
public class AipTask extends TaskBase {

    private static final Logger logger = Logger.getLogger(AipTask.class);
    private static final String description = "Automatic Investment Plan Calculation Task";

    private static final double monthlyInvestMoney = 10000.0;

    @Override
    public void doTask() throws Exception {
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

        for (int i = 1; i < 45; i++) {
            List<AipResult> filtered = getFiltered(results, i);
            DescriptiveStatistics perf = new DescriptiveStatistics();
            for (AipResult result : filtered){
                perf.addValue(result.performanceMonthly);
            }
            System.out.println(perf.getMean() + "\t" + perf.getStandardDeviation());
        }
        

//        Collections.sort(filtered);
//        try {
//            IoHelper.toJsonFileWithIndent(filtered, "D:\\AipResult.json");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        System.out.println(perf.getMean() + " : " + perf.getStandardDeviation());
    }

    List<AipResult> getFiltered(List<AipResult> results, int month){
        List<AipResult> filtered = new ArrayList<>();
        for (AipResult result : results){
            if(result.monthCount > month){
                filtered.add(result);
            }
        }
        return filtered;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
