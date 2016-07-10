package com.victor.midas.model.vo;


import java.util.ArrayList;
import java.util.List;

public class MidasBond implements Comparable<MidasBond> {

    public int cob;

    public List<Double> term = new ArrayList<>();

    public List<Double> yield = new ArrayList<>();

    public List<String> termName = new ArrayList<>();

    public int getCob() {
        return cob;
    }

    public void setCob(int cob) {
        this.cob = cob;
    }

    public List<Double> getTerm() {
        return term;
    }

    public void setTerm(List<Double> term) {
        this.term = term;
    }

    public List<Double> getYield() {
        return yield;
    }

    public void setYield(List<Double> yield) {
        this.yield = yield;
    }

    public List<String> getTermName() {
        return termName;
    }

    public void setTermName(List<String> termName) {
        this.termName = termName;
    }

    @Override
    public String toString() {
        return "MidasBond{" +
                "cob=" + cob +
                ", term=" + term +
                ", yield=" + yield +
                ", termName=" + termName +
                '}';
    }

    @Override
    public int compareTo(MidasBond o) {
        return Integer.valueOf(cob).compareTo(o.cob);
    }
}
