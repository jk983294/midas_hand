package com.victor.midas.train.strategy.single;

import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.calculator.common.CalcUtil;
import com.victor.midas.calculator.util.MathStockUtil;
import com.victor.midas.model.common.KState;
import com.victor.midas.model.train.StockDecision;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.train.strategy.common.SingleStockStrategyBase;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;

import java.util.ArrayList;
import java.util.List;

/**
 * only trade single strategy
 */
public class LgtStrategyS extends SingleStockStrategyBase {

    public final static String STRATEGY_NAME = "LgtStrategyS";

    private CalcUtil calcUtil;

    private double buyPrice;
    private double holdHighestEnd;

    private double[] changePct;
    private double[] upShadowPct;
    private double[] downShadowPct;
    private double[] middleShadowPct;
    private double[] pMaYearD1;
    private double[] pMaLongD1;
    private int[] k_state;
    private double[] vMa;
    private int[] lgt;
    private double[] signals;

    public LgtStrategyS(List<StockVo> allStocks, CalcParameter parameter) throws MidasException {
        super(allStocks, parameter, false);
        discreteCalculators = new ArrayList<>();
        continuousCalculators = new ArrayList<>();

        calcUtil = new CalcUtil();

//        lowbounds = new double[]{-1.1, -1.1};
//        upbounds = new double[]{1.1, 1.1};

        //Discrete Calculators
//        discreteCalculators.add(new IndexPriceMA(new SMA()));

        //Continuous Calculators
//        continuousCalculators.add(new PriceMaTangle());
    }

    @Override
    public void checkCouldBuyToday(int index) throws MidasException {
        // will buy, and today down open, then buy at start
        if(MathStockUtil.calculateChangePct(end[index - 1], start[index]) < 0.02){
            decision = StockDecision.BUY_AT_START;
            buyPrice = start[index];
            holdHighestEnd = end[index];
        } else {
            decision = StockDecision.WATCH;
        }
    }

    @Override
    public StockDecision checkIfSell(int index){
        if(KState.isSellDecision(k_state[index])) return StockDecision.SELL_STOP_K_STATE;
        //stop loss
        if(MathStockUtil.calculateChangePct(buyPrice, end[index]) < -0.02){
            return StockDecision.SELL_STOP_LOSS_PERCENTAGE;
        }
        // moving stop win
        if(MathStockUtil.calculateChangePct(holdHighestEnd, end[index]) < -0.05){
            return StockDecision.SELL_STOP_WIN_MOVING;
        }
        if(upShadowPct[index] > 0.05){
            return StockDecision.SELL_STOP_WIN_UP_SHADOW;
        }
        if(MathStockUtil.calculateChangePct(buyPrice, max[index]) > 0.20){
            return StockDecision.SELL_STOP_WIN_PERCENTAGE;
        }
        if(calcUtil.isBigVolume(index)){
            return StockDecision.SELL_STOP_WIN_HUGE_VOLUME;
        }
        return StockDecision.HOLD;
    }

    @Override
    public void maintainDataWhenHolding(int index) {
        holdHighestEnd = Math.max(holdHighestEnd, end[index]);
    }

    @Override
    public boolean checkCouldBuyTomorrow(int index) {
        return StockDecision.couldBuy(decision) && lgt[index] > 1;
    }

    @Override
    public void initStrategySpecifiedData() throws MidasException {
        upShadowPct = (double[])stock.queryCmpIndex("k_u");
        downShadowPct = (double[])stock.queryCmpIndex("k_d");
        middleShadowPct = (double[])stock.queryCmpIndex("k_m");
        changePct = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_CHANGEPCT);
        pMaYearD1 = (double[])stock.queryCmpIndex("pMa60D1");
        pMaLongD1 = (double[])stock.queryCmpIndex("pMa20D1");
        vMa = (double[])stock.queryCmpIndex("vMaMedium");
        k_state = (int[])stock.queryCmpIndex("k_state");
        lgt = (int[])stock.queryCmpIndex("lgt");
    }

    @Override
    public String getStrategyName() {
        return STRATEGY_NAME;
    }

    @Override
    public List<int[]> getDiscreteParams() {
        return null;
    }

    @Override
    public void applyParameters(double[] params) {
    }

    @Override
    public void applyParameters(int[] discreteParams) {
    }



}
