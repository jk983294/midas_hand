package com.victor.md.msg;

import java.nio.ByteBuffer;

public class MdFeedMsgBuilder {
    private static final int SizeOfByte = 1;
    private static final int SizeOfShort = 2;
    private static final int SizeOfInt = 4;
    private static final int SizeOfLong = 8;

    public MdFeedMsgBuilder buildCtrlConnectResponse(ByteBuffer buf, int offset, CtrlConnectResponse ctrlConnectResponse) {
        ctrlConnectResponse.type = buf.get(offset);
        offset += SizeOfByte;
        ctrlConnectResponse.msgSize = buf.getShort(offset);
        offset += SizeOfShort;
        ctrlConnectResponse.cid = buf.get(offset);
        offset += SizeOfByte;
        ctrlConnectResponse.status = buf.get(offset);
        offset += SizeOfByte;
        ctrlConnectResponse.shmType = buf.get(offset);
        offset += SizeOfByte;

        for (int i = 0; i < ctrlConnectResponse.shmPath.length; i++) {
            ctrlConnectResponse.shmPath[i] = buf.get(offset);
            offset += SizeOfByte;
        }
        ctrlConnectResponse.shmSize = buf.getInt(offset);
        offset += SizeOfInt;
        ctrlConnectResponse.exchange = buf.getShort(offset);
        return this;
    }

    public MdFeedMsgBuilder buildCtrlDisconnectResponse(ByteBuffer buf, int offset, CtrlDisconnectResponse ctrlDisconnectResponse) {
        ctrlDisconnectResponse.type = buf.get(offset);
        offset += SizeOfByte;
        ctrlDisconnectResponse.msgSize = buf.getShort(offset);
        offset += SizeOfShort;
        ctrlDisconnectResponse.status = buf.get(offset);
        return this;
    }

    public MdFeedMsgBuilder buildCtrlSubscribeResponse(ByteBuffer buf, int offset, CtrlSubscribeResponse ctrlSubscribeResponse) {
        ctrlSubscribeResponse.type = buf.get(offset);
        offset += SizeOfByte;
        ctrlSubscribeResponse.msgSize = buf.getShort(offset);
        offset += SizeOfShort;
        ctrlSubscribeResponse.status = buf.get(offset);
        offset += SizeOfByte;
        ctrlSubscribeResponse.locate = buf.getShort(offset);
        offset += SizeOfShort;
        for (int i = 0; i < ctrlSubscribeResponse.symbol.length; i++) {
            ctrlSubscribeResponse.symbol[i] = buf.get(offset);
            offset += SizeOfByte;
        }
        ctrlSubscribeResponse.exchange = buf.getShort(offset);
        return this;
    }

    public MdFeedMsgBuilder buildCtrlUnsubscribeResponse(ByteBuffer buf, int offset, CtrlUnsubscribeResponse ctrlUnsubscribeResponse) {
        ctrlUnsubscribeResponse.type = buf.get(offset);
        offset += SizeOfByte;
        ctrlUnsubscribeResponse.msgSize = buf.getShort(offset);
        offset += SizeOfShort;
        ctrlUnsubscribeResponse.status = buf.get(offset);
        offset += SizeOfByte;
        ctrlUnsubscribeResponse.locate = buf.getShort(offset);
        offset += SizeOfShort;
        for (int i = 0; i < ctrlUnsubscribeResponse.symbol.length; i++) {
            ctrlUnsubscribeResponse.symbol[i] = buf.get(offset);
            offset += SizeOfByte;
        }
        ctrlUnsubscribeResponse.exchange = buf.getShort(offset);
        return this;
    }

    public MdFeedMsgBuilder buildHeader(ByteBuffer buf, int offset, Header header) {
        header.session = buf.getLong(offset);
        offset += SizeOfLong;
        header.seq = buf.getLong(offset);
        offset += SizeOfLong;
        header.transmitTimestamp = buf.getLong(offset);
        offset += SizeOfLong;
        header.streamId = buf.get(offset);
        offset += SizeOfByte;
        header.count = buf.get(offset);
        offset += SizeOfByte;
        header.size = buf.getShort(offset);
        return this;
    }

    public MdFeedMsgBuilder buildHeaderBuffer(ByteBuffer buf, long session, long seq, long transmitTimestamp,
                                              byte streamId, byte count, short size) {
        buf.putLong(session).putLong(seq).putLong(transmitTimestamp).put(streamId).put(count).putShort(size);
        return this;
    }

    public MdFeedMsgBuilder buildCtrlConnectBuffer(ByteBuffer buf, byte type, short msgSize, int clientPid,
                                                   byte clientId, int flags, byte[] user, byte[] pwd,
                                                   byte[] shmKey) {
        buf.put(type).putShort(msgSize).putInt(clientPid).put(clientId).putInt(flags).put(user)
                .put(pwd).put(shmKey);
        return this;
    }

    public MdFeedMsgBuilder buildCtrlDisconnectBuffer(ByteBuffer buf, byte type, short msgSize, int cpid) {
        buf.put(type).putShort(msgSize).putInt(cpid);
        return this;
    }

    public MdFeedMsgBuilder buildCtrlSubscribeBuffer(ByteBuffer buf, byte type, short msgSize, byte[] symbol,
                                                     short exchange, int flags) {
        buf.put(type).putShort(msgSize).put(symbol).putShort(exchange).putInt(flags);
        return this;
    }

    public MdFeedMsgBuilder buildCtrlUnsubscribeBuffer(ByteBuffer buf, byte type, short msgSize, byte[] symbol,
                                                       short exchange) {
        buf.put(type).putShort(msgSize).put(symbol).putShort(exchange);
        return this;
    }

}
