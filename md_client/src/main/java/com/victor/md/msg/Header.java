package com.victor.md.msg;

public class Header {
    public long session = 0; // sessionId id
    public long seq = 0; // sequence#
    public long transmitTimestamp = 0; // transmit timestamp for message unit
    public byte streamId = 0; // stream-id - always 0 for control traffic
    public byte count = 0; // number of messages in 'message packet'
    public short size = 0; // total size (bytes) of messages (excluding header size) in 'message packet'


    public final static short sizeOfHeader() {
        return 28;
    }
}
