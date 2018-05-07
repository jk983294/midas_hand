package com.victor.md.msg;

import com.victor.md.consumer.MdDataType;

public class DataHeartbeat {
    public byte type = MdDataType.DATA_HEARTBEAT_TYPE;
    public short msgSize = 20;
    public Heartbeat hb = new Heartbeat();

    public final static short sizeOfDataHeartbeat() {
        return 20;
    }
}
