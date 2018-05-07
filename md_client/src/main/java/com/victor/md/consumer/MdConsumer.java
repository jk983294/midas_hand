package com.victor.md.consumer;

import com.victor.md.book.BookLevel;
import com.victor.md.book.MdBook;
import com.victor.md.config.MdConfig;
import com.victor.md.exception.KeyNotExistException;
import com.victor.md.msg.*;
import com.victor.md.util.PidAccess;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

public class MdConsumer {
    private static final Logger logger = Logger.getLogger(MdConsumer.class);

    public MdConfig mdConfig;

    public int pid;
    public byte clientId = 0;
    public long session;
    public long startTime;

    private ControlChannel controlChannel;
    private DataChannel dataChannel;

    private ConsumerCallBacks callbacks;

    private HashMap<MdKey, MdTicker> subscriptionByName = new HashMap<>();
    private Short2ObjectOpenHashMap<ShmCacheConsumer> bookCaches = new Short2ObjectOpenHashMap<>(); // exchCode -> cache

    private final byte[] MD_BOOK_WATERMARK = {'b', 'o', 'o', 'k', 'M', 'e', 'T', 'A'};
    private byte[] mdBookWaterMark = new byte[8];

    // To avoid data race, three MDKey need to be used:
    // keyForSearch is for dataPoll, on user thread or data poll thread.
    // keyForSubResponse is for subscription response, on control channel thread.
    // keyForSub is for subscription, on user thread.
    private MdKey keyForSearch;
    private MdKey keyForSubResponse;
    private MdKey keyForSub;
    private byte[] subSymbolBytes;

    public MdConsumer(String fileConfig, String key, ConsumerCallBacks callbacks) throws KeyNotExistException, IOException {
        this.pid = PidAccess.getPID();
        this.session = 0;
        this.startTime = System.currentTimeMillis() / 1000;

        this.callbacks = callbacks;

        mdConfig = new MdConfig();
        mdConfig.load(fileConfig);

        this.dataChannel = new DataChannel(mdConfig, key, callbacks);
        dataChannel.setOnTradingActionFunctor((Header header, DataTradingAction ta) -> onTradingAction(header, ta));
        dataChannel.setOnBookChangedFunctor((Header header, DataBookChanged dbc) -> onBookChanged(header, dbc));
        dataChannel.setOnBookRefreshedFunctor((Header header, DataBookRefreshed dbr) -> onBookRefreshed(header, dbr));

        this.controlChannel = new ControlChannel(mdConfig, key, callbacks,
                (Header header, CtrlConnectResponse crs) -> onConnectResponse(header, crs),
                (Header header, CtrlSubscribeResponse srsp) -> onSubscribeResponse(header, srsp),
                pid, session);

        controlChannel.setResubscribeFunctor(() -> resubscribe());
        controlChannel.setUpdateConnectionStatusFunctor((Boolean connected) -> updateConnectionResponse(connected));

        this.keyForSearch = new MdKey();
        this.keyForSubResponse = new MdKey();
        this.keyForSub = new MdKey();
        subSymbolBytes = new byte[9];
    }

    /**
     * Create a sessionId with an Publisher
     * Initiates a TCP connection to a configured Publisher running on the same server.
     * This function will not return until a response is received from Publisher or it is determined
     * that a connection to Publisher cannot be established.
     * On receiving response from Publisher, book cache and event queues in shared memory are attached to
     * before this function returns.
     *
     * @throws IOException : If fail open config file or initialize control channel.
     */
    public void start() throws IOException {
        controlChannel.start(callbacks.subscriptionFlags);
        controlChannel.waitUntilConnected();

        dataChannel.start();
    }

    /**
     * Disconnect from an Publisher in orderly manner
     * Detaches from both book cache and event queues in shared memory.
     * Unsubscribes from all currently subscribed symbols.
     * Sends a disconnect message to Publisher.
     */
    public void stop() {
        dataChannel.stop();
        controlChannel.stop();
    }

