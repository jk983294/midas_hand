package com.victor.midas.model.db.misc;

import java.util.List;

/**
 * contain a list of sampled cobs
 */
public class SampleCobDb extends MiscBase {

    private List<Integer> cobs;

    public SampleCobDb() {
    }

    public SampleCobDb(String miscName, List<Integer> cobs) {
        super(miscName);
        this.cobs = cobs;
    }

    public List<Integer> getCobs() {
        return cobs;
    }

    public void setCobs(List<Integer> cobs) {
        this.cobs = cobs;
    }

    @Override
    public String toString() {
        return "SampleCobDb{" +
                "cobs=" + cobs +
                '}';
    }
}
