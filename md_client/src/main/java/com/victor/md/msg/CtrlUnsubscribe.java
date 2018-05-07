package com.victor.md.msg;

import com.victor.md.consumer.MdDataType;

public class CtrlUnsubscribe {
    public final byte type = MdDataType.CTRL_UNSUBSCRIBE_TYPE;
    public final short msgSize = 14;
    public byte[] symbol = new byte[9];
    public short exchange = -1;

    public final static short sizeOfCtrlUnsubscribe() {
        return 14;
    }
}
