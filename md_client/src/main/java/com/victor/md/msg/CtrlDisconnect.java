package com.victor.md.msg;

import com.victor.md.consumer.MdDataType;

public class CtrlDisconnect {
    public byte type = MdDataType.CTRL_DISCONNECT_TYPE;
    public short msgSize = 7;
    public int cpid = 0;

    public final static short sizeOfCtrlDisconnect() {
        return 7;
    }
}
