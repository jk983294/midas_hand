package com.victor.midas.train.common;


import com.victor.midas.model.train.SingleParameterTrainResult;
import com.victor.midas.model.vo.CalcParameter;

public interface Trainee {

    public SingleParameterTrainResult getPerformance();

    public void apply(CalcParameter parameter) throws Exception;

}
