package com.victor.md.msg;

import java.nio.ByteBuffer;

public class Timestamps {
    private ByteBuffer buf;
    private int baseOffset;

    public void loadBuf(ByteBuffer buf, int baseOffset) {
        this.buf = buf;
        this.baseOffset = baseOffset;
    }

    public long srcReceive() {
        return buf.getLong(baseOffset + 0);
    }

    public long srcTransmit() {
        return buf.getLong(baseOffset + 8);
    }

    public long producerReceive() {
        return buf.getLong(baseOffset + 16);
    }

    public long producerTransmit() {
        return buf.getLong(baseOffset + 24);
    }
}
