package com.voctor.midas.calculator;

import com.victor.midas.model.common.StockType;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.util.MidasConstants;

/**
 * provide test data
 */
public class TestData {

    public StockVo getStockVo(){
        StockVo stock = new StockVo("test", "test", StockType.Index);
        double[] end = new double[]{1.0, 1.01, 1.01, 1.04, 1.03, 1.04, 1.03, 1.10, 1.0, 1.01, 1.0, 1.01, 0.98, 1.0, 0.96};
        stock.addIndex(MidasConstants.INDEX_NAME_END, end);
        return stock;
    }
}
