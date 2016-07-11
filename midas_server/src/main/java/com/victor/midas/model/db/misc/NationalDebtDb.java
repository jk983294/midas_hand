package com.victor.midas.model.db.misc;

import com.victor.midas.model.vo.MidasBond;

import java.util.List;

/**
 * contain a list of stock names
 */
public class NationalDebtDb extends MiscBase {

    private List<MidasBond> bonds;

    public NationalDebtDb() {
    }

    public NationalDebtDb(String miscName, List<MidasBond> bonds) {
        super(miscName);
        this.bonds = bonds;
    }

    @Override
    public String toString() {
        return "NationalDebtDb{" +
                "bonds=" + bonds +
                '}';
    }

    public List<MidasBond> getBonds() {
        return bonds;
    }

    public void setBonds(List<MidasBond> bonds) {
        this.bonds = bonds;
    }
}
