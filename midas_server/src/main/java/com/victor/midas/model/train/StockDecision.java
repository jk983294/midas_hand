package com.victor.midas.model.train;

import com.victor.utilities.utils.MathHelper;

/**
 * model decision
 */
public enum StockDecision {
    WATCH,
    /** could buy at current day due to yesterday's decision */
    WILL_BUY,
    WILL_BUY_LGT,
    WILL_BUY_SR,
    WILL_BUY_GP,
    /** decide to buy at current day */
    BUY_AT_START,
    BUY_AT_END,
    HOLD,
    /** stop loss 5 */
    SELL_FOR_STATISTICS,
    SELL_STOP_LOSS_PERCENTAGE,
    /** stop win 7 */
    SELL_STOP_WIN_MOVING,
    SELL_STOP_WIN_UP_SHADOW,
    SELL_STOP_WIN_PERCENTAGE,
    SELL_STOP_K_STATE,
    SELL_STOP_BREAK_MA,
    SELL_STOP_BOUNDARY,
    SELL_STOP_VOLUME_PRICE_CORR,
    SELL_STOP_GP,
    SELL_STOP_WIN_HUGE_VOLUME;

    public static boolean couldBuy(StockDecision decision){
        return (decision == WATCH || isWillBuyDecision(decision));
    }

    public static boolean isWillBuyDecision(int decision){
        return MathHelper.isInRange(decision, WILL_BUY.ordinal(), WILL_BUY_GP.ordinal());
    }

    public static boolean isWillBuyDecision(StockDecision decision){
        return isWillBuyDecision(decision.ordinal());
    }

    public static boolean isSellDecision(StockDecision decision){
        return decision.ordinal() >= SELL_FOR_STATISTICS.ordinal();
    }

    public static boolean isBuyDecision(StockDecision decision){
        return (decision == BUY_AT_END || decision == BUY_AT_START);
    }
}
