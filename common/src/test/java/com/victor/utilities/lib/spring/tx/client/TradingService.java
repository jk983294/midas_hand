package com.victor.utilities.lib.spring.tx.client;


import com.victor.utilities.lib.spring.tx.model.TradeOrderData;

public interface TradingService {

    void updateTradeOrder(TradeOrderData trade);

    TradeOrderData getTrade(String key);

    void validateUpdate();
}
