package com.victor.midas.model.vo;

import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.model.train.TradeRecord;

import java.util.List;
import java.util.Map;

/**
 * used to record for one discrete parameter train
 */
public class TrainItem {

    private CalcParameter parameter;

    private List<TradeRecord> history;

    private double fitness;

    private double R;

    private double winProbability;

    private int opportunity;

    public TrainItem() {
    }

    public TrainItem(CalcParameter parameter, List<TradeRecord> history, double fitness) {
        this.parameter = parameter;
        this.history = history;
        this.fitness = fitness;
    }

    public CalcParameter getParameter() {
        return parameter;
    }

    public void setParameter(CalcParameter parameter) {
        this.parameter = parameter;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public List<TradeRecord> getHistory() {
        return history;
    }

    public void setHistory(List<TradeRecord> history) {
        this.history = history;
    }

    public double getR() {
        return R;
    }

    public void setR(double r) {
        R = r;
    }

    public int getOpportunity() {
        return opportunity;
    }

    public void setOpportunity(int opportunity) {
        this.opportunity = opportunity;
    }

    public double getWinProbability() {
        return winProbability;
    }

    public void setWinProbability(double winProbability) {
        this.winProbability = winProbability;
    }
}
