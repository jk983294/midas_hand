package com.victor.md.msg;

import java.nio.ByteBuffer;

public class BookChanged {
    public enum ChangedSide {
        bid((byte) 0),
        ask((byte) 1),
        both((byte) 2),
        none((byte) -1);

        private byte value;

        private ChangedSide(byte value) {
            this.value = value;
        }

        public byte getValue() {
            return value;
        }

        public static ChangedSide fromValue(byte value) {
            switch (value) {
                case 0:
                    return ChangedSide.bid;
                case 1:
                    return ChangedSide.ask;
                case 2:
                    return ChangedSide.both;
                case -1:
                    return ChangedSide.none;
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

    public ChangedSide side() {
        return ChangedSide.fromValue(buf.get(baseOffset));
    }

    public Timestamps timestamps() {
        return timestamps;
    }
}