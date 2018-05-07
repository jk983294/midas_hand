package com.victor.md.consumer;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class Connection {
    private static final Logger logger = Logger.getLogger(Connection.class);

    private SocketChannel channel;
    private Selector selector;
    private InetSocketAddress localSocketAddr;
    private InetSocketAddress remoteSocketAddr;
    private String addrLocal;
    private int portLocal;
    private String addrRemote;
    private int portRemote;

    public Connection() throws IOException {
        this.selector = Selector.open();
        this.channel = SocketChannel.open();
    }

    boolean isConnected() {
        return channel.isConnected();
    }

    void setSendBuffer(int bufSize) {
        try {
            channel.socket().setSendBufferSize(bufSize);
        } catch (SocketException e) {
            //If error happens here, just using default bufSize
            logger.error("SocketException in set send buffer size: \n", e);
        }
    }

    void setRecvBuf(int bufSize) {
        try {
            channel.socket().setReceiveBufferSize(bufSize);
        } catch (SocketException e) {
            //If error happens here, just using default bufSize
            logger.error("SocketException in set receive buffer size: \n", e);
        }
    }

    boolean makeTCPSocketClient(String addrLocal, int portLocal, String addrRemote, int portRemote, int timeout) {
        boolean connectionStatus = false;
        try {
            if (!channel.isOpen())
                channel = SocketChannel.open();
            if (localSocketAddr == null || !this.addrLocal.equals(addrLocal) || this.portLocal != portLocal) {
                localSocketAddr = new InetSocketAddress(addrLocal, portLocal);
                this.addrLocal = addrLocal;
                this.portLocal = portLocal;
            }
            channel.bind(localSocketAddr);

            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ);
            setSendBuffer(16 * 1024 * 1024);
            setRecvBuf(16 * 1024 * 1024);

            if (remoteSocketAddr == null || !this.addrRemote.equals(addrRemote) || this.portRemote != portRemote) {
                remoteSocketAddr = new InetSocketAddress(addrRemote, portRemote);
                this.addrRemote = addrRemote;
                this.portRemote = portRemote;
            }
            channel.connect(remoteSocketAddr);

            if (!channel.isConnected()) {
                connectionStatus = waitForConnected(timeout);
            }

            if (!connectionStatus) {
                channel.close();
            }
        } catch (IOException e) {
            /**
             * This exception can be catch, clean up channel and return connection status back. Outer layer logic will
             * do the right thing according to the return connectionStatus
             */
            logger.error("IOException in makeTCPSocketClient: \n", e);
            channel.close();
        } finally {
            return connectionStatus;
        }
    }

    int readSocket(ByteBuffer buf, int numBytes) throws IOException {
        buf.position(0);
        buf.limit(numBytes);
        int nRead = 0;
        while (buf.hasRemaining()) {
            int nRecv;
            nRecv = channel.read(buf);
            if (nRecv > 0) {
                nRead += nRecv;
            } else if (nRecv < 0) {
                return -1;
            } else {
                continue;
            }
        }
        return nRead;
    }

    int writeSocket(ByteBuffer buf, int numBytes) throws IOException {
        //Similar with flip this buffer. The limit is set to the numBytes and then the position is set to zero.
        // If the mark is defined then it is discarded.
        buf.position(0);
        buf.limit(numBytes);

        int nSent = 0;
        while (buf.hasRemaining()) {
            nSent += channel.write(buf);
        }
        return nSent;
    }

    Selector getSelector() {
        return selector;
    }

    boolean waitForConnected(int timeout) throws IOException {
        long currentTime = System.currentTimeMillis();
        long targetTime = currentTime + timeout * 1000;

        while (!channel.finishConnect()) {
            if (targetTime <= System.currentTimeMillis())
                break;
        }
        return channel.isConnected();
    }

    void closeSocket() throws IOException {
        channel.close();
    }
}
