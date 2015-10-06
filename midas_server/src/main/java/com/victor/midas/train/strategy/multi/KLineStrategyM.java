package com.victor.midas.train.strategy.multi;

import com.victor.midas.calculator.util.MathStockUtil;
import com.victor.midas.model.train.StockDecision;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.train.strategy.common.MultiStockStrategyBase;
import com.victor.midas.train.strategy.common.SingleStockStrategyBase;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;

import java.util.ArrayList;
import java.util.List;

/**
 * only trade single strategy
 */
/*
public class KLineStrategyM extends MultiStockStrategyBase {

    public final static String STRATEGY_NAME = "KLineStrategyM";

    private double buyPrice;
    private double holdHighestEnd;


    private double[] changePct;
    private double[] upShadowPct;
    private double[] downShadowPct;
    private double[] middleShadowPct;
    private double[] pD1;
    private double[] vMa;
    private double[] k_sig;
    private double[] signals;

    public KLineStrategyM(List<StockVo> allStocks, CalcParameter parameter) throws MidasException {
        super(allStocks, parameter, false);
        discreteCalculators = new ArrayList<>();
        continuousCalculators = new ArrayList<>();

//        lowbounds = new double[]{-1.1, -1.1};
//        upbounds = new double[]{1.1, 1.1};

        //Discrete Calculators
//        discreteCalculators.add(new IndexPriceMA(new SMA()));

        //Continuous Calculators
//        continuousCalculators.add(new PriceMaTangle());
    }

    @Override
    public void tradeSimulationPerStock() {
        boolean willBuy = false;        // when set up, next day will buy according to some criteria
        boolean hasBuy = false;         // when buy, set up. then in the following day, seek sell point
        StockDecision decision = StockDecision.WATCH;
        // go through time line
        for (int i = startIndex; i <= endIndex; i++) {
            if(willBuy){
                // will buy, and today down open, then buy at start
                if(MathStockUtil.calculateChangePct(end[i - 1], start[i]) < 0){
                    decision = StockDecision.BUY_AT_START;
                    holdHighestEnd = buyPrice = start[i];
                    hasBuy = true;
                    willBuy = false;
                } else if(changePct[i] < -0.03){ // buy at end of day
                    decision = StockDecision.BUY_AT_END;
                    holdHighestEnd = buyPrice = end[i];
                    hasBuy = true;
                    willBuy = false;
                }else {
                    willBuy = false;
                }
            } else if(hasBuy){    // check if it is sell point
                holdHighestEnd = Math.max(holdHighestEnd, end[i]);
                decision = checkIfSell(i);
//                if(StockDecision.isSellDecision(decision)){
//                    signals[i] = sellStrategy;
//                    hasBuy = false;
//                }
            } else if(!hasBuy && !willBuy && k_sig[i] >= 3 && pD1[i] > 0){
                willBuy = true;
            }


            // take action according to yesterday's decision make
            if(StockDecision.isBuyDecision(decision)){
                portfolioItem.buyAll(decision);
            } else if(StockDecision.isSellDecision(decision)){
                portfolioItem.sellAll(decision);
            }

            signals[i] = StockDecision.WATCH.ordinal();
        }
    }

    private StockDecision checkIfSell(int index){
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
        if(total[index] / vMa[index - 1] > 1.5){
            return StockDecision.SELL_STOP_WIN_HUGE_VOLUME;
        }
        return StockDecision.HOLD;
    }

    @Override
    public void initStrategySpecifiedData() throws MidasException {
        upShadowPct = (double[])stock.queryCmpIndex("k_u");
        downShadowPct = (double[])stock.queryCmpIndex("k_d");
        middleShadowPct = (double[])stock.queryCmpIndex("k_m");
        changePct = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_CHANGEPCT);
        pD1 = (double[])stock.queryCmpIndex("pD1");
        vMa = (double[])stock.queryCmpIndex("vMaMedium");
        k_sig = (double[])stock.queryCmpIndex("k_sig");
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
*/
