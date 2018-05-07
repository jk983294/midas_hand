package com.victor.md.book;

import com.victor.md.consumer.MdDataType;

public class BookMetadata {
    public byte[] watermark = {'b', '0', '0', 'k', 'M', 'e', 'T', 'A'};
    public short exchMD = MdDataType.ExchangeNone;
    public byte exchDepth = 0;
    public short exchOffsetBytesBid = 0;
    public short exchOffsetBytesAsk = 0;

    public final static short sizeOfBookMetadata() {
        return 15;
    }
}
