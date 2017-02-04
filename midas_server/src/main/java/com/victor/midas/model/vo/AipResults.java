package com.victor.midas.model.vo;


import com.victor.utilities.model.SimpleStatisticObject;

import java.util.List;

public class AipResults {

    public List<AipResult> results;
    public List<SimpleStatisticObject> statisticObjects;

    public AipResults(List<AipResult> results, List<SimpleStatisticObject> statisticObjects) {
        this.results = results;
        this.statisticObjects = statisticObjects;
    }

    public AipResults() {
    }

    public List<AipResult> getResults() {
        return results;
    }

    public void setResults(List<AipResult> results) {
        this.results = results;
    }

    public List<SimpleStatisticObject> getStatisticObjects() {
        return statisticObjects;
    }

    public void setStatisticObjects(List<SimpleStatisticObject> statisticObjects) {
        this.statisticObjects = statisticObjects;
    }
}
