package com.victor.md.snap;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DataThread extends Thread {

    private static final Logger logger = Logger.getLogger(DataThread.class);
    private Selector selector;
    private int dataPort;
    private ConcurrentHashMap<String, SocketChannel> channels = new ConcurrentHashMap<>();


    public DataThread(int dataPort) throws IOException {
        this.dataPort = dataPort;

        ServerSocketChannel serverChannel;
        serverChannel = ServerSocketChannel.open();
        ServerSocket ss = serverChannel.socket();
        InetSocketAddress address = new InetSocketAddress(dataPort);
        ss.bind(address);
        serverChannel.configureBlocking(false);
        selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    @Override
    public void run() {
        logger.info("data thread start at port " + dataPort + " ...");
        while (true) {
            try {
                selector.select();
            } catch (IOException ex) {
                logger.error("data thread select exception: " + ex.getMessage());
            }

            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                try {
                    if (key.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept();
                        logger.info("Accepted data connection from " + client);
                        client.configureBlocking(false);
                        SelectionKey clientKey = client.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        clientKey.attach(buffer);
                        channels.put(client.toString(), client);
                    } else if (key.isReadable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer output = (ByteBuffer) key.attachment();
                        client.read(output);
                    } else if (key.isWritable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer output = (ByteBuffer) key.attachment();
                        output.flip();
                        client.write(output);
                        output.compact();
                    }
                } catch (IOException ex) {
                    key.cancel();
                    try {

                        if (key.channel() instanceof SocketChannel) {
                            SocketChannel channel = (SocketChannel) key.channel();
                            if (channels.containsKey(channel.toString())) {
                                logger.info("remove channel " + channel.toString() + " " + ex.getMessage());
                                channels.remove(channel.toString());
                            }
                        }

                        key.channel().close();
                    } catch (IOException cex) {
                        logger.error("channel close error: " + cex.getMessage());
                    }
                }
            }
        }
    }

    public void sendMsg(String msg) {
        if (msg != null && msg.length() > 0) {
            ByteBuffer buffer = buildMsg(msg);
            for (SocketChannel channel : channels.values()) {
                try {
                    logger.info("send data to " + channel + " with content: " + msg);
                    channel.write(buffer);
                } catch (IOException e) {
                    logger.error("channel write error: " + e.getMessage());
                }
            }
        }
    }

    /**
     * write midas header into buffer
     * 0 - 1    msg length (total exclude length field)
     * 2 - 5    time second
     * 6 - 9    time usecond
     * 10       255
     * 11       group
     * 12 - 15  msg sequence number
     */
    private static final int MidasHeaderLength = 16;
    private static final byte Magic = (byte) 0xFF;
    private static final byte Group = (byte) 1;

    private ByteBuffer buildMsg(String msg) {
        ByteBuffer buf = ByteBuffer.allocate(MidasHeaderLength + msg.length());
        short len = (short) (MidasHeaderLength + msg.length() - 2);
        Timestamp d = new Timestamp(System.currentTimeMillis());
        long mSecond = d.getTime();
        int second = (int) (mSecond / 1000);
        int uSecond = (int) ((second % 1000) * 1000 + d.getNanos() / 1e3);
        buf.putShort(len).putInt(second).putInt(uSecond).put(Magic).put(Group).putInt(0).put(msg.getBytes());
        return buf;
    }
}
