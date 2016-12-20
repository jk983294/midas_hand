package com.victor.midas.train.common;


import com.victor.midas.model.train.SingleParameterTrainResult;
import com.victor.midas.model.vo.CalcParameter;

public interface Trainee {

    public SingleParameterTrainResult getPerformance();

    /**
     * apply this parameter to all calculator, then re-calculate all index
     */
    public void apply(CalcParameter parameter) throws Exception;

    public void setIsInTrain(boolean isInTrain);

}
