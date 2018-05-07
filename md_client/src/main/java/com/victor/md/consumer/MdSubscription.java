package com.victor.md.consumer;

import com.victor.md.book.MdBook;

import java.nio.ByteBuffer;

public class MdSubscription {
    private ConsumerStatus status;

    private MdTicker ticker;

    public MdSubscription() {
    }

    public MdSubscription(MdTicker ticker) {
        this.ticker = ticker;
    }

    void setTicker(MdTicker ticker) {
        this.ticker = ticker;
    }

    public final ByteBuffer bookCache() {
        return ticker.bookCache();
    }

    public final String symbol() {
        return ticker.symbol();
    }

    public final MdKey mdKey() {
        return ticker.mdKey();
    }

    public final short exchange() {
        return ticker.exchange();
    }

    public final byte strmid() {
        return ticker.streamId();
    }

    public final short locate() {
        return ticker.locate();
    }

    public ConsumerStatus snap(int side, MdBook book) {
        return ticker.snap(side, book);
    }

}