    /**
     * Polls shared memory and invoke callback(s) on queued events; returns when there is no queued event left.
     * Should be called when user wants to do data polling by themselves,
     * which means there is no polling thread in data channel.
     */
    public void dataPoll() {
        dataChannel.run_loop_once();
    }

    /**
     * Subscribes to name 'symbol.exchange'
     * Creates an internal subscription object or increases reference counter if already subscribed.
     * Sends a subscribe request to Publisher if this is a new subscription.
     * Do not accept duplicate subscriptions.
     */
    public ConsumerStatus subscribe(MdSubscription subscription, String symbol, short exchange) {
        return subscribe(subscription, symbol, exchange, false);
    }

    /**
     * Subscribes to name symbol
     * Creates an internal subscription object or increases reference counter if already subscribed.
     * Sends a subscribe request to Publisher if this is a new subscription.
     */
    public ConsumerStatus subscribe(MdSubscription subscription, String symbol, short exchange, boolean duplicateOK) {
        logger.info("Consumer subscribing to " + symbol + " ,exchange " + exchange + " ,duplicationOK " + duplicateOK);

        if (symbol.length() >= CtrlSubscribe.sizeOfSymbol()) {
            return ConsumerStatus.BAD_SYMBOL;
        }

        synchronized (this) {
            Arrays.fill(subSymbolBytes, (byte) 0);

            for (int i = 0; i < symbol.length(); i++) {
                subSymbolBytes[i] = (byte) symbol.charAt(i);
            }

            keyForSub.symbol = subSymbolBytes;
            keyForSub.exchange = exchange;

            if (!bookCaches.containsKey(exchange)) {
                logger.warn("Cannot subscribe to: " + symbol + "." + symbol + ": No bookCache");
                return ConsumerStatus.BAD_BOOK;
            }
            ShmCacheConsumer bookCache = bookCaches.get(exchange);

            if (subscriptionByName.containsKey(keyForSub)) {
                MdTicker ticker = subscriptionByName.get(keyForSub);
                if (duplicateOK) {
                    ticker.subscriptionAdd(subscription);
                    logger.info("Duplicated subscribed to " + symbol);
                    return ConsumerStatus.OK;
                } else {
                    logger.info("Already subscribed to " + symbol);
                    return ConsumerStatus.ALREADY_SUBSCRIBED;
                }
            } else {
                MdTicker ticker = new MdTicker(symbol, subSymbolBytes, exchange, bookCache.getCache(), bookCache.getCacheAddr(), bookCache.metaDataBytes(), bookCache.bytesPerProduct(), bookCache.bytesOffsetBid(), bookCache.bytesOffsetAsk(), callbacks);
                ticker.setConnected(controlChannel.isConnected());

                subscriptionByName.put(ticker.mdKey(), ticker);
                ticker.subscriptionAdd(subscription);
                ticker.state(MdTicker.State.pending);
                ConsumerStatus status = controlChannel.queueSubscribe(ticker.mdKey());
                if (status == ConsumerStatus.OK) {
                    logger.info("Queued subscribe request for " + symbol);
                } else {
                    logger.warn("Failed to queue subscribe request for " + symbol);
                }
                return status;
            }
        }
    }

