package com.victor.midas.train.common;


import com.victor.midas.model.train.SingleParameterTrainResult;
import com.victor.midas.model.vo.CalcParameter;

public interface Trainee {

    SingleParameterTrainResult getPerformance();

    /**
     * apply this parameter to all calculator, then re-calculate all index
     */
    void apply(CalcParameter parameter) throws Exception;

    void setIsInTrain(boolean isInTrain);

}
