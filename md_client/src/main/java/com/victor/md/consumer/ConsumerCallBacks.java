package com.victor.md.consumer;

import com.victor.md.msg.*;
import com.victor.md.util.ObjectShortConsumer;
import com.victor.md.util.ObjectShortObjectConsumer;
import org.apache.log4j.Logger;

import java.util.function.Consumer;

public class ConsumerCallBacks {
    private static final Logger logger = Logger.getLogger(ConsumerCallBacks.class);

    private Consumer<FeedStatus> onConnection;
    private Consumer<FeedStatus> onDisconnection;
    private ObjectShortObjectConsumer<String, ConsumerStatus> onSubscribe;
    private Runnable onPeriodically;
    private ObjectShortObjectConsumer<String, TradingAction> onTradingAction;
    private ObjectShortObjectConsumer<String, BookChanged> onBookChanged;
    private ObjectShortConsumer<String> onBookRefreshed;
    private Consumer<Heartbeat> onHeartbeat;

    private int cbInterval = Integer.MAX_VALUE;

    int subscriptionFlags = 0;

    public ConsumerCallBacks() {
        onConnection = (FeedStatus status) -> {
        };

        onDisconnection = (FeedStatus status) -> {
        };

        onSubscribe = (String symbol, short exchange, ConsumerStatus status) -> {
        };

        onPeriodically = () -> {
        };

        onTradingAction = (String symbol, short exchange, TradingAction action) -> {
        };

        onBookChanged = (String symbol, short exchange, BookChanged bookChanged) -> {
        };

        onBookRefreshed = (String symbol, short exchange) -> {
        };

        onHeartbeat = (Heartbeat hb) -> {
        };
    }


    /**
     * Set onConnection callback.
     */
    public void setOnConnection(Consumer<FeedStatus> onConnection) {
        this.onConnection = onConnection;
    }


    /**
     * Set onDisconnection callback.
     */
    public void setOnDisconnection(Consumer<FeedStatus> onDisconnection) {
        this.onDisconnection = onDisconnection;
    }


    /**
     * Set onSubscribe callback.
     */
    public void setOnSubscribe(ObjectShortObjectConsumer<String, ConsumerStatus> onSubscribe) {
        this.onSubscribe = onSubscribe;
    }


    /**
     * Set onTradingAction callback.
     */
    public void setOnTradingAction(ObjectShortObjectConsumer<String, TradingAction> onTradingAction) {
        this.onTradingAction = onTradingAction;
        subscriptionFlags |= MdDataType.FLAG_SEND_TRADING_ACTION;
    }


    /**
     * Set bookChange callback.
     */
    public void setBookChanged(ObjectShortObjectConsumer<String, BookChanged> onBookChanged) {
        this.onBookChanged = onBookChanged;
        subscriptionFlags |= MdDataType.FLAG_SEND_BOOK_CHANGED;
    }


    /**
     * Set bookRefreshed callback.
     */
    public void setBookRefreshed(ObjectShortConsumer<String> onBookRefreshed) {
        this.onBookRefreshed = onBookRefreshed;
        subscriptionFlags |= MdDataType.FLAG_SEND_BOOK_REFRESHED;
    }


    /**
     * Set onPeriodically callback
     */
    public void setOnPeriodically(Runnable onPeriodically, int cbInterval) {
        this.onPeriodically = onPeriodically;
        this.cbInterval = cbInterval;
    }


    /**
     * Set onHeartbeat callback
     */
    public void setOnHeartbeat(Consumer<Heartbeat> onHeartbeat) {
        this.onHeartbeat = onHeartbeat;
        subscriptionFlags |= MdDataType.FLAG_SEND_DATA_HEARTBEAT;
    }


    int callbackInterval() {
        return cbInterval;
    }

    void onTradingAction(String symbol, short exchange, TradingAction ta) {
        onTradingAction.accept(symbol, exchange, ta);
    }

    void onBookChanged(String symbol, short exchange, BookChanged bc) {
        onBookChanged.accept(symbol, exchange, bc);
    }

    void onBookRefreshed(String symbol, short exchange) {
        onBookRefreshed.accept(symbol, exchange);
    }

    void onSubscribe(String symbol, short exchange, ConsumerStatus status) {
        onSubscribe.accept(symbol, exchange, status);
    }

    void onPeriodically() {
        logger.info("call back periodically");
        onPeriodically.run();
    }

    void onHeartbeat(Header header, DataHeartbeat dhb) {
        onHeartbeat.accept(dhb.hb);
    }

    void onControlChannelDisconnected() {
        logger.info("Control channel is now disconnected");
        onDisconnection.accept(FeedStatus.CONNECT_STATUS_OK);
    }

    Consumer<FeedStatus> getOnConnection() {
        return onConnection;
    }

    void onControlChannelEstablished() {
        logger.info("Control channel is now established");
        onConnection.accept(FeedStatus.CONNECT_STATUS_OK);
    }

    void onDisconnectResponse(Header header, CtrlDisconnectResponse ctrl_DisconnectResponse) {
        logger.info("Disconnect status " + ctrl_DisconnectResponse.status);
    }

    void onControlChannelConnected() {
        logger.info("Control channel is now connected");
    }

    void onUnsubscribeResponse(Header header, CtrlUnsubscribeResponse cur) {
        logger.info("Received UnsubscribeResponse, status " + cur.status + "\tlocate " + cur.locate + "\texchange " + cur.exchange);
    }

}
