package com.victor.midas.calculator.common;


import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.util.MidasConstants;
import com.victor.midas.util.MidasException;
import com.victor.midas.util.StockFilterUtil;

import java.util.Set;

public interface ICalculator {

    public MidasConstants.CalculatorType getCalculatorType();

    // aggregation use whole set to init
    public void init_aggregation(StockFilterUtil filterUtil);

    // for aggregation, pass in null since marketIndex will be retrieved in StockFilterUtil
    public void calculate(StockVo stock) throws MidasException;

    public void calculate() throws MidasException;

    // use this index name to register into factory
    public String getIndexName();

    public Set<String> getRequiredCalculators();

    public void setRequiredCalculators();

    /**
     * for concrete calculator set their parameter
     */
    public void applyParameter(CalcParameter parameter);

}
