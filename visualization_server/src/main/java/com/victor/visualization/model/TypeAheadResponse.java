package com.victor.visualization.model;

import java.util.List;

public class TypeAheadResponse {
    private List<String> tips;
    private String status;

    public TypeAheadResponse() {}

    public TypeAheadResponse(List<String> tips, String status) {
        this.tips = tips;
        this.status = status;
    }

    public List<String> getTips() {
        return tips;
    }

    public void setTips(List<String> tips) {
        this.tips = tips;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TypeAheadResponse{" +
                "tips=" + tips +
                ", status='" + status + '\'' +
                '}';
    }
}
