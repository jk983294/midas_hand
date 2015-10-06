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
    /** benchmark date from SH index*/
    private int[] datesSH;

    public FocusGenerator(List<StockTrain> tradableStocks, int[] datesSH) {
        this.tradableStocks = tradableStocks;
        this.datesSH = datesSH;
        focus = new ArrayList<>();
    }

    public void execute() throws MidasException {
        int dateSH;
        List<InnerStock> stocks = assemblyData();
        for (int i = 0; i < datesSH.length; i++) {
            dateSH = datesSH[i];
            List<FocusStock> focusSingleDay = new ArrayList<>();
            for (InnerStock stock : stocks){
                if(stock.isSameDayWithIndex(dateSH)){
                    if(StockDecision.isWillBuyDecision(stock.currentProd())){
                        focusSingleDay.add(generateFocusStock(stock));
                    }
                }
                stock.advanceIndex(dateSH);
            }
            if(focusSingleDay.size() > 0){
                focus.add(new DayFocusDb(dateSH, focusSingleDay));
            }
        }
    }

    private FocusStock generateFocusStock(InnerStock stock){
        return new FocusStock(stock.code, 0d, StockDecision.values()[stock.currentProd()].toString());
    }

    private List<InnerStock> assemblyData() throws MidasException {
        List<InnerStock> stocks = new ArrayList<>();
        for (StockTrain stock : tradableStocks){
            stocks.add(new InnerStock(stock.getStock(), datesSH[0]));
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
         * check if current date in stock is the same date with SH index
         */
        public boolean isSameDayWithIndex(int dateSH){
            return index < len && dates[index] == dateSH;
        }

        /**
         * advance current date in stock, make it the next day with SH index
         */
        public void advanceIndex(int dateSH){
            if(isSameDayWithIndex(dateSH)) ++index;
        }

        public int currentProd(){
            return prod[index];
        }
    }
}
