package com.victor.midas.train.strategy.common;

import com.victor.midas.model.vo.CalcParameter;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.train.strategy.single.GpStrategyS;
import com.victor.midas.train.strategy.single.KLineStrategyS;
import com.victor.midas.train.strategy.single.LgtStrategyS;
import com.victor.midas.train.strategy.single.SrStrategyS;
import com.victor.midas.util.MidasException;

import java.util.List;

/**
 * create strategy
 */
public class StrategyFactory {

    public static TradeStrategy getStrategyByName(String name, CalcParameter parameter, List<StockVo> stocks) throws MidasException {
        switch (name){
            case KLineStrategyS.STRATEGY_NAME : return new KLineStrategyS(stocks, parameter);
            //case KLineStrategyM.STRATEGY_NAME : return new KLineStrategyS(stocks, parameter);
            case LgtStrategyS.STRATEGY_NAME : return new LgtStrategyS(stocks, parameter);
            case GpStrategyS.STRATEGY_NAME : return new GpStrategyS(stocks, parameter);
            case SrStrategyS.STRATEGY_NAME : return new SrStrategyS(stocks, parameter);
            default: throw new MidasException("no such strategy found : " + name );
        }
    }
}
