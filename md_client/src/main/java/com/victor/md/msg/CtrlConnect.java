package com.victor.md.msg;

import com.victor.md.consumer.MdDataType;

public class CtrlConnect {
    public final byte type = MdDataType.CTRL_CONNECT_TYPE;
    public final short msgSize = 52;
    public int clientPid = 0;
    public byte clientId = 0;
    public int flags = 0; // indicates event types client is interested in
    public byte[] user = new byte[9];
    public byte[] pwd = new byte[13];
    public byte[] shmKey = new byte[18];

    public final static short sizeOfCtrlConnect() {
        return 52;
    }
}
