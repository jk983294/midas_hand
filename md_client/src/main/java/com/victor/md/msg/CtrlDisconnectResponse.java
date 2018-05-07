package com.victor.md.msg;

import com.victor.md.consumer.MdDataType;

public class CtrlDisconnectResponse {
    public byte type = MdDataType.CTRL_DISCONNECT_RESPONSE_TYPE;
    public short msgSize = 4;
    public byte status = 0;

    public final static short sizeOfCtrlDisconnectResponse() {
        return 4;
    }
}
