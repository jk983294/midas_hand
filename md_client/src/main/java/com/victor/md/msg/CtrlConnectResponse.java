package com.victor.md.msg;

import com.victor.md.consumer.MdDataType;

public class CtrlConnectResponse {
    public byte type = MdDataType.CTRL_CONNECT_RESPONSE_TYPE;
    public short msgSize = 30;
    public byte cid = 0;
    public byte status = 0;
    public byte shmType = 0x00;
    public byte[] shmPath = new byte[18];
    public int shmSize = 0;
    public short exchange = -1;

    public final static short sizeOfCtrlConnectResponse() {
        return 30;
    }
}
