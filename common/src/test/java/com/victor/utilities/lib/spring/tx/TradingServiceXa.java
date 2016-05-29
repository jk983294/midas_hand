package com.victor.utilities.lib.spring.tx;

import com.victor.utilities.lib.spring.tx.model.Placement;
import com.victor.utilities.lib.spring.tx.model.TradeOrderData;

/**
 * best practice: use it only when your tx needs to coordinate several resources (DB, JMS, distributed DBs)
 */
public class TradingServiceXa {

    PlacementService placementService;
    ExecutionService executionService;


    /**
     * when this rollback, sendPlacementMessage won't rollback because the msg may be consumed
     */
    public void placeFixedIncomeTrade(TradeOrderData trade) throws Exception {
        Placement placement = placementService.placeTrade(trade);
        placementService.sendPlacementMessage(placement);
        executionService.executeTrade(placement);
    }
}
