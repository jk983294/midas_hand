package com.victor.midas.train.strategy.single;

import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.calculator.util.strategy.CalcUtilGp;
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
public class GpStrategyS extends SingleStockStrategyBase {

    public final static String STRATEGY_NAME = "GpStrategyS";

    private CalcUtilGp calcUtil;

    private double buyPrice;
    private double holdHighestEnd;

    private double[] changePct;
    private double[] upShadowPct;
    private double[] downShadowPct;
    private double[] middleShadowPct;
    private double[] pMaYearD1;
    private double[] pMaLongD1;
    private double[] vMaMedium;
    private int[] k_state;
    private double[] vMa;
    private int[] gp_sig;
    //private int[] volPriceCorr;


    public GpStrategyS(List<StockVo> allStocks, CalcParameter parameter) throws MidasException {
        super(allStocks, parameter, false);
        discreteCalculators = new ArrayList<>();
        continuousCalculators = new ArrayList<>();

        calcUtil = new CalcUtilGp();

//        lowbounds = new double[]{-1.1, -1.1};
//        upbounds = new double[]{1.1, 1.1};

        //Discrete Calculators
//        discreteCalculators.add(new IndexPriceMA(new SMA()));

        //Continuous Calculators
//        continuousCalculators.add(new PriceMaTangle());
    }

    @Override
    public StockDecision checkIfSell(int index){
        if(KState.isSellDecision(k_state[index])) return StockDecision.SELL_STOP_K_STATE;
        //if(VolPriceCorrelation.isSellDecision(volPriceCorr[index])) return StockDecision.SELL_STOP_VOLUME_PRICE_CORR;
        //stop loss
        if(MathStockUtil.calculateChangePct(buyPrice, end[index]) < -0.02){
            return StockDecision.SELL_STOP_LOSS_PERCENTAGE;
        }
        // moving stop win
        if(MathStockUtil.calculateChangePct(holdHighestEnd, end[index]) < -0.05){
            return StockDecision.SELL_STOP_WIN_MOVING;
        }
//        if(upShadowPct[index] > 0.05){
//            return StockDecision.SELL_STOP_WIN_UP_SHADOW;
//        }
        if(MathStockUtil.calculateChangePct(buyPrice, max[index]) > 0.20){
            return StockDecision.SELL_STOP_WIN_PERCENTAGE;
        }
        if(gp_sig[index] < 0){
            return StockDecision.SELL_STOP_GP;
        }
        if(calcUtil.isSellTimeWithinBoundary(index)){
            return StockDecision.SELL_STOP_BOUNDARY;
        }
//        if(calcUtil.isPriceUpVolumeDown(index)){
//            return StockDecision.SELL_STOP_VOLUME_PRICE_CORR;
//        }
//        if(calcUtil.isBigVolume(index)){
//            return StockDecision.SELL_STOP_WIN_HUGE_VOLUME;
//        }
        return StockDecision.HOLD;
    }

    @Override
    public void maintainDataWhenHolding(int index) {
        holdHighestEnd = Math.max(holdHighestEnd, end[index]);
    }

    @Override
    public boolean checkCouldBuyTomorrow(int index) {
        if(StockDecision.couldBuy(decision) && gp_sig[index] > 1){
            decision = StockDecision.WILL_BUY_GP;
            return true;
        }
        return false;
    }

    @Override
    public void checkCouldBuyToday(int index) throws MidasException {
//        if(calcUtil.isVolumeDownAnormal(index)){
//            decision = StockDecision.WATCH;
//        }else
        if(middleShadowPct[index] >= 0 && changePct[index] >= 0
                && !calcUtil.isVolumeDownAnormal(index)
                && isBigStock(index)
                //&& calcUtil.isTrendUp(index)
            //&& !calcUtil.isVolumeDownTooMuchAgainstPreBuy(index)
                ){
            decision = StockDecision.BUY_AT_END;
            buyReason = StockDecision.WILL_BUY_GP;
            buyPrice = end[index];
            holdHighestEnd = end[index];
            calcUtil.setBoundaryForBuy(index);
        }
    }

    private boolean isBigStock(int index){
        return vMaMedium[index] < 0.3 *  stockIndexTotal[shStockIndex];
    }

    @Override
    public void initStrategySpecifiedData() throws MidasException {
        calcUtil.init(stock.getStock());
        upShadowPct = (double[])stock.queryCmpIndex("k_u");
        downShadowPct = (double[])stock.queryCmpIndex("k_d");
        middleShadowPct = (double[])stock.queryCmpIndex("k_m");
        changePct = (double[])stock.queryCmpIndex(MidasConstants.INDEX_NAME_CHANGEPCT);
        pMaYearD1 = (double[])stock.queryCmpIndex("pMa60D1");
        pMaLongD1 = (double[])stock.queryCmpIndex("pMa20D1");
        vMaMedium = (double[])stock.queryCmpIndex("vMaLong");
        vMa = (double[])stock.queryCmpIndex("vMaMedium");
        k_state = (int[])stock.queryCmpIndex("k_state");
        gp_sig = (int[])stock.queryCmpIndex("gp_sig");
        //volPriceCorr = (int[])stock.queryCmpIndex("vp_corr");
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
