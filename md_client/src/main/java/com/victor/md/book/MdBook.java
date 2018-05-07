package com.victor.md.book;

import com.victor.md.util.UnsafeAccess;

import java.nio.ByteBuffer;

public class MdBook {
    public enum BookType {
        price((byte) 0),
        image((byte) 1),
        order((byte) 2),
        none((byte) -1);

        private BookType(byte value) {
        }
    }

    public BidBookLevel[] bidBookLevels;
    public AskBookLevel[] askBookLevels;

    private ByteBuffer thunk = null;
    private long thunkAddress = -1;
    private short numBidLevels = 0;
    private long bidAddress = -1;
    private short numAskLevels = 0;
    private long askAddress = -1;
    private BookType bookType = BookType.none;
    private boolean myAlloc = true;

    public void loadBookCache(ByteBuffer thunk, long thunkAddress, BookType bookType, boolean myAlloc) {
        this.thunk = thunk;
        this.thunkAddress = thunkAddress;
        this.bookType = bookType;
        this.myAlloc = myAlloc;
    }

    public BidBookLevel[] bidBookLevels() {
        return bidBookLevels;
    }

    public AskBookLevel[] askBookLevels() {
        return askBookLevels;
    }

    public ByteBuffer getThunk() {
        return thunk;
    }

    public long getThunkAddress() {
        return thunkAddress;
    }

    public BookType getBookType() {
        return bookType;
    }

    public boolean getMyAlloc() {
        return myAlloc;
    }

    public void loadBookAddr(long bidAddress, long askAddress) {
        if (numBidLevels == 0 || numAskLevels == 0)
            throw new IllegalArgumentException("Depth of book can not be zero");
        loadBookAddr(numAskLevels, bidAddress, askAddress);
    }

    public void allocBookMemory(short depth, int bidCap, int askCap) {
        if (bidCap == 0 || askCap == 0) {
            this.numAskLevels = depth;
            this.numBidLevels = depth;
        } else {
            loadBookAddr(depth, UnsafeAccess.getUnsafe().allocateMemory(bidCap), UnsafeAccess.getUnsafe().allocateMemory(askCap));
        }
    }

    private void loadBookAddr(short depth, long bidAddress, long askAddress) {
        this.numBidLevels = depth;
        this.numAskLevels = depth;
        this.bidAddress = bidAddress;
        this.askAddress = askAddress;

        if (bidBookLevels == null || depth != bidBookLevels.length || depth != askBookLevels.length) {
            bidBookLevels = new BidBookLevel[depth];
            askBookLevels = new AskBookLevel[depth];
            for (int i = 0; i < depth; i++) {
                bidBookLevels[i] = new BidBookLevel();
                askBookLevels[i] = new AskBookLevel();
            }
        }

        for (int i = 0; i < depth; i++) {
            bidBookLevels[i].loadAddr(bidAddress + i * BookLevel.sizeOfBookLevel());
            askBookLevels[i].loadAddr(askAddress + i * BookLevel.sizeOfBookLevel());
        }
    }

    public short getNumBidLevels() {
        return numBidLevels;
    }

    public short getNumAskLevels() {
        return numAskLevels;
    }

    public long getBidAddress() {
        return bidAddress;
    }

    public long getAskAddress() {
        return askAddress;
    }

    public void destroy() {
        if (bidAddress != -1)
            UnsafeAccess.getUnsafe().freeMemory(bidAddress);
        if (askAddress != -1)
            UnsafeAccess.getUnsafe().freeMemory(askAddress);
    }
}