    /**
     * Unsubscribes from name 'symbol.exchange'
     * Decrements reference count of internal subscription object.
     * Sends a unsubscribe request to Publisher if reference count drops to 0 if control channel
     * is connected, otherwise defers sending request but returns CONSUMER_OK anyway.
     */
    public ConsumerStatus unsubscribe(MdSubscription sub) {
        ConsumerStatus rc = ConsumerStatus.NOT_SUBSCRIBED;

        String symbol = sub.symbol();
        short exchange = sub.exchange();

        MdKey keyForUnsub = sub.mdKey();

        synchronized (this) {
            if (subscriptionByName.containsKey(keyForUnsub)) {
                MdTicker ticker = subscriptionByName.get(keyForUnsub);
                if (ticker.subscriptionDelete(sub)) {
                    sub = null; // now delete the matching subscription

                    if (ticker.subscriptionCount() == 0) { //Ticker can be removed
                        if ((rc = controlChannel.queueUnsubscribe(keyForUnsub)) == ConsumerStatus.OK) {
                            subscriptionByName.remove(keyForUnsub, ticker);
                            logger.info("Queued unsubscribe request for " + symbol + "." + exchange);
                        } else {
                            logger.warn("Failed to queue unsubscribe request for" + symbol + "." + exchange);
                        }
                    }
                }
            } else {
                logger.warn("No such subscription " + symbol + "." + exchange);
            }
        }
        return rc;
    }

    /**
     * access book meta data, allocates memory for book.
     * alloc bid or ask book space directly.
     */
    public ConsumerStatus allocBook(short exchange, MdBook book) {
        return allocBook(exchange, book, true);
    }

    /**
     * access book meta data, allocates memory for book.
     */
    public ConsumerStatus allocBook(short exchange, MdBook book, boolean alloc) {
        ConsumerStatus rc = ConsumerStatus.BAD_BOOK;

        if (!bookCaches.containsKey(exchange)) {
            return rc;
        }
        ShmCacheConsumer bookCache = bookCaches.get(exchange);

        if (book != null && bookCache != null) {
            ByteBuffer thunk = bookCache.getCache();
            book.loadBookCache(thunk, bookCache.getCacheAddr(), MdBook.BookType.price, alloc);

            for (int i = 0; i < MD_BOOK_WATERMARK.length; i++)
                mdBookWaterMark[i] = thunk.get(i);
            if (thunk != null && Arrays.equals(mdBookWaterMark, MD_BOOK_WATERMARK)) {
                short depth = bookCache.getDepth();
                if (alloc) {
                    book.allocBookMemory(depth, depth * BookLevel.sizeOfBookLevel(), depth * BookLevel.sizeOfBookLevel());
                } else {
                    book.allocBookMemory(depth, 0, 0);
                }
                rc = ConsumerStatus.OK;
            } else {
                logger.error("water mark not correct");
            }
        }
        return rc;
    }

    /**
     * Copies book from shared memory into 'mdbook' in app memory.
     */
    public ConsumerStatus snap(MdSubscription sub, int bookSide, MdBook book) {
        return sub.snap(bookSide, book);
    }

    /**
     * Get the data channel's circular buffer size.
     */
    public int dataChannelQueueSize() {
        return dataChannel.queueSize();
    }

    /**
     * Get the running status of data channel's polling thread.
     */
    public boolean isRunning() {
        return dataChannel.isRunning();
    }

    /**
     * Get the polling thread configed status of data channel.
     */
    public boolean isPolling() {
        return dataChannel.isPolling();
    }


    private void resubscribe() {
        synchronized (this) {
            for (Entry<MdKey, MdTicker> entry : subscriptionByName.entrySet()) {
                MdTicker ticker = entry.getValue();
                MdKey key = entry.getKey();

                logger.info("Resubscribing to " + ticker.symbol());
                if (controlChannel.sendSubscribe(key.symbol, ticker.exchange()) == ConsumerStatus.OK) {
                    logger.info("Posted resubscribe request for " + ticker.symbol() + " to control channel");
                } else {
                    controlChannel.queueSubscribe(key);
                    logger.info("Failed to queue resubscribe request for " + ticker.symbol());
                }
            }
        }
    }

    private void updateConnectionResponse(boolean connected) {
        synchronized (this) {
            logger.info("changing all the ticker's control channel connected status to: " + connected);
            for (Entry<MdKey, MdTicker> entry : subscriptionByName.entrySet()) {
                MdTicker ticker = entry.getValue();
                ticker.setConnected(connected);
            }
        }
    }

