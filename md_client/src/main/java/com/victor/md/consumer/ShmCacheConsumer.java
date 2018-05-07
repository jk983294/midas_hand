package com.victor.md.consumer;

import com.victor.md.book.BookLevel;
import com.victor.md.book.BookMetadata;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.file.StandardOpenOption;

public class ShmCacheConsumer {
    private static final Logger logger = Logger.getLogger(ShmCacheConsumer.class);

    private final String SHMFILE = "/dev/shm/";

    private int sizeBookCache;
    private String nameBookCache;
    private ByteBuffer bookCache; // change later
    private long bookCacheAddress;
    private int metaDataBytes;
    private int shmBytesPerProduct;
    private int shmBytesOffsetBid;
    private int shmBytesOffsetAsk;
    private short depth;

    public ShmCacheConsumer(String cacheName, int cacheSize) throws IOException {
        this.sizeBookCache = cacheSize;
        this.nameBookCache = cacheName;

        //attach to cache shared memory
        try {
            File attachFile = new File(SHMFILE + nameBookCache);
            FileChannel channel;
            channel = FileChannel.open(attachFile.toPath(), StandardOpenOption.WRITE, StandardOpenOption.READ);

            this.bookCache = channel.map(MapMode.READ_WRITE, 0, cacheSize);
            this.bookCache.order(ByteOrder.LITTLE_ENDIAN);
            bookCacheAddress = makeAddress();
        } catch (IOException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
            logger.error("IOException in attach to book cache, failed to attach to book cache: \n", e);
            throw new IOException(e.getMessage());
        }

        this.depth = 0;
        metaDataBytes = 0;

        for (int pMeta = 0; bookCache.getShort(pMeta + 8) != MdDataType.ExchangeNone;
             pMeta += BookMetadata.sizeOfBookMetadata()) {
            depth += bookCache.get(pMeta + 10);
            metaDataBytes += BookMetadata.sizeOfBookMetadata();
        }
        metaDataBytes += BookMetadata.sizeOfBookMetadata();

        shmBytesPerProduct = (depth + 2) * (BookLevel.sizeOfBookLevel() + BookLevel.sizeOfBookLevel());
        shmBytesOffsetBid = 0;
        shmBytesOffsetAsk = (depth + 2) * BookLevel.sizeOfBookLevel();

        logger.info("nameBookCache: " + nameBookCache + "\tmetaDataBytes: " + metaDataBytes + "\tshmBytesPerProduct: " + shmBytesPerProduct +
                "\tshmBytesOffsetBid: " + shmBytesOffsetBid + "\tshmBytesOffsetAsk: " + shmBytesOffsetAsk);
    }

    public String name() {
        return nameBookCache;
    }

    public final int size() {
        return sizeBookCache;
    }

    public final ByteBuffer getCache() {
        return bookCache;
    }

    public final long getCacheAddr() {
        return bookCacheAddress;
    }

    public final short getDepth() {
        return depth;
    }

    public final int metaDataBytes() {
        return metaDataBytes;
    }

    public final int bytesPerProduct() {
        return shmBytesPerProduct;
    }

    public final int bytesOffsetBid() {
        return shmBytesOffsetBid;
    }

    public final int bytesOffsetAsk() {
        return shmBytesOffsetAsk;
    }

    private long makeAddress() throws NoSuchMethodException, SecurityException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Method addM = bookCache.getClass().getMethod("address");
        addM.setAccessible(true);
        return (long) addM.invoke(bookCache);
    }
}
