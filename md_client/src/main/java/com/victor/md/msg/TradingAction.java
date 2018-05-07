package com.victor.md.msg;

import java.nio.ByteBuffer;

public class TradingAction {
    public enum trade_status {
        auction((byte) 0),
        auction_close((byte) 1),
        auction_close2((byte) 2),
        auction_intraday((byte) 3),
        auction_intraday2((byte) 4),
        auction_open((byte) 5),
        auction_open2((byte) 6),
        auction_volatility((byte) 7),
        auction_volatility2((byte) 8),
        closed((byte) 9),
        halt((byte) 10),
        halt_quoting((byte) 11),
        obtrd((byte) 12),
        popen((byte) 13),
        postclose((byte) 14),
        none((byte) -1);

        private byte value;

        private trade_status(byte value) {
            this.value = value;
        }

        public byte getValue() {
            return value;
        }

        public static trade_status fromValue(byte value) {
            switch (value) {
                case 0:
                    return trade_status.auction;
                case 1:
                    return trade_status.auction_close;
                case 2:
                    return trade_status.auction_close2;
                case 3:
                    return trade_status.auction_intraday;
                case 4:
                    return trade_status.auction_intraday2;
                case 5:
                    return trade_status.auction_open;
                case 6:
                    return trade_status.auction_open2;
                case 7:
                    return trade_status.auction_volatility;
                case 8:
                    return trade_status.auction_volatility2;
                case 9:
                    return trade_status.closed;
                case 10:
                    return trade_status.halt;
                case 11:
                    return trade_status.halt_quoting;
                case 12:
                    return trade_status.obtrd;
                case 13:
                    return trade_status.popen;
                case 14:
                    return trade_status.postclose;
                case -1:
                    return trade_status.none;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    private ByteBuffer buf;
    private int baseOffset;
    private Timestamps timestamps = new Timestamps();

    public void loadBuf(ByteBuffer buf, int baseOffset) {
        this.buf = buf;
        this.baseOffset = baseOffset;
        timestamps.loadBuf(buf, baseOffset + 1);
    }

    public trade_status status() {
        return trade_status.fromValue(buf.get(baseOffset));
    }

    public Timestamps timestamps() {
        return timestamps;
    }
}
