package com.victor.utilities.math.combination;

import com.victor.utilities.utils.ArrayHelper;

import java.util.List;

/**
 * Created by Administrator on 2014/11/10.
 */
public class Combinations {

    /**
     * each decision has k choices, all n decision making have k^n combination
     */
    public static boolean allChoices(int[] choice,int n, int k){
        for (int i = n-1; i >= 0; --i ){
            if(choice[i] != k-1){//find first pos not equal k-1, add 1 and then set backward all 0
                ++choice[i];
                ArrayHelper.fill(choice, i + 1, choice.length, 0);
                return true;
            }
        }
        return false;		//all combination has been traversed, there is no decision left
    }


    /**
     * each decision choice[i] has choiceCnt[i] choices, all n decision making have PI(choiceCnt[i]) combination
     */
    public static boolean allChoices(int[] choice,int[] choiceCnt){
        int n = choice.length;
        for (int i = n-1; i >= 0; --i ){
            if(choice[i] != choiceCnt[i]-1){   //find first pos not equal choiceCnt[i]-1, add 1 and then set backward all 0
                ++choice[i];
                ArrayHelper.fill(choice, i + 1, choice.length, 0);
                return true;
            }
        }
        return false;		//all combination has been traversed, there is no decision left
    }

    public static int[] getChoiceCnt(List<int[]> discreteParams){
        int[] choiceCnt = new int[discreteParams.size()];
        for (int i = 0; i < discreteParams.size(); i++) {
            choiceCnt[i] = discreteParams.get(i).length;
        }
        return choiceCnt;
    }

    public static int[] getChoice(List<int[]> discreteParams, int[] choiceIndex, int[] choice){
        for (int i = 0; i < discreteParams.size(); i++) {
            choice[i] = discreteParams.get(i)[choiceIndex[i]];
        }
        return choice;
    }

}
