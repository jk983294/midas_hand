package com.victor.md.consumer;

import java.util.Arrays;

public class MdKey {
    public volatile MdKey nextKey;

    public byte[] symbol;
    public short exchange;

    public MdKey(byte[] symbol, short exchange) {
        this.symbol = Arrays.copyOf(symbol, symbol.length);
        this.exchange = exchange;
    }

    public MdKey() {
    }

    @Override
    public int hashCode() {
        int h = 0;
        if (symbol.length > 0) {
            for (int i = 0; i < symbol.length; i++) {
                h = 31 * h + symbol[i];
            }
            h = h ^ (this.exchange << 4);
        }
        return h;
    }

    @Override
    public boolean equals(Object key) {
        MdKey newKey = (MdKey) key;
        return ((this.exchange == newKey.exchange) && (Arrays.equals(this.symbol, newKey.symbol)));
    }

}
