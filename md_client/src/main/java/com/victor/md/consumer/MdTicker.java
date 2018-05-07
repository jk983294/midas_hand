package com.victor.md.consumer;

import com.victor.md.book.BookLevel;
import com.victor.md.book.MdBook;
import com.victor.md.msg.BookChanged;
import com.victor.md.msg.TradingAction;
import com.victor.md.util.UnsafeAccess;
import org.apache.log4j.Logger;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class MdTicker {
    private static final Logger logger = Logger.getLogger(MdTicker.class);
    private final int SizeOfLong = 8;

    enum State {
        empty, //Ticker created but not used yet
        pending,  // Awaiting response from producer
        active // Subscribed
    }

    private ConsumerCallBacks callbacks;

    private String symbol;
    private MdKey mdKey;
    private short exchange = -1;
    private State mdState = State.empty;
    private byte streamId = 0;
    private short locate = -1;
    private ArrayList<MdSubscription> subscriptions;
    private ByteBuffer l2AddressBookCache;
    private long l2AddressBookCacheAddress;
    private int l2StartOfData;
    private int l2BytesPerProduct;
    private int l2OffsetBytesBid;
    private int l2OffsetBytesAsk;
    private int l2ShmBookBidMe; // where all the bid levels start in shared memory
    private int l2ShmBookAskMe; // where all the ask levels start in shared memory

    private int l2LockBid;
    private int l2LockAsk;
    private int l2CheckBid; // check whether this book has been written by producer
    private int l2CheckAsk; // check whether this book has been written by producer

    private volatile long meta1;
    private volatile long meta2;

    private boolean staled;

    public MdTicker(String symbol, byte[] tick2Bytes, short exchange, ByteBuffer l2AddressBookCache, long l2AddrBookCacheAddr, int l2StartOfData, int l2BytesPerProduct,
                    int l2OffsetBytesBid, int l2OffsetBytesAsk, ConsumerCallBacks callbacks) {

        this.symbol = symbol;
        this.mdKey = new MdKey(tick2Bytes, exchange);
        this.exchange = exchange;
        this.mdState = State.empty;
        this.streamId = 0;
        this.locate = -1;
        this.l2AddressBookCache = l2AddressBookCache;
        this.l2AddressBookCacheAddress = l2AddrBookCacheAddr;
        this.l2StartOfData = l2StartOfData;
        this.l2BytesPerProduct = l2BytesPerProduct;
        this.l2OffsetBytesBid = l2OffsetBytesBid;
        this.l2OffsetBytesAsk = l2OffsetBytesAsk;
        this.l2ShmBookBidMe = -1;
        this.l2ShmBookAskMe = -1;
        this.l2CheckBid = -1;
        this.l2CheckAsk = -1;
        this.l2LockBid = -1;
        this.l2LockAsk = -1;

        this.subscriptions = new ArrayList<>();

        this.callbacks = callbacks;
    }

    public final ByteBuffer bookCache() {
        return l2AddressBookCache;
    }

    public final String symbol() {
        return symbol;
    }

    public final MdKey mdKey() {
        return mdKey;
    }

    public final short exchange() {
        return exchange;
    }

    public final State state() {
        return mdState;
    }

    public void state(State s) {
        mdState = s;
    }

    public final byte streamId() {
        return streamId;
    }

    public void streamId(byte streamId) {
        this.streamId = streamId;
    }

    public final short locate() {
        return locate;
    }

    public void locate(short code) {
        locate = code;
        initOffsets();
    }

    public final int subscriptionCount() {
        synchronized (this) {
            return subscriptions.size();
        }
    }

    public final int addrBidLevels() {
        return l2ShmBookBidMe;
    }

    public final int addrAskLevels() {
        return l2ShmBookAskMe;
    }

    public void initOffsets() {
        if (l2ShmBookBidMe == -1 && locate != -1) {
            l2ShmBookBidMe = l2StartOfData + locate * l2BytesPerProduct + l2OffsetBytesBid;
            l2LockBid = l2ShmBookBidMe + (((l2BytesPerProduct / (BookLevel.sizeOfBookLevel() + BookLevel.sizeOfBookLevel())) - 1) * BookLevel.sizeOfBookLevel());
            l2CheckBid = l2LockBid + SizeOfLong;
            logger.debug("l2AddressBookCache: " + l2StartOfData + "\tlocate: " + locate + "\tl2BytesPerProduct: " + l2BytesPerProduct + "\tl2OffsetBytesBid: " + l2OffsetBytesBid);
        }

        if (l2ShmBookAskMe == -1 && locate != -1) {
            l2ShmBookAskMe = l2StartOfData + locate * l2BytesPerProduct + l2OffsetBytesAsk;
            l2LockAsk = l2ShmBookAskMe + (((l2BytesPerProduct / (BookLevel.sizeOfBookLevel() + BookLevel.sizeOfBookLevel())) - 1) * BookLevel.sizeOfBookLevel());
            l2CheckAsk = l2LockAsk + SizeOfLong;
            logger.debug("l2AddressBookCache: " + l2StartOfData + "\tlocate: " + locate + "\tl2BytesPerProduct: " + l2BytesPerProduct + "\tl2OffsetBytesAsk: " + l2OffsetBytesAsk);
        }
    }

    MdSubscription subscriptionAdd(MdSubscription subscription) {
        synchronized (this) {
            subscription.setTicker(this);
            subscriptions.add(subscription);
            return subscription;
        }
    }

    boolean subscriptionDelete(MdSubscription mdsub) {
        synchronized (this) {
            for (int i = 0; i < subscriptions.size(); i++) {
                if (subscriptions.get(i) == mdsub) {
                    return subscriptions.remove(subscriptions.get(i));
                }
            }
        }
        return false;
    }

    void onSubscribeResponse(String symbol, short exch, ConsumerStatus status) {
        synchronized (this) {
            for (int i = 0; i < subscriptions.size(); i++) {
                callbacks.onSubscribe(symbol, exch, status);
            }
        }
    }

    void onTradingAction(String symbol, short exch, TradingAction ta) {
        synchronized (this) {
            for (int i = 0; i < subscriptions.size(); i++) {
                callbacks.onTradingAction(symbol, exch, ta);
            }
        }
    }

    void onBookChanged(String symbol, short exch, BookChanged bkchngd) {
        synchronized (this) {
            for (int i = 0; i < subscriptions.size(); i++) {
                callbacks.onBookChanged(symbol, exch, bkchngd);
            }
        }
    }

    void onBookRefreshed(String symbol, short exch) {
        synchronized (this) {
            for (int i = 0; i < subscriptions.size(); i++) {
                callbacks.onBookRefreshed(symbol, exch);
            }
        }
    }

    public void setConnected(boolean connected) {
        this.staled = !connected;
    }

    ConsumerStatus _snap(long address, int startPos, int n, int pMeta) {
        if (staled) {
            return ConsumerStatus.NOT_CONNECTED;
        }

        while (true) {
            meta1 = l2AddressBookCache.getLong(pMeta);
            final long nSnappedVersion = (meta1 << 12) >> 12;

            // check versions -
            // writer will increment version twice,
            // step1: odd number to indicate modifying in process and possibly partial data,
            // step2: even number to indicate good data

            // if it's odd version, it's modification in process
            if ((nSnappedVersion & 1) == 1) {
                continue;
            }

            assert (l2AddressBookCacheAddress != -1);
            UnsafeAccess.getUnsafe().copyMemory(l2AddressBookCacheAddress + startPos, address, n);


            // if not modified, good to go
            meta2 = l2AddressBookCache.getLong(pMeta);
            final long versionNow = (meta2 << 12) >> 12;
            if (versionNow == nSnappedVersion) {
                return ConsumerStatus.OK;
            }
        }
    }

    ConsumerStatus bidSnap(MdBook book) {
        ConsumerStatus rc = ConsumerStatus.NO_DATA;
        if (l2AddressBookCache.getLong(l2CheckBid) == 0) {
            rc = _snap(book.getBidAddress(), l2ShmBookBidMe, book.getNumBidLevels() * BookLevel.sizeOfBookLevel(), l2LockBid);
        }
        return rc;
    }

    ConsumerStatus askSnap(MdBook book) {
        ConsumerStatus rc = ConsumerStatus.NO_DATA;
        if (l2AddressBookCache.getLong(l2CheckAsk) == 0) {
            rc = _snap(book.getAskAddress(), l2ShmBookAskMe, book.getNumAskLevels() * BookLevel.sizeOfBookLevel(), l2LockAsk);
        }
        return rc;
    }

    ConsumerStatus snap(int side, MdBook book) {
        ConsumerStatus rc = ConsumerStatus.NO_DATA;
        if (locate > -1) //initialization already
        {
            if (book.getBookType() == MdBook.BookType.price) {
                switch (side) {
                    case MdDataType.BOOK_SIDE_BID:
                        rc = bidSnap(book);
                        break;
                    case MdDataType.BOOK_SIDE_ASK:
                        rc = askSnap(book);
                        break;
                    case MdDataType.BOOK_SIDE_BOTH:
                        if ((rc = bidSnap(book)) == ConsumerStatus.OK)
                            rc = askSnap(book);
                        break;
                    default:
                        break;
                }
            }
        }
        return rc;
    }
}
