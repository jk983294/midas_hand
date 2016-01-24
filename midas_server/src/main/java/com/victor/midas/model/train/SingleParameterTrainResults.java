package com.victor.midas.model.train;


import com.victor.midas.model.db.misc.MiscBase;

import java.util.ArrayList;
import java.util.List;

public class SingleParameterTrainResults extends MiscBase {

    private List<SingleParameterTrainResult> results = new ArrayList<>();
    private TrainType type;

    public SingleParameterTrainResults() {
    }
    public SingleParameterTrainResults(String miscName) {
        super(miscName);
    }

    public void add(SingleParameterTrainResult result){
        results.add(result);
    }

    public SingleParameterTrainResult getLastResults() {
        return results.size() > 0 ? results.get(results.size() - 1): null;
    }

    public void setType(TrainType type) {
        this.type = type;
    }

    public List<SingleParameterTrainResult> getResults() {
        return results;
    }

    public void setResults(List<SingleParameterTrainResult> results) {
        this.results = results;
    }

    public TrainType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "SingleParameterTrainResults{" +
                "results=" + results +
                ", type=" + type +
                '}';
    }
}
