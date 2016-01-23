package com.victor.midas.model.train;


public class SingleParameterTrainResult {

    public double parameter, dayPerformance, kellyAnnualizedPerformance, stdDev, kellyFraction;
    public double d1Open, d1Close, d1High, d1Low;
    public double d1OpenStdDev, d1CloseStdDev, d1HighStdDev, d1LowStdDev;
    public double d2Open, d2Close, d2High, d2Low;
    public double d2OpenStdDev, d2CloseStdDev, d2HighStdDev, d2LowStdDev;
    public long cnt;


    public double getParameter() {
        return parameter;
    }

    public void setParameter(double parameter) {
        this.parameter = parameter;
    }

    public double getDayPerformance() {
        return dayPerformance;
    }

    public void setDayPerformance(double dayPerformance) {
        this.dayPerformance = dayPerformance;
    }

    public double getKellyAnnualizedPerformance() {
        return kellyAnnualizedPerformance;
    }

    public void setKellyAnnualizedPerformance(double kellyAnnualizedPerformance) {
        this.kellyAnnualizedPerformance = kellyAnnualizedPerformance;
    }

    public double getStdDev() {
        return stdDev;
    }

    public void setStdDev(double stdDev) {
        this.stdDev = stdDev;
    }

    public double getKellyFraction() {
        return kellyFraction;
    }

    public void setKellyFraction(double kellyFraction) {
        this.kellyFraction = kellyFraction;
    }

    public double getD1Open() {
        return d1Open;
    }

    public void setD1Open(double d1Open) {
        this.d1Open = d1Open;
    }

    public double getD1Close() {
        return d1Close;
    }

    public void setD1Close(double d1Close) {
        this.d1Close = d1Close;
    }

    public double getD1High() {
        return d1High;
    }

    public void setD1High(double d1High) {
        this.d1High = d1High;
    }

    public double getD1Low() {
        return d1Low;
    }

    public void setD1Low(double d1Low) {
        this.d1Low = d1Low;
    }

    public double getD1OpenStdDev() {
        return d1OpenStdDev;
    }

    public void setD1OpenStdDev(double d1OpenStdDev) {
        this.d1OpenStdDev = d1OpenStdDev;
    }

    public double getD1CloseStdDev() {
        return d1CloseStdDev;
    }

    public void setD1CloseStdDev(double d1CloseStdDev) {
        this.d1CloseStdDev = d1CloseStdDev;
    }

    public double getD1HighStdDev() {
        return d1HighStdDev;
    }

    public void setD1HighStdDev(double d1HighStdDev) {
        this.d1HighStdDev = d1HighStdDev;
    }

    public double getD1LowStdDev() {
        return d1LowStdDev;
    }

    public void setD1LowStdDev(double d1LowStdDev) {
        this.d1LowStdDev = d1LowStdDev;
    }

    public double getD2Open() {
        return d2Open;
    }

    public void setD2Open(double d2Open) {
        this.d2Open = d2Open;
    }

    public double getD2Close() {
        return d2Close;
    }

    public void setD2Close(double d2Close) {
        this.d2Close = d2Close;
    }

    public double getD2High() {
        return d2High;
    }

    public void setD2High(double d2High) {
        this.d2High = d2High;
    }

    public double getD2Low() {
        return d2Low;
    }

    public void setD2Low(double d2Low) {
        this.d2Low = d2Low;
    }

    public double getD2OpenStdDev() {
        return d2OpenStdDev;
    }

    public void setD2OpenStdDev(double d2OpenStdDev) {
        this.d2OpenStdDev = d2OpenStdDev;
    }

    public double getD2CloseStdDev() {
        return d2CloseStdDev;
    }

    public void setD2CloseStdDev(double d2CloseStdDev) {
        this.d2CloseStdDev = d2CloseStdDev;
    }

    public double getD2HighStdDev() {
        return d2HighStdDev;
    }

    public void setD2HighStdDev(double d2HighStdDev) {
        this.d2HighStdDev = d2HighStdDev;
    }

    public double getD2LowStdDev() {
        return d2LowStdDev;
    }

    public void setD2LowStdDev(double d2LowStdDev) {
        this.d2LowStdDev = d2LowStdDev;
    }

    public long getCnt() {
        return cnt;
    }

    public void setCnt(long cnt) {
        this.cnt = cnt;
    }

    @Override
    public String toString() {
        return "SingleParameterTrainResult{" +
                "parameter=" + parameter +
                ", dayPerformance=" + dayPerformance +
                ", kellyAnnualizedPerformance=" + kellyAnnualizedPerformance +
                ", stdDev=" + stdDev +
                ", kellyFraction=" + kellyFraction +
                ", d1Open=" + d1Open +
                ", d1Close=" + d1Close +
                ", d1High=" + d1High +
                ", d1Low=" + d1Low +
                ", d1OpenStdDev=" + d1OpenStdDev +
                ", d1CloseStdDev=" + d1CloseStdDev +
                ", d1HighStdDev=" + d1HighStdDev +
                ", d1LowStdDev=" + d1LowStdDev +
                ", d2Open=" + d2Open +
                ", d2Close=" + d2Close +
                ", d2High=" + d2High +
                ", d2Low=" + d2Low +
                ", d2OpenStdDev=" + d2OpenStdDev +
                ", d2CloseStdDev=" + d2CloseStdDev +
                ", d2HighStdDev=" + d2HighStdDev +
                ", d2LowStdDev=" + d2LowStdDev +
                ", cnt=" + cnt +
                '}';
    }
}
