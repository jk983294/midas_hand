package com.victor.midas.calculator.util.model;

import com.victor.midas.calculator.macd.model.MacdSectionType;
import com.victor.midas.calculator.util.MathStockUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * two lines (fast line and slow line) go up and down forms section
 * limit means the fast line leave percentage against slow line
 */
public class LineCrossSection {

    public int fromIndex, toIndex;
    public int limitIndex = -1, priceLimitIndex = -1;
    public double limit, priceLimit;
    public MacdSectionType type;
    public List<Integer> breakoutIndexes = new ArrayList<>();

    public static LineCrossSection create(int index, double priceShort, double priceLong){
        LineCrossSection section = new LineCrossSection();
        section.fromIndex = section.toIndex = section.limitIndex = section.priceLimitIndex = index;
        section.type = MacdSectionType.getType(priceShort, priceLong);
        section.priceLimit = priceShort;
        section.limit = Math.abs(MathStockUtil.calculateChangePct(priceLong, priceShort));
        return section;
    }

    public boolean update(int index, double priceShort, double priceLong){
        if(type == MacdSectionType.getType(priceShort, priceLong)){
            toIndex = index;
            if(type == MacdSectionType.red){
                if(priceLimit < priceShort){
                    priceLimit = priceShort;
                    priceLimitIndex = index;
                }
                double currentLimit = MathStockUtil.calculateChangePct(priceLong, priceShort);
                if(limit < currentLimit){
                    limit = currentLimit;
                    limitIndex = index;
                }
            } else if(type == MacdSectionType.green){
                if(priceLimit > priceShort){
                    priceLimit = priceShort;
                    priceLimitIndex = index;
                }
                double currentLimit = MathStockUtil.calculateChangePct(priceShort, priceLong);
                if(limit < currentLimit){
                    limit = currentLimit;
                    limitIndex = index;
                }
            }
            return true;
        }
        return false;
    }

    public int getSectionDayCount(){
        return toIndex - fromIndex + 1;
    }

    public LineCrossSection() {
    }

    @Override
    public String toString() {
        return "LineCrossSection{" +
                "fromIndex=" + fromIndex +
                ", toIndex=" + toIndex +
                ", limitIndex=" + limitIndex +
                ", priceLimitIndex=" + priceLimitIndex +
                ", limit=" + limit +
                ", priceLimit=" + priceLimit +
                ", type=" + type +
                '}';
    }
}
