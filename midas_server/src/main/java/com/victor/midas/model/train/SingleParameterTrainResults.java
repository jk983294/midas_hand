package com.victor.midas.model.train;


import java.util.ArrayList;
import java.util.List;

public class SingleParameterTrainResults {

    private List<SingleParameterTrainResult> results = new ArrayList<>();
    private TrainType type;

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
}
