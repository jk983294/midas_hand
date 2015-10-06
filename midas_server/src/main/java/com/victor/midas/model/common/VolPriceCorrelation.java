package com.victor.midas.model.common;

import com.victor.utilities.utils.MathHelper;

/**
 * model decision
 */
public enum VolPriceCorrelation {
    NO_MEANING,
    /** good */
    PRICE_UP_VOL_UP,
    PRICE_DOWN_VOL_DOWN,
    /** bad */
    PRICE_DOWN_VOL_UP,
    PRICE_UP_VOL_DOWN,
    HUGE_VOLUME_UP,
    HUGE_VOLUME_FREZZE_TIME,
    HUGE_EXTREME_VOLUME,
    HUGE_VOLUME_DOWN;

    public static int signal(VolPriceCorrelation state){
        int ordinal = state.ordinal();
        if(ordinal >= PRICE_DOWN_VOL_UP.ordinal()){
            return (ordinal - PRICE_DOWN_VOL_UP.ordinal() + 1) * -1;
        } else {
            return ordinal;
        }
    }

    public static boolean isSellDecision(int decision){
        return MathHelper.isInRange(decision, signal(PRICE_UP_VOL_DOWN), signal(HUGE_VOLUME_DOWN));
    }

    public static boolean isBuyDecision(int decision){
        return MathHelper.isInRange(decision, signal(PRICE_UP_VOL_UP), signal(PRICE_DOWN_VOL_DOWN));
    }

    public static boolean isBadHugeVolume(int decision){
        return MathHelper.isInRange(decision, signal(HUGE_VOLUME_FREZZE_TIME), signal(HUGE_VOLUME_DOWN));
    }

    public static boolean isBadHugeVolume(VolPriceCorrelation decision){
        return isBadHugeVolume(signal(decision));
    }
}
