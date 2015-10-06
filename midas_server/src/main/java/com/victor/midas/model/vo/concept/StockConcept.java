package com.victor.midas.model.vo.concept;

/**
 * stock concept
 */
public class StockConcept {

    String code;

    String name;

    public StockConcept() {
    }

    public StockConcept(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * the same name should belong to same concept
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StockConcept that = (StockConcept) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "StockConcepts{" +
                "code=" + code +
                ", name='" + name + '\'' +
                '}';
    }
}
