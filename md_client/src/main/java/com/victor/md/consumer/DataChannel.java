package com.victor.md.consumer;

import com.victor.md.config.MdConfig;
import com.victor.md.exception.KeyNotExistException;
import com.victor.md.msg.*;
import com.victor.md.util.RingBuffer;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.function.BiConsumer;

public class DataChannel implements Runnable {
    private static final Logger logger = Logger.getLogger(DataChannel.class);

    static class ConfigArgs {
        static final String CONSUMER_POLL_PROP_STR = "poll";
        static final boolean CONSUMER_POLL_PROP_DEFAULT = false;
    }

    private boolean stopped;
    private boolean poll;
    private Thread pollingThread;

    private ArrayList<RingBuffer> queues;

    private MdFeedMsgBuilder feedMsgsBuilder;
    private Header header;
    private DataTradingAction tradingAction;
    private DataBookChanged bookChanged;
    private DataBookRefreshed bookRefreshed;
    private DataHeartbeat heartbeat;

    private BiConsumer<Header, DataTradingAction> onTradingActionFunctor;
    private BiConsumer<Header, DataBookChanged> onBookChangedFunctor;
    private BiConsumer<Header, DataBookRefreshed> onBookRefreshedFunctor;

    private ConsumerCallBacks callbacks;

    public DataChannel(MdConfig mdConfig, String key, ConsumerCallBacks callbacks) throws KeyNotExistException {
        this.stopped = true;

        this.callbacks = callbacks;

        this.queues = new ArrayList<>();

        this.poll = mdConfig.queryBool(key, ConfigArgs.CONSUMER_POLL_PROP_STR, ConfigArgs.CONSUMER_POLL_PROP_DEFAULT);

        initializeMessage();
    }

    private void initializeMessage() {
        this.feedMsgsBuilder = new MdFeedMsgBuilder();
        this.header = new Header();
        this.tradingAction = new DataTradingAction();
        this.bookChanged = new DataBookChanged();
        this.bookRefreshed = new DataBookRefreshed();
        this.heartbeat = new DataHeartbeat();
    }

    void setOnTradingActionFunctor(BiConsumer<Header, DataTradingAction> onTradingActionFunctor) {
        this.onTradingActionFunctor = onTradingActionFunctor;
    }

    void setOnBookChangedFunctor(BiConsumer<Header, DataBookChanged> onBookChangedFunctor) {
        this.onBookChangedFunctor = onBookChangedFunctor;
    }

    void setOnBookRefreshedFunctor(BiConsumer<Header, DataBookRefreshed> onBookRefreshedFunctor) {
        this.onBookRefreshedFunctor = onBookRefreshedFunctor;
    }

    void queue(String name) {
        for (int i = 0; i < queues.size(); i++) {
            if (queues.get(i).getFileName().equals("name")) {
                logger.info("Shared memory queue for " + name + " exists");
                return;
            }
        }
        RingBuffer rb;
        try {
            rb = RingBuffer.attachSharedMemory(name);
            queues.add(rb);
            logger.info("Created a shared memory queue with name " + name);
        } catch (IllegalAccessException e) {
            logger.error("IllegalAccessException in attach to Circular Buffer \n", e);
        } catch (IOException e) {
            logger.error("IOException in attach to Circular Buffer \n", e);
        }
    }

    int queueSize() {
        return queues.size();
    }

    boolean isRunning() {
        return (!stopped && poll);
    }

    boolean isPolling() {
        return poll;
    }

    void start() {
        stopped = false;
        if (poll) {
            pollingThread = new Thread(this);
            pollingThread.start();
            Thread.yield();
        }
    }

    void stop() {
        if (!stopped) {
            stopped = true;
            if (poll) {
                try {
                    pollingThread.join();
                } catch (InterruptedException e) {
                    logger.error("InterruptedException in DataChannel stop \n", e);
                }
            }
        }
    }

    private void run_once() {
        if (stopped)
            return;
        for (int i = 0; i < queues.size(); i++) {
            RingBuffer buf = queues.get(i);
            for (; ; ) { // read as much as there is
                int headerOffset = buf.read(Header.sizeOfHeader());
                if (headerOffset > 0) { // whether can read the header from shared memory
                    feedMsgsBuilder.buildHeader(buf.getBuffer(), headerOffset, header);
                    buf.consume(Header.sizeOfHeader());
                    int dataOffset;
                    if (header.size > 0 && (dataOffset = buf.read(header.size)) > 0) { // read the content
                        for (int c = 0; c < header.count; c++) {
                            //BreakPoint.startTracing("startRead", 2, 2, 1); // UTune Java Tracer
                            switch (buf.getBuffer().get(dataOffset)) { // type
                                case MdDataType.DATA_TRADING_ACTION_TYPE:
                                    tradingAction.loadBuf(buf.getBuffer(), dataOffset);
                                    dataOffset += DataTradingAction.sizeOfDataTradingAction();
                                    onTradingActionFunctor.accept(header, tradingAction);
                                    break;
                                case MdDataType.DATA_BOOK_CHANGED_TYPE:
                                    logger.info("DATA_BOOK_CHANGED_TYPE");
                                    bookChanged.loadBuf(buf.getBuffer(), dataOffset);
                                    dataOffset += DataBookChanged.sizeOfDataBookChanged();
                                    onBookChangedFunctor.accept(header, bookChanged);
                                    callbacks.onBookChanged(new String(bookChanged.symbol()), bookChanged.exchange(), bookChanged.bookChanged());
                                    break;
                                case MdDataType.DATA_BOOK_REFRESHED_TYPE:
                                    logger.info("DATA_BOOK_REFRESHED_TYPE");
                                    bookRefreshed.loadBuf(buf.getBuffer(), dataOffset);
                                    dataOffset += DataBookRefreshed.sizeOfDataBookRefreshed();
                                    onBookRefreshedFunctor.accept(header, bookRefreshed);
                                    break;
                                default:
                                    break;
                            }
                        }
                        buf.consume(header.size);
                    } else if (header.size == 0) { // heartbeat
                        logger.info("DATA_HEARTBEAT_TYPE");
                        heartbeat.hb.loadHeartbeat(header.session, header.streamId, header.transmitTimestamp);
                        callbacks.onHeartbeat(header, heartbeat);
                    } else {
                        // should not happen - as writing of (header + payload) is signalled together
                    }
                } else {
                    break; // read from next queue
                }
            }
        }
    }

    private void runloop() {
        if (poll) {
            while (!stopped) {
                run_once();
            }
        } else {
            throw new RuntimeException("consumer is not in poll mode");
        }
    }

    void run_loop_once() {
        if (poll) {
            throw new RuntimeException("consumer is in poll mode");
        }
        run_once();
    }

    @Override
    public void run() {
        logger.info("Starting data channel(s) for consumer");

        runloop();
    }
}
