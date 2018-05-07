package com.victor.md.msg;

import com.victor.md.consumer.MdDataType;

public class CtrlUnsubscribeResponse {
    public byte type = MdDataType.CTRL_UNSUBSCRIBE_RESPONSE_TYPE;
    public short msgSize = 17;
    public byte status = 0;
    public short locate = 0;
    public byte[] symbol = new byte[9];
    public short exchange = -1;

    public final static short sizeOfCtrlUnsubscribeResponse() {
        return 17;
    }
}
