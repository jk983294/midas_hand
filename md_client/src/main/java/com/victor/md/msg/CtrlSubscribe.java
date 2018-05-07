package com.victor.md.msg;

import com.victor.md.consumer.MdDataType;

public class CtrlSubscribe {
    public final byte type = MdDataType.CTRL_SUBSCRIBE_TYPE;
    public final short msgSize = 18;
    public byte[] symbol = new byte[9];
    public short exchange = -1;
    public int flags = 0; // indicates event types MDConsumer is interested in

    public final static short sizeOfCtrlSubscribe() {
        return 18;
    }

    public final static short sizeOfSymbol() {
        return 9;
    }
}
