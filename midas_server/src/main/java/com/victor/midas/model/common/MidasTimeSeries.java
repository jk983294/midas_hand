package com.victor.midas.model.common;

import org.springframework.data.annotation.Id;

public class MidasTimeSeries {

    @Id
    public String name;

    public int[] cobs;

    public double[] values;

    public int[] getCobs() {
        return cobs;
    }

    public void setCobs(int[] cobs) {
        this.cobs = cobs;
    }

    public double[] getValues() {
        return values;
    }

    public void setValues(double[] values) {
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
