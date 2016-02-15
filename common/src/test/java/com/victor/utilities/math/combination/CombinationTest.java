package com.victor.utilities.math.combination;

import com.victor.utilities.utils.ArrayHelper;
import com.victor.utilities.visual.VisualAssist;

/**
 * test for Combination function
 */
public class CombinationTest {

    public static void main(String[] args) {
        int[] choice = ArrayHelper.newArray(3, 0);
        do {
            VisualAssist.print(choice);
        } while (Combinations.allChoices(choice, 3, 2));


        VisualAssist.print("another type : ");
        choice = ArrayHelper.newArray(3, 0);
        int[] choiceCnt = new int[]{3, 2, 1};
        do {
            VisualAssist.print(choice);
        } while (Combinations.allChoices(choice, choiceCnt));
    }
}
