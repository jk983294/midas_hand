package com.victor.md.consumer;

import com.victor.md.config.MdConfig;
import com.victor.md.exception.KeyNotExistException;
import com.victor.md.msg.*;
import com.victor.md.util.MpscQueue;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class ControlChannel {
    private static final Logger logger = Logger.getLogger(ControlChannel.class);

    static class ConfigArgs {
        // executor is used to schedule the task. need the time for future get and shutdown timeout
        static final int DISCONNECT_TIMEOUT_DEFAULT = 30;
        static final String DISCONNECT_TIMEOUT_STR = "disconnect_timeout";

        static final byte STREAM_ID_CONTROL_CHANNEL = 0;
        static final short PUBLISHER_PORT_PROP_DEFAULT = 0;
        static final int CONSUMER_HB_INTERVAL_PROP_DEFAULT = 1;
        static final int CONSUMER_MAX_MISSING_HB_INTERVAL_PROP_DEFAULT = 5;
        static final int CONSUMER_PAUSE_MILLISECONDS_AFTER_DISCONNECT_PROP_DEFAULT = 500;
        static final int CONSUMER_CONNECT_TIMEOUT_PROP_DEFAULT = 10;
        static final int CONSUMER_CTRL_QUEUE_SIZE_PROP_DEFAULT = 16777216;
        static final int CONSUMER_NUM_REQUESTS_AT_A_TIME_PROP_DEFAULT = 50;

        static final String PUBLISHER_IP_PROP_STR = "publisher_ip";
        static final String PUBLISHER_PORT_PROP_STR = "publisher_port";
        static final String CONSUMER_IP_PROP_STR = "consumer_ip";
        static final String CONSUMER_CONNECT_TIMEOUT_PROP_STR = "connect_timeout";
        static final String CONSUMER_HB_INTERVAL_PROP_STR = "heartbeat_interval";
        static final String CONSUMER_MAX_MISSING_HB_INTERVAL_PROP_STR = "max_missing_heartbeat_interval";
        static final String CONSUMER_CTRL_QUEUE_PROP_STR = "control_queue";
        static final String CONSUMER_CTRL_QUEUE_PROP_DEFAULT = "mdc_ctrl.queue";
        static final String CONSUMER_CTRL_QUEUE_SIZE_PROP_STR = "control_queue_size";
        static final String CONSUMER_NUM_REQUESTS_AT_A_TIME_PROP_STR = "req_batch_size";
        static final String CONSUMER_PAUSE_MILLISECONDS_AFTER_DISCONNECT_PROP_STR = "pause_ms_after_disconnect";
        static final String CONSUMER_USER_PROP_STR = "user_name";
        static final String CONSUMER_PASSWORD_PROP_STR = "password";
        static final String CONSUMER_NAME_PROP_STR = "name";
        static final String CONSUMER_CLIENT_ID_PROP_STR = "client_id";
    }

    // The offset of xmitt in header
    private static final int XMITTS_OFFSET = 17;

    private int pid;
    private long session;
    private long sequence;
    private ScheduledExecutorService executor;
    private Selector selector;
    private Connection channel;
    private int intervalHeartbeat = 1000;
    private int maxMissingHB = 5;
    private int pauseMillisecondsAfterDisconnect = 250;
    private Runnable channelExecutorTask = null;
    private Runnable heartbeatTimer = null;
    private Runnable keepTickingTimer = null;
    private Runnable periodicalTimer = null;
    private Runnable stopChannelTask = null;
    private String ipPublisher;
    private int portPublisher;
    private String ipConsumer;
    private int connectTimeout;
    private long controlSeqNo;

    private MpscQueue<MdKey> reqSubscribe;
    private MpscQueue<MdKey> reqUnsubscribe;

    private String user;
    private String shmName;
    private byte clientId = 0;

    private int numReqAtATime;
    private boolean connected = false;
    private Lock connectedLock;
    private Condition connectedCV;

    private int disconnectTimeout;

    private Header header;
    private CtrlConnect ctrlConnect;
    private CtrlConnectResponse ctrlConnectResponse;
    private CtrlDisconnectResponse ctrlDisconnectResponse;
    private CtrlSubscribeResponse ctrlSubscribeResponse;
    private CtrlUnsubscribeResponse ctrlUnsubscribeResponse;
    private CtrlDisconnect ctrlDisconnect;
    private CtrlSubscribe ctrlSubscribe;
    private CtrlUnsubscribe ctrlUnsubscribe;
    private ByteBuffer bufRecvHeader;
    private ByteBuffer bufRecv;
    private ByteBuffer bufHeartbeat;
    private ByteBuffer bufSendConnect;
    private ByteBuffer bufSendDisconnect;
    private ByteBuffer bufSendSubscribe;
    private ByteBuffer bufSendUnsubscribe;
    private MdFeedMsgBuilder feedMsgBuilder;

    private ScheduledFuture<?> channelExecutorTaskFuture;
    private ScheduledFuture<?> heartbeatTimerFuture;
    private ScheduledFuture<?> periodicalTimerFuture;
    private ScheduledFuture<?> keepTickingTimerFuture;
    private Future<?> stopChannelTaskFuture;

    private ConsumerCallBacks callbacks;
    private BiFunction<Header, CtrlConnectResponse, Boolean> onConnectionResponseFunctor;
    private BiConsumer<Header, CtrlSubscribeResponse> onSubscriptionResponseFunctor;
    private Consumer<Boolean> updateConnectionStatusFunctor;
    private Runnable resubscribeFunctor;

    private int subscriptionFlags = 0;

    ControlChannel(MdConfig mdConfig, String key, ConsumerCallBacks callbacks, BiFunction<Header, CtrlConnectResponse, Boolean> onConnectionResponseFunctor,
                   BiConsumer<Header, CtrlSubscribeResponse> onSubscriptionResponseFunctor, int pid, long session) throws KeyNotExistException {
        this.pid = pid;
        this.session = session;
        this.sequence = 0;
        this.controlSeqNo = 0;
        this.connected = false;

        this.callbacks = callbacks;
        this.onConnectionResponseFunctor = onConnectionResponseFunctor;
        this.onSubscriptionResponseFunctor = onSubscriptionResponseFunctor;

        this.connectedLock = new ReentrantLock();
        this.connectedCV = connectedLock.newCondition();

        // if cannot find all the needed config arguments, then throw this QueryKeyNotExistException to user
        initialiseConfigArgs(mdConfig, key);

        // initialize feed msgs and bufs, only initial once.
        initialiseMessages();

        this.reqSubscribe = new MpscQueue<>(AtomicReferenceFieldUpdater.newUpdater(MdKey.class, MdKey.class, "nextKey"));
        this.reqUnsubscribe = new MpscQueue<>(AtomicReferenceFieldUpdater.newUpdater(MdKey.class, MdKey.class, "nextKey"));
    }

    void start(int subscriptionFlags) throws IOException {
        this.subscriptionFlags = subscriptionFlags;

        //If cannot open channel, then throw this IOException to user
        this.channel = new Connection();

        executor = Executors.newScheduledThreadPool(1);

        channelExecutorTask = () -> {
            channelExecutorTaskHandler();
        };

        heartbeatTimer = () -> {
            heartbeatTimerHandler();
        };

        keepTickingTimer = () -> {
            keepTickingTimerHandler();
        };

        periodicalTimer = () -> {
            periodicalTimerHandler();
        };

        keepTickingTimerFuture = executor.scheduleWithFixedDelay(keepTickingTimer, 1000, 1000, TimeUnit.MILLISECONDS);
        periodicalTimerFuture = executor.scheduleWithFixedDelay(periodicalTimer, callbacks.callbackInterval(), callbacks.callbackInterval(), TimeUnit.MILLISECONDS);
        heartbeatTimerFuture = executor.scheduleWithFixedDelay(heartbeatTimer, intervalHeartbeat * 1000, intervalHeartbeat * 1000, TimeUnit.MILLISECONDS);
        channelExecutorTaskFuture = executor.scheduleWithFixedDelay(channelExecutorTask, 0, 1, TimeUnit.MILLISECONDS);
    }

    @SuppressWarnings("finally")
    boolean stop() {
        channelExecutorTaskFuture.cancel(false);
        heartbeatTimerFuture.cancel(false);
        periodicalTimerFuture.cancel(false);
        keepTickingTimerFuture.cancel(false);

        stopChannelTask = () -> {
            producerDisconnect();
        };

        stopChannelTaskFuture = executor.submit(stopChannelTask);
        try {
            stopChannelTaskFuture.get(disconnectTimeout * 1000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.error("InterruptedException in waiting stopChannelTask finished: \n", e);
        } catch (ExecutionException e) {
            logger.error("ExecutionException in waiting stopChannelTask finished: \n", e);
        } catch (TimeoutException e) {
            logger.error("TimeoutException in waiting stopChannelTask finished: \n", e);
        } finally {
            executor.shutdown();
            // Wait a while for existing tasks to terminate
            try {
                if (!executor.awaitTermination(disconnectTimeout * 1000, TimeUnit.MILLISECONDS))
                    executor.shutdownNow();// Cancel currently executing tasks
            } catch (InterruptedException e) {
                executor.shutdownNow();
                logger.error("InterruptedException in trying to shutdown executor: \n", e);
            }

            boolean awaitStatus = false;

            try {
                awaitStatus = executor.awaitTermination(disconnectTimeout * 1000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                logger.error("InterruptedException in trying to shutdown executor: \n", e);
            } finally {
                // if go into the InterruptedException cases, not sure whether producerDisconnect() has been called or not
                // change connected to false explicitly
                onConnectedChange(false);
                return awaitStatus;
            }
        }
    }

    private void onConnectedChange(boolean value) {
        connected = value;
        updateConnectionStatusFunctor.accept(connected);
    }

    void setResubscribeFunctor(Runnable resubscribeFunctor) {
        this.resubscribeFunctor = resubscribeFunctor;
    }

    void setUpdateConnectionStatusFunctor(Consumer<Boolean> updateConnectionStatusFunctor) {
        this.updateConnectionStatusFunctor = updateConnectionStatusFunctor;
    }

    boolean isConnected() {
        return connected;
    }

    private void initialiseConfigArgs(MdConfig mdConfig, String key) throws KeyNotExistException {
        this.ipPublisher = mdConfig.queryString(key, ConfigArgs.PUBLISHER_IP_PROP_STR);

        this.portPublisher = mdConfig.queryInt(key, ConfigArgs.PUBLISHER_PORT_PROP_STR, ConfigArgs.PUBLISHER_PORT_PROP_DEFAULT);

        this.ipConsumer = mdConfig.queryString(key, ConfigArgs.CONSUMER_IP_PROP_STR);

        this.connectTimeout = mdConfig.queryInt(key, ConfigArgs.CONSUMER_CONNECT_TIMEOUT_PROP_STR, ConfigArgs.CONSUMER_CONNECT_TIMEOUT_PROP_DEFAULT);

        this.intervalHeartbeat = mdConfig.queryInt(key, ConfigArgs.CONSUMER_HB_INTERVAL_PROP_STR, ConfigArgs.CONSUMER_HB_INTERVAL_PROP_DEFAULT);

        this.maxMissingHB = mdConfig.queryInt(key, ConfigArgs.CONSUMER_MAX_MISSING_HB_INTERVAL_PROP_STR, ConfigArgs.CONSUMER_MAX_MISSING_HB_INTERVAL_PROP_DEFAULT);

        this.numReqAtATime = mdConfig.queryInt(key, ConfigArgs.CONSUMER_NUM_REQUESTS_AT_A_TIME_PROP_STR, ConfigArgs.CONSUMER_NUM_REQUESTS_AT_A_TIME_PROP_DEFAULT);

        this.pauseMillisecondsAfterDisconnect = mdConfig.queryInt(key, ConfigArgs.CONSUMER_PAUSE_MILLISECONDS_AFTER_DISCONNECT_PROP_STR, ConfigArgs.CONSUMER_PAUSE_MILLISECONDS_AFTER_DISCONNECT_PROP_DEFAULT);

        this.disconnectTimeout = mdConfig.queryInt(key, ConfigArgs.DISCONNECT_TIMEOUT_STR, ConfigArgs.DISCONNECT_TIMEOUT_DEFAULT);

        this.user = mdConfig.queryString(key, ConfigArgs.CONSUMER_USER_PROP_STR);

        this.shmName = mdConfig.queryString(key, ConfigArgs.CONSUMER_NAME_PROP_STR);

        this.clientId = (byte) mdConfig.queryShort(key, ConfigArgs.CONSUMER_CLIENT_ID_PROP_STR);
    }

    private void initialiseMessages() {
        this.header = new Header();
        this.ctrlConnect = new CtrlConnect();
        this.ctrlConnectResponse = new CtrlConnectResponse();
        this.ctrlDisconnectResponse = new CtrlDisconnectResponse();
        this.ctrlSubscribeResponse = new CtrlSubscribeResponse();
        this.ctrlUnsubscribeResponse = new CtrlUnsubscribeResponse();
        this.ctrlDisconnect = new CtrlDisconnect();
        this.ctrlSubscribe = new CtrlSubscribe();
        this.ctrlUnsubscribe = new CtrlUnsubscribe();
        this.bufRecv = ByteBuffer.allocate(1024);
        this.bufRecvHeader = ByteBuffer.allocate(Header.sizeOfHeader());
        this.bufHeartbeat = ByteBuffer.allocate(Header.sizeOfHeader());
        this.bufSendConnect = ByteBuffer.allocate(Header.sizeOfHeader() + CtrlConnect.sizeOfCtrlConnect());
        this.bufSendDisconnect = ByteBuffer.allocate(Header.sizeOfHeader() + CtrlDisconnect.sizeOfCtrlDisconnect());
        this.bufSendSubscribe = ByteBuffer.allocate(Header.sizeOfHeader() + CtrlSubscribe.sizeOfCtrlSubscribe());
        this.bufSendUnsubscribe = ByteBuffer.allocate(Header.sizeOfHeader() + CtrlUnsubscribe.sizeOfCtrlUnsubscribe());
        bufRecv.order(ByteOrder.LITTLE_ENDIAN);
        bufRecvHeader.order(ByteOrder.LITTLE_ENDIAN);
        bufHeartbeat.order(ByteOrder.LITTLE_ENDIAN);
        bufSendConnect.order(ByteOrder.LITTLE_ENDIAN);
        bufSendDisconnect.order(ByteOrder.LITTLE_ENDIAN);
        bufSendSubscribe.order(ByteOrder.LITTLE_ENDIAN);
        bufSendUnsubscribe.order(ByteOrder.LITTLE_ENDIAN);
        this.feedMsgBuilder = new MdFeedMsgBuilder();
    }

    private void channelExecutorTaskHandler() {
        try {
            producerConnect();
            selector = channel.getSelector();
            if (selector.select(200) > 0) {
                Iterator<?> keyIterator = selector.selectedKeys().iterator();
                while (keyIterator.hasNext()) {
                    SelectionKey key = (SelectionKey) keyIterator.next();
                    if (key.isReadable()) {
                        int nPackets = recv(1);
                        if (nPackets < 0) { // socket would have been closed in recv
                            logger.warn("Publisher hung up?");
                            onConnectedChange(false);
                            channel.closeSocket();
                            callbacks.onControlChannelDisconnected();
                            Thread.sleep(pauseMillisecondsAfterDisconnect);
                        }
                    }
                    keyIterator.remove();
                }
            }
        } catch (IOException e) {
            logger.error("IOException in channel Executor: \n", e);
        } catch (InterruptedException e) {
            logger.error("InterruptedException in channel Executor: \n", e);
        }
    }

    private int recv(int nPackets) {
        bufRecv.clear();
        bufRecvHeader.clear();
        int packetsRcvd = 0;
        boolean cleanup = false;

        for (; ; ) {
            int n = 0;
            try {
                n = channel.readSocket(bufRecvHeader, Header.sizeOfHeader());
            } catch (IOException e) {
                cleanup = true;
                logger.error("IOException in readSocket: \n", e);
                break;
            }
            if (n > 0) {
                bufRecvHeader.flip();
                feedMsgBuilder.buildHeader(bufRecvHeader, bufRecvHeader.position(), header);
                if (header.size == 0) {
                    // heartbeat. Now producer doesn't send heartbeat to MDConsumer, but may will...
                } else {
                    try {
                        n = channel.readSocket(bufRecv, header.size);
                    } catch (IOException e) {
                        cleanup = true;
                        logger.error("IOException in readSocket: \n", e);
                        break;
                    }
                    if (n > 0) {
                        boolean isConnected = false;
                        bufRecv.flip();
                        int offset = 0;
                        for (int c = 0; c < header.count; c++) {
                            switch (bufRecv.get(offset)) // type
                            {
                                case MdDataType.CTRL_CONNECT_RESPONSE_TYPE:
                                    logger.info("Dispatching onConnectResponse");
                                    feedMsgBuilder.buildCtrlConnectResponse(bufRecv, offset, ctrlConnectResponse);
                                    offset += CtrlConnectResponse.sizeOfCtrlConnectResponse();
                                    isConnected = onConnectionResponseFunctor.apply(header, ctrlConnectResponse);
                                    break;
                                case MdDataType.CTRL_DISCONNECT_RESPONSE_TYPE:
                                    logger.info("Dispatching onDisconnectResponse");
                                    /*
                                     * Never get chance to be called. The task to process the channel's coming msg
                                     * will be stop first before sending disconnect response.
                                     */
                                    feedMsgBuilder.buildCtrlDisconnectResponse(bufRecv, offset, ctrlDisconnectResponse);
                                    offset += CtrlDisconnectResponse.sizeOfCtrlDisconnectResponse();
                                    callbacks.onDisconnectResponse(header, ctrlDisconnectResponse);
                                    break;
                                case MdDataType.CTRL_SUBSCRIBE_RESPONSE_TYPE:
                                    feedMsgBuilder.buildCtrlSubscribeResponse(bufRecv, offset, ctrlSubscribeResponse);
                                    offset += CtrlSubscribeResponse.sizeOfCtrlSubscribeResponse();
                                    onSubscriptionResponseFunctor.accept(header, ctrlSubscribeResponse);
                                    break;
                                case MdDataType.CTRL_UNSUBSCRIBE_RESPONSE_TYPE:
                                    feedMsgBuilder.buildCtrlUnsubscribeResponse(bufRecv, offset, ctrlUnsubscribeResponse);
                                    offset += CtrlUnsubscribeResponse.sizeOfCtrlUnsubscribeResponse();
                                    callbacks.onUnsubscribeResponse(header, ctrlUnsubscribeResponse);
                                    break;
                                default:
                                    break;
                            }
                        }

                        // Multiple CtrlConnectResponse is delivered in message unit
                        // Call 'signalConnected' only when the entire unit is processed
                        if (isConnected) {
                            signalConnected();
                            callbacks.onControlChannelEstablished();
                            resubscribeFunctor.run();
                        }
                    } else {
                        cleanup = true;
                        break;
                    }
                }
                if (++packetsRcvd == nPackets) { // won't ever break if npkts == 0
                    break;
                }
            } else {
                cleanup = true;
                break;
            }
        }

        if (cleanup) {
            try {
                channel.closeSocket();
            } catch (IOException e) {
                logger.error("IOException in closing socket: \n", e);
            }
        }
        return (cleanup ? -1 : packetsRcvd);
    }

    private void signalConnected() {
        logger.info("handshake with producer completed");
        connectedLock.lock();
        try {
            onConnectedChange(true);
            connectedCV.signal();
        } finally {
            connectedLock.unlock();
        }
    }

    private boolean producerConnect() throws IOException {
        boolean cleanup = false;

        if (!channel.isConnected()) {
            if (channel.makeTCPSocketClient(ipConsumer, 0, ipPublisher, portPublisher, connectTimeout)) {
                callbacks.onControlChannelConnected();

                if (!channel.getSelector().isOpen()) {
                    cleanup = true;
                } else {
                    // now do the HANDSHAKE by sending a connect message
                    if (sendConnect() != ConsumerStatus.OK.value) {
                        cleanup = true;
                    }
                }
            }
        }

        if (cleanup) {
            channel.closeSocket();
        }
        return channel.isConnected();
    }

    private void producerDisconnect() {
        // send a disconnect msg to publisher
        // don't worry about if msg was successfully sent - Publisher will know one way or other
        try {
            sendDisconnect();
            channel.closeSocket();
        } catch (IOException e) {
            logger.error("IOException in producerDisconnect: \n", e);
        } finally {
            onConnectedChange(false);
            logger.info("Control channel is now closed!");
        }
    }

    private int sendDisconnect() throws IOException {
        bufSendDisconnect.clear();
        feedMsgBuilder.buildHeaderBuffer(bufSendDisconnect, session,
                ++controlSeqNo, 0, ConfigArgs.STREAM_ID_CONTROL_CHANNEL, (byte) 1, CtrlDisconnect.sizeOfCtrlDisconnect())
                .buildCtrlDisconnectBuffer(bufSendDisconnect, ctrlDisconnect.type, ctrlDisconnect.msgSize, pid);

        bufSendDisconnect.putLong(XMITTS_OFFSET, System.nanoTime());

        int writeBytes = channel.writeSocket(bufSendDisconnect, Header.sizeOfHeader() + CtrlDisconnect.sizeOfCtrlDisconnect());
        return (writeBytes == (Header.sizeOfHeader() + CtrlDisconnect.sizeOfCtrlDisconnect()) ? ConsumerStatus.OK.value : ConsumerStatus.NOT_CONNECTED.value);
    }

    private int sendConnect() throws IOException {
        bufSendConnect.clear();

        //change string to byte array
        byte[] bytes = user.getBytes();
        System.arraycopy(bytes, 0, ctrlConnect.user, 0, bytes.length);
        bytes = "123456".getBytes();
        System.arraycopy(bytes, 0, ctrlConnect.pwd, 0, bytes.length);
        bytes = shmName.getBytes();
        System.arraycopy(bytes, 0, ctrlConnect.shmKey, 0, bytes.length);

        //header
        feedMsgBuilder.buildHeaderBuffer(bufSendConnect, session,
                ++controlSeqNo, 0, ConfigArgs.STREAM_ID_CONTROL_CHANNEL, (byte) 1, CtrlConnect.sizeOfCtrlConnect())
                .buildCtrlConnectBuffer(bufSendConnect, ctrlConnect.type, ctrlConnect.msgSize, pid,
                        clientId, subscriptionFlags, ctrlConnect.user, ctrlConnect.pwd, ctrlConnect.shmKey);

        bufSendConnect.putLong(XMITTS_OFFSET, System.nanoTime());

        int writeBytes = channel.writeSocket(bufSendConnect, Header.sizeOfHeader() + CtrlConnect.sizeOfCtrlConnect());
        return (writeBytes == (Header.sizeOfHeader() + CtrlConnect.sizeOfCtrlConnect()) ? ConsumerStatus.OK.value : ConsumerStatus.NOT_CONNECTED.value);
    }

    boolean waitUntilConnected() {
        boolean result = false;
        connectedLock.lock();
        try {
            while (!connected) {
                try {
                    result = connectedCV.await(connectTimeout * 1000, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    logger.error("InterruptedException in waiting connected condition variable: \n", e);
                }
                if (!result)
                    break;
            }
            if (!result) {
                logger.info("Time out in waitUnitlConnected");
            } else if (connected) {
                logger.info("Connected already");
            }
        } finally {
            connectedLock.unlock();
        }
        return connected;
    }

    private void heartbeatTimerHandler() {
        if (connected) {
            try {
                sendHeartbeat();
            } catch (IOException e) {
                logger.error("IOException in sending heartbeat: \n", e);
            }
        }
    }

    private int sendHeartbeat() throws IOException {
        bufHeartbeat.clear();
        feedMsgBuilder.buildHeaderBuffer(bufHeartbeat, session, ++controlSeqNo,
                System.nanoTime(), ConfigArgs.STREAM_ID_CONTROL_CHANNEL, (byte) 0, (short) 0);

        int writeBytes = channel.writeSocket(bufHeartbeat, Header.sizeOfHeader());
        return (writeBytes == bufHeartbeat.capacity() ? ConsumerStatus.OK.value : ConsumerStatus.NOT_CONNECTED.value);
    }

    ConsumerStatus queueSubscribe(MdKey key) {
        reqSubscribe.put(key);
        return ConsumerStatus.OK;
    }

    ConsumerStatus sendSubscribe(byte[] symbol, short exchange) {
        if (!connected) {
            return ConsumerStatus.NOT_CONNECTED;
        } else {
            logger.info("Session: " + session + "\tSequence: " + (controlSeqNo + 1) + "\tStreamID " + ConfigArgs.STREAM_ID_CONTROL_CHANNEL + "\tsymbol: " + new String(symbol) + "\texchange: " + exchange);

            bufSendSubscribe.clear();

            feedMsgBuilder.buildHeaderBuffer(bufSendSubscribe, session, ++controlSeqNo, 0, ConfigArgs.STREAM_ID_CONTROL_CHANNEL, (byte) 1, CtrlSubscribe.sizeOfCtrlSubscribe())
                    .buildCtrlSubscribeBuffer(bufSendSubscribe, ctrlSubscribe.type, ctrlSubscribe.msgSize, symbol, exchange, ctrlSubscribe.flags);

            bufSendSubscribe.putLong(XMITTS_OFFSET, System.nanoTime());

            int writeBytes = -1;
            try {
                writeBytes = channel.writeSocket(bufSendSubscribe, Header.sizeOfHeader() + CtrlSubscribe.sizeOfCtrlSubscribe());
                return (writeBytes == bufSendSubscribe.capacity() ? ConsumerStatus.OK : ConsumerStatus.NOT_CONNECTED);
            } catch (IOException e) {
                // If IO exception happens here, we do nothing just return the not connected return code,
                // The keepTickingTimer will get this return code and repush the subscription into queue.
                // The recv() in ChannelExecutor will get the IOException again from channel and close this channel.
                logger.error("IOException in write subscription into socket", e);
                return ConsumerStatus.NOT_CONNECTED;
            }
        }
    }

    ConsumerStatus queueUnsubscribe(MdKey key) {
        reqUnsubscribe.put(key);
        return ConsumerStatus.OK;
    }

    ConsumerStatus sendUnsubscribe(byte[] symbol, short exchange) {
        if (!connected) {
            return ConsumerStatus.NOT_CONNECTED;
        } else {
            logger.info("Session: " + session + "\tSequence: " + (controlSeqNo + 1) + "\tStreamID" + ConfigArgs.STREAM_ID_CONTROL_CHANNEL + "\tsymbol: " + symbol + "\texchange: " + exchange);

            bufSendUnsubscribe.clear();

            feedMsgBuilder.buildHeaderBuffer(bufSendUnsubscribe, session, ++controlSeqNo, 0, ConfigArgs.STREAM_ID_CONTROL_CHANNEL, (byte) 1, CtrlUnsubscribe.sizeOfCtrlUnsubscribe())
                    .buildCtrlUnsubscribeBuffer(bufSendUnsubscribe, ctrlUnsubscribe.type, ctrlUnsubscribe.msgSize, symbol, exchange);

            bufSendUnsubscribe.putLong(XMITTS_OFFSET, System.nanoTime());

            int writeBytes = -1;
            try {
                writeBytes = channel.writeSocket(bufSendUnsubscribe, Header.sizeOfHeader() + CtrlUnsubscribe.sizeOfCtrlUnsubscribe());
                return (writeBytes == bufSendUnsubscribe.capacity() ? ConsumerStatus.OK : ConsumerStatus.NOT_CONNECTED);
            } catch (IOException e) {
                // If IO exception happens here, we do nothing just return the not connected return code,
                // The keepTickingTimer will get this return code and repush the subscription into queue.
                // The recv() in ChannelExecutor will get the IOException again from channel and close this channel.
                logger.error("IOException in write unsubscription into socket", e);
                return ConsumerStatus.NOT_CONNECTED;
            }
        }
    }

    private void keepTickingTimerHandler() {
        if (connected) {
            ConsumerStatus rc;
            MdKey s;
            for (int nreq = 0; nreq < numReqAtATime; ++nreq) { //Handle subscribe requests
                s = reqSubscribe.get();
                if (s == null) {
                    break; // no more requests in queue
                }

                rc = sendSubscribe(s.symbol, s.exchange);
                if (rc != ConsumerStatus.OK) {
                    logger.warn("Failed to send subscribe request for " + s.symbol + "." + s.exchange);
                    reqSubscribe.put(s);
                    break; // don't try further at this point
                }
            }

            for (int nReq = 0; nReq < numReqAtATime; ++nReq) { // Handle unsubscribe requests
                s = reqUnsubscribe.get();
                if (s == null) {
                    break; // no more requests in queue
                }

                rc = sendUnsubscribe(s.symbol, s.exchange);
                if (rc != ConsumerStatus.OK) {
                    logger.warn("Failed to send unsubscribe request for " + s.symbol + "." + s.exchange);
                    reqUnsubscribe.put(s);
                    break; // don't try further at this point
                }
            }
        }
    }

    private void periodicalTimerHandler() {
        if (connected) {
            callbacks.onPeriodically();
        }
    }
}
