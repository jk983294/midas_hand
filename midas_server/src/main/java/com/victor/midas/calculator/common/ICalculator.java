package com.victor.midas.calculator.common;


import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.train.common.MidasTrainOptions;
import com.victor.midas.train.common.TrainOptionApply;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.victor.midas.util.StockFilterUtil;

import java.util.Set;

public interface ICalculator {

    MidasConstants.CalculatorType getCalculatorType();

    // aggregation use whole set to init
    void init_aggregation(StockFilterUtil filterUtil);

    // for aggregation, pass in null since marketIndex will be retrieved in StockFilterUtil
    void calculate(StockVo stock) throws MidasException;

    void calculate() throws MidasException;

    // use this index name to register into factory
    String getIndexName();

    Set<String> getRequiredCalculators();

    void setRequiredCalculators();

    /**
     * for concrete calculator set their parameter
     */
    void applyParameter(CalcParameter parameter);

    MidasTrainOptions getTrainOptions();

}
