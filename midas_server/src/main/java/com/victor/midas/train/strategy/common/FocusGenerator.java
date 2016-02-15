package com.victor.midas.train.strategy.common;

import com.victor.midas.model.db.DayFocusDb;
import com.victor.midas.model.train.StockDecision;
import com.victor.midas.model.train.StockTrain;
import com.victor.midas.model.vo.FocusStock;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.util.MidasException;
import com.victor.utilities.algorithm.search.BinarySearch;

import java.util.ArrayList;
import java.util.List;

/**
 * generate focus stock for each day
 */
public class FocusGenerator {

    private List<DayFocusDb> focus;

    private List<StockTrain> tradableStocks;
    /** benchmark date from market index*/
    private int[] marketDates;

    public FocusGenerator(List<StockTrain> tradableStocks, int[] marketDates) {
        this.tradableStocks = tradableStocks;
        this.marketDates = marketDates;
        focus = new ArrayList<>();
    }

    public void execute() throws MidasException {
        int marketCob;
        List<InnerStock> stocks = assemblyData();
        for (int i = 0; i < marketDates.length; i++) {
            marketCob = marketDates[i];
            List<FocusStock> focusSingleDay = new ArrayList<>();
            for (InnerStock stock : stocks){
                if(stock.isSameDayWithIndex(marketCob)){
                    if(StockDecision.isWillBuyDecision(stock.currentProd())){
                        focusSingleDay.add(generateFocusStock(stock));
                    }
                }
                stock.advanceIndex(marketCob);
            }
            if(focusSingleDay.size() > 0){
                focus.add(new DayFocusDb(marketCob, focusSingleDay));
            }
        }
    }

    private FocusStock generateFocusStock(InnerStock stock){
        return new FocusStock(stock.code, 0d, StockDecision.values()[stock.currentProd()].toString());
    }

    private List<InnerStock> assemblyData() throws MidasException {
        List<InnerStock> stocks = new ArrayList<>();
        for (StockTrain stock : tradableStocks){
            stocks.add(new InnerStock(stock.getStock(), marketDates[0]));
        }
        return stocks;
    }

    public List<DayFocusDb> getFocus() {
        return focus;
    }

    public void setFocus(List<DayFocusDb> focus) {
        this.focus = focus;
    }

    private class InnerStock {
        public int[] dates;
        public int[] prod;
        public int index;
        public int len;
        public String code;

        public InnerStock(StockVo stock, int firstDay) throws MidasException {
            dates = stock.getDatesInt();
            prod = (int[])stock.queryCmpIndex("prod");
            code = stock.getStockName();
            len = dates.length;
            index = Math.abs(BinarySearch.find(firstDay, dates));
        }

        /**
         * check if current date in stock is the same date with market index
         */
        public boolean isSameDayWithIndex(int marketCob){
            return index < len && dates[index] == marketCob;
        }

        /**
         * advance current date in stock, make it the next day with market index
         */
        public void advanceIndex(int marketCob){
            if(isSameDayWithIndex(marketCob)) ++index;
        }

        public int currentProd(){
            return prod[index];
        }
    }
}
