package com.victor.midas.calculator.macd.model;

import com.victor.midas.calculator.common.model.FractalType;
import com.victor.midas.calculator.common.model.SignalType;
import com.victor.utilities.utils.MathHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * section means red and green section
 * assume only three limit point, partition section into four part
 * grow1, decay1, grow2, decay2
 * take green section as example, first value continue to grow (negative), then hit the most bottom(first limit point)
 * then rebound, value decay, then hit the relative top(second limit point),
 * then value continue to grow again until third limit point, then decay to red section
 */
public class MacdSection {

    public int fromIndex, toIndex, overrideCnt, overrideDirectCnt;
    public int limitIndex1 = -1, limitIndex2 = -1, limitIndex3 = -1, limitIndex4 = -1;
    public double limit1, limit2, limit3, limit4;
    public double priceLimit;
    public MacdSectionType type;
    public SignalType signalType = SignalType.unknown;
    public MacdSectionStatus status = MacdSectionStatus.grow1;
    public MacdSectionStatus status1 = MacdSectionStatus.grow1;
    public List<TippingPoint> points = new ArrayList<>();           // all points
    public List<TippingPoint> filteredPoints = new ArrayList<>();   // all points
    public TippingPoint latestPoint;

    public static MacdSection create(int index, double bar, double price){
        MacdSection section = new MacdSection();
        section.fromIndex = section.toIndex = section.limitIndex1 = index;
        section.limit1 = bar;
        section.type = MacdSectionType.getType(bar);
        section.latestPoint = new TippingPoint(index, bar, FractalType.Top);
        section.points.add(section.latestPoint);
        section.priceLimit = price;
        return section;
    }

    public void updateAllTippingPoints(int index, double bar){
        double newValueAbs = Math.abs(bar), oldValueAbs = Math.abs(latestPoint.value);
        if(status1 == MacdSectionStatus.grow1){
            if(newValueAbs > oldValueAbs){
                latestPoint.index = index;
                latestPoint.value = bar;
            } else {
                status1 = MacdSectionStatus.decay1;
                latestPoint = new TippingPoint(index, bar, FractalType.Bottom);
                points.add(latestPoint);
                updateFilteredTippingPoints();
            }
        } else if(status1 == MacdSectionStatus.decay1){
            if(newValueAbs > oldValueAbs){
                status1 = MacdSectionStatus.grow1;
                latestPoint = new TippingPoint(index, bar, FractalType.Top);
                points.add(latestPoint);
                updateFilteredTippingPoints();
            } else {
                latestPoint.index = index;
                latestPoint.value = bar;
            }
        }
    }

    public void updateFilteredTippingPoints(){
        if(points.size() > 1){
            TippingPoint point = points.get(points.size() - 2);
            if(filteredPoints.size() == 0){
                filteredPoints.add(point);
            } else {
                int lastIndexOfFilteredPoints = filteredPoints.size() - 1;
                TippingPoint lastFilteredPoints = filteredPoints.get(lastIndexOfFilteredPoints);
                if(point.type == lastFilteredPoints.type){
                    if(point.type == FractalType.Top && MathHelper.isMoreAbs(point.value, lastFilteredPoints.value)){
                        lastFilteredPoints.copy(point);
                    } else if(point.type == FractalType.Bottom && MathHelper.isLessAbs(point.value, lastFilteredPoints.value)){
                        lastFilteredPoints.copy(point);
                    }
                } else if(lastFilteredPoints.type == FractalType.Top){
                    if(Math.abs(point.value) < Math.abs(lastFilteredPoints.value) * 0.4){
                        filteredPoints.add(point);
                    }
                } else if(lastFilteredPoints.type == FractalType.Bottom){
                    if(point.index - lastFilteredPoints.index > 2){
                        filteredPoints.add(point);
                    }
                }
            }
        }
    }

    public boolean shouldSellByStatus(){
        if(signalType == SignalType.sell) return true;
        if(type == MacdSectionType.red){
            if(status == MacdSectionStatus.decay1 || status == MacdSectionStatus.decay2)
                return true;
        } else {
            if(status == MacdSectionStatus.grow1 || status == MacdSectionStatus.grow2)
                return true;
        }
        return false;
    }

    public boolean shouldHoldByStatus(){
        if(type == MacdSectionType.red){
            if(status == MacdSectionStatus.grow1 || status == MacdSectionStatus.grow2)
                return true;
        } else {
            if(status == MacdSectionStatus.decay1 || status == MacdSectionStatus.decay2)
                return true;
        }
        return false;
    }

    public boolean update(int index, double bar, double minPrice, double maxPrice){
        signalType = SignalType.unknown;
        if(type == MacdSectionType.getType(bar)){
            toIndex = index;
            updateAllTippingPoints(index, bar);
            if(type == MacdSectionType.red){
                priceLimit = Math.max(priceLimit, maxPrice);
                updateRed(index, bar);
            } else if(type == MacdSectionType.green){
                priceLimit = Math.min(priceLimit, minPrice);
                updateGreen(index, bar);
            }
            return true;
        }
        return false;
    }



