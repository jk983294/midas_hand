package com.victor.md.msg;

import java.nio.ByteBuffer;

public class DataTradingAction {
    private ByteBuffer buf;
    private int baseOffset;
    private byte[] symbol = new byte[9];
    private TradingAction tradingAction = new TradingAction();

    public void loadBuf(ByteBuffer buf, int baseOffset) {
        this.buf = buf;
        this.baseOffset = baseOffset;
        for (int i = 0; i < symbol.length; i++) {
            symbol[i] = buf.get(baseOffset + 13 + i);
        }
        tradingAction.loadBuf(buf, baseOffset + 24);
    }

    public final byte type() {
        return buf.get(baseOffset + 0);
    }

    public final short msgSize() {
        return buf.getShort(baseOffset + 1);
    }

    public final long pubRcvt() {
        return buf.getLong(baseOffset + 3);
    }

    public final short locate() {
        return buf.getShort(baseOffset + 11);
    }

    public final byte[] symbol() {
        return symbol;
    }

    public final short exchange() {
        return buf.getShort(baseOffset + 22);
    }

    public final TradingAction tradingAction() {
        return tradingAction;
    }

    public final static short sizeOfDataTradingAction() {
        return 57;
    }
}
