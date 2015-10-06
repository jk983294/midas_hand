package com.victor.midas.model.vo;

import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

/**
 * store train result
 */
public class TrainResult {
    @Id
    private long trainId;

    private String strategyName;

    private List<TrainItem> items;

    public TrainResult(String strategyName) {
        items = new ArrayList<>();
        this.strategyName = strategyName;
    }

    public void addResult(TrainItem item){
        items.add(item);
    }

    public long getTrainId() {
        return trainId;
    }

    public void setTrainId(long trainId) {
        this.trainId = trainId;
    }

    public List<TrainItem> getItems() {
        return items;
    }

    public void setItems(List<TrainItem> items) {
        this.items = items;
    }

    public String getStrategyName() {
        return strategyName;
    }

    public void setStrategyName(String strategyName) {
        this.strategyName = strategyName;
    }
}
