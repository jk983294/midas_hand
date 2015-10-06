package com.victor.midas.model.common;

import com.victor.utilities.utils.MathHelper;

/**
 * model decision
 */
public enum KState {
    NO_MEANING,
    /** good */
    ENGULG_UP,
    HAMMER,
    INVERTED_HAMMER,
    MORNING_STAR,
    PIERCING_PATTERN,
    LONG_LEG_DOJI_UP,
    HARAMI_CROSS_UP,
    HIGH_WAVE_UP,
    WINDOW_UP,
    /** bad */
    HANGING_MAN,
    DARK_CLOUD_COVER,
    ENGULG_DOWN,
    LONG_LEG_DOJI_DOWN,
    HIGH_WAVE_DOWN,
    WINDOW_DOWN,
    EVENING_STAR,
    HARAMI_CROSS_DOWN;



    public static int signal(KState state){
        int ordinal = state.ordinal();
        if(ordinal >= HANGING_MAN.ordinal()){
            return (ordinal - HANGING_MAN.ordinal() + 1) * -1;
        } else {
            return ordinal;
        }
    }

    public static boolean isSellDecision(int decision){
        return MathHelper.isInRange(decision, signal(HANGING_MAN), signal(HARAMI_CROSS_DOWN));
    }

    public static boolean isBuyDecision(int decision){
        return MathHelper.isInRange(decision, signal(ENGULG_UP), signal(WINDOW_UP));
    }

    public static boolean isSellDecisionStrong(int decision){
        return MathHelper.isInRange(decision, signal(HANGING_MAN), signal(WINDOW_DOWN));
    }
}
