package com.victor.midas.model.db;

import com.victor.midas.model.vo.FocusStock;
import org.springframework.data.annotation.Id;

import java.util.List;

/**
 * every day has some focused stocks
 */
public class DayFocusDb {

    @Id
    private int date;

    private List<FocusStock> stocks;

    public DayFocusDb() {
    }

    public DayFocusDb(int date, List<FocusStock> stocks) {
        this.date = date;
        this.stocks = stocks;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public List<FocusStock> getStocks() {
        return stocks;
    }

    public void setStocks(List<FocusStock> stocks) {
        this.stocks = stocks;
    }
}