    private boolean onConnectResponse(Header header, CtrlConnectResponse crs) {
        if (crs.status == FeedStatus.CONNECT_STATUS_OK.value) {
            session = header.session; //Note the sessionId
            String cacheName = new String(crs.shmPath).trim();
            switch (crs.shmType) {
                case MdDataType.SHM_TYPE_EVENT_QUEUE:
                    logger.info("Ring buffer case:" + cacheName);
                    dataChannel.queue(cacheName);
                    break;
                case MdDataType.SHM_TYPE_BOOK_CACHE:
                    logger.info("Book cache case:" + cacheName);
                    if (!bookCaches.containsKey(crs.exchange)) {
                        try {
                            bookCaches.put(crs.exchange, new ShmCacheConsumer(cacheName, crs.shmSize));
                        } catch (IOException e) {
                            return false;
                        }
                    } else if (!bookCaches.get(crs.exchange).name().equals(cacheName)) {
                        logger.error("Existing bookCache for exchange " + crs.exchange + " found, but name mismatch; expected: " + cacheName +
                                ", actual: " + bookCaches.get(crs.exchange).name());
                        return false;
                    }
                    break;
                default:
                    //can't be here
                    logger.warn("Unknown response type from producer connection response");
                    break;
            }
            return true;
        } else {
            if (crs.status == FeedStatus.CONNECT_STATUS_ALREADY_CONNECTED.value) {
                logger.info("client " + crs.cid + " is already connected");
            }
            callbacks.getOnConnection().accept(FeedStatus.fromValue(crs.status));
        }
        return false;
    }

    private void onSubscribeResponse(Header header, CtrlSubscribeResponse response) {
        logger.info("Received SubscribeResponse, status " + response.status + "\t locate " + response.locate
                + "\tsymbol " + new String(response.symbol, Charset.forName("ISO-8859-1"))
                + "\texchange " + response.exchange);

        boolean subOk = MdDataType.SUBSCRIBE_STATUS_OK == response.status;
        final short key = response.exchange;

        synchronized (this) {
            // the key used here should be based on the composite key
            keyForSubResponse.symbol = response.symbol;
            keyForSubResponse.exchange = key;

            if (subscriptionByName.containsKey(keyForSubResponse)) {
                if (subOk) {
                    subscriptionByName.get(keyForSubResponse).locate(response.locate);
                }
                MdTicker ticker = subscriptionByName.get(keyForSubResponse);
                ticker.onSubscribeResponse(ticker.symbol(), key, subOk ? ConsumerStatus.OK : ConsumerStatus.BAD_SYMBOL);
            }
        }
    }

    private void onTradingAction(Header header, DataTradingAction ta) {
        synchronized (this) {
            final short exchange = ta.exchange();
            keyForSearch.symbol = ta.symbol();
            keyForSearch.exchange = exchange;

            MdTicker ticker = subscriptionByName.get(keyForSearch);
            if (ticker != null) {
                ticker.onTradingAction(ticker.symbol(), exchange, ta.tradingAction());
            }
        }
    }

    private void onBookChanged(Header header, DataBookChanged chngd) {
        synchronized (this) {
            final short exchange = chngd.exchange();
            keyForSearch.symbol = chngd.symbol();

            MdTicker ticker = subscriptionByName.get(keyForSearch);
            if (ticker != null) {
                ticker.onBookChanged(ticker.symbol(), exchange, chngd.bookChanged());
            }
        }
    }

    private void onBookRefreshed(Header header, DataBookRefreshed rfr) {
        synchronized (this) {
            final short exchange = rfr.exchange();
            final short locate = rfr.locate();
            keyForSearch.symbol = rfr.symbol();
            keyForSearch.exchange = exchange;

            MdTicker ticker = subscriptionByName.get(keyForSearch);
            if (ticker != null) {
                //Look up the ticker first
                ticker.streamId(header.streamId);
                ticker.locate(locate);
                ticker.onBookRefreshed(ticker.symbol(), exchange);
            }
        }
    }

}