    public void updateRed(int index, double bar){
        if(status == MacdSectionStatus.grow1){
            if(bar > limit1){
                limit1 = bar;
                limitIndex1 = index;
            } else {
                status = MacdSectionStatus.decay1;
                limit2 = bar;
                limitIndex2 = index;
            }
        } else if(status == MacdSectionStatus.decay1){
            if(bar > limit1){
                limit1 = bar;
                limitIndex1 = index;
                limitIndex2 = -1;
                status = MacdSectionStatus.grow1;
            } else if(bar < limit2){
                limit2 = bar;
                limitIndex2 = index;
            } else {
                signalType = SignalType.buy;
                if(Math.abs(limit2) < Math.abs(limit1) * 0.4){
                    status = MacdSectionStatus.grow2;
                    limit3 = bar;
                    limitIndex3 = index;
                }
            }
        } else if(status == MacdSectionStatus.grow2){
            if(bar > limit1){
                limit1 = bar;
                limitIndex1 = index;
                limitIndex2 = limitIndex3 = -1;
                status = MacdSectionStatus.grow1;
            } else if(bar < limit3){
                limit4 = bar;
                limitIndex4 = index;
                status = MacdSectionStatus.decay2;
            } else {
                limit3 = bar;
                limitIndex3 = index;
            }
        } else {    // decay2
            if(bar > limit1){
                limit1 = bar;
                limitIndex1 = index;
                limitIndex2 = limitIndex3 = -1;
                status = MacdSectionStatus.grow1;
            } else if(bar > limit3){
                limit3 = bar;
                limitIndex3 = index;
                status = MacdSectionStatus.grow2;
            } else if(bar < limit4){
                limit4 = bar;
                limitIndex4 = index;
            } else {
                // trigger buy signal
                signalType = SignalType.buy;
            }
        }
    }

    public void updateGreen(int index, double bar){
        if(status == MacdSectionStatus.grow1){
            if(bar < limit1){
                limit1 = bar;
                limitIndex1 = index;
            } else {
                status = MacdSectionStatus.decay1;
                signalType = SignalType.buy;
                limit2 = bar;
                limitIndex2 = index;
            }
        } else if(status == MacdSectionStatus.decay1){
            if(bar < limit1){
                limit1 = bar;
                limitIndex1 = index;
                limitIndex2 = -1;
                status = MacdSectionStatus.grow1;
                signalType = SignalType.sell;
            } else if(bar > limit2){
                limit2 = bar;
                limitIndex2 = index;
            } else {
                signalType = SignalType.sell;
                if(Math.abs(limit2) < Math.abs(limit1) * 0.4){
                    status = MacdSectionStatus.grow2;
                    limit3 = bar;
                    limitIndex3 = index;
                }
            }
        } else if(status == MacdSectionStatus.grow2){
            if(bar < limit1){
                limit1 = bar;
                limitIndex1 = index;
                limitIndex2 = limitIndex3 = -1;
                status = MacdSectionStatus.grow1;
                signalType = SignalType.sell;
            } else if(bar > limit3){
                limit4 = bar;
                limitIndex4 = index;
                status = MacdSectionStatus.decay2;
                signalType = SignalType.buy;
            } else {
                limit3 = bar;
                limitIndex3 = index;
            }
        } else {    // decay2
            if(bar < limit1){
                limit1 = bar;
                limitIndex1 = index;
                limitIndex2 = limitIndex3 = -1;
                status = MacdSectionStatus.grow1;
                signalType = SignalType.sell;
            } else if(bar < limit3){
                limit3 = bar;
                limitIndex3 = index;
                status = MacdSectionStatus.grow2;
                signalType = SignalType.sell;
            } else if(bar > limit4){
                limit4 = bar;
                limitIndex4 = index;
            } else {
                // trigger sell signal
                signalType = SignalType.sell;
            }
        }
    }

    public int getTotalDays(){
        return toIndex - fromIndex + 1;
    }

    public int getFromIndex() {
        return fromIndex;
    }

    public void setFromIndex(int fromIndex) {
        this.fromIndex = fromIndex;
    }

    public int getToIndex() {
        return toIndex;
    }

    public void setToIndex(int toIndex) {
        this.toIndex = toIndex;
    }

    public int getLimitIndex1() {
        return limitIndex1;
    }

    public void setLimitIndex1(int limitIndex1) {
        this.limitIndex1 = limitIndex1;
    }

    public int getLimitIndex2() {
        return limitIndex2;
    }

    public void setLimitIndex2(int limitIndex2) {
        this.limitIndex2 = limitIndex2;
    }

    public double getLimit1() {
        return limit1;
    }

    public void setLimit1(double limit1) {
        this.limit1 = limit1;
    }

    public double getLimit2() {
        return limit2;
    }

    public void setLimit2(double limit2) {
        this.limit2 = limit2;
    }

    public MacdSectionType getType() {
        return type;
    }

    public void setType(MacdSectionType type) {
        this.type = type;
    }

    public MacdSectionStatus getStatus() {
        return status;
    }

    public void setStatus(MacdSectionStatus status) {
        this.status = status;
    }

    public MacdSection() {

    }

    @Override
    public String toString() {
        return "MacdSection{" +
                "fromIndex=" + fromIndex +
                ", toIndex=" + toIndex +
                ", limitIndex1=" + limitIndex1 +
                ", limitIndex2=" + limitIndex2 +
                ", limit1=" + limit1 +
                ", limit2=" + limit2 +
                ", type=" + type +
                ", status=" + status +
                '}';
    }
}
