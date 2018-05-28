package com.victor.md.snap;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class AdminThread extends Thread {

    private static final Logger logger = Logger.getLogger(AdminThread.class);
    private MonitorService service;
    private Selector selector;
    private int adminPort;

    public AdminThread(MonitorService service, int adminPort) throws IOException {
        this.service = service;
        this.adminPort = adminPort;

        ServerSocketChannel serverChannel;
        serverChannel = ServerSocketChannel.open();
        ServerSocket ss = serverChannel.socket();
        InetSocketAddress address = new InetSocketAddress(adminPort);
        ss.bind(address);
        serverChannel.configureBlocking(false);
        selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    @Override
    public void run() {
        logger.info("admin thread start at port " + adminPort + " ...");
        while (true) {
            try {
                selector.select();
            } catch (IOException ex) {
                logger.error("admin thread select exception: " + ex.getMessage());
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
                        logger.info("Accepted admin connection from " + client);
                        client.configureBlocking(false);
                        SelectionKey clientKey = client.register(
                                selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        clientKey.attach(buffer);
                    } else if (key.isReadable()) {
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer output = (ByteBuffer) key.attachment();
                        int num = client.read(output);
                        if (num > 0) {
                            String result = handleRequest(output);
                            client.write(ByteBuffer.wrap(result.getBytes()));
                        }
                    }
                } catch (IOException ex) {
                    key.cancel();
                    try {
                        SocketChannel client = (SocketChannel) key.channel();
                        logger.info("trying to close channel: " + client);
                        key.channel().close();
                    } catch (IOException cex) {
                        logger.error("channel close error: " + cex.getMessage());
                    }
                }
            }
        }
    }

    private String handleRequest(ByteBuffer buffer) {
        String content = new String(buffer.array());

        JSONObject json = new JSONObject(content);
        String command = json.getString("command");
        JSONArray jsonArray = json.getJSONArray("arguments");
        List<String> arguments = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            arguments.add((String) jsonArray.get(i));
        }

        String response;
        try {
            response = dispatchCommand(command, arguments);
        } catch (Exception e) {
            response = "command execution failed. " + e.getMessage();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{").append("\"userId\" : ").append("\"").append(json.getString("userId")).
                append("\", ").append("\"requestId\" : ").append("\"").append(json.getString("requestId")).
                append("\", ").append("\"response\" : ").append("\"").
                append(response.replace("\"", "'")).append("\"").append("}");
        return sb.toString();
    }

    private String dispatchCommand(String cmd, List<String> arguments) {
        StringBuilder sb = new StringBuilder();
        if (cmd.equalsIgnoreCase("meters")) {
            return service.meters();
        } else if (cmd.equalsIgnoreCase("help")) {
            sb.append("meters\t\t\t\t\tdisplay server meters\n")
                    .append("help\t\t\t\t\tdisplay available commands\n")
                    .append("subscribe <symbol> <exchange>\t\tsubscribe given symbol\n")
                    .append("subscribe.file <path>\t\t\tsubscribe given symbols in given file\n");
            return sb.toString();
        } else if (cmd.equalsIgnoreCase("subscribe")) {
            if (arguments.size() == 2) {
                subscribe(arguments.get(0), arguments.get(1), sb);
                return sb.toString();
            } else {
                return "subscribe <symbol> <exchange>";
            }
        } else if (cmd.equalsIgnoreCase("subscribe.file")) {
            if (arguments.size() == 1) {
                subscribeFile(arguments.get(0), sb);
                return sb.toString();
            } else {
                return "subscribe.file <file>";
            }
        }
        return "no such command found!";
    }

    private boolean subscribeFile(String path, StringBuilder sb) {
        boolean success = true;
        try {
            List<String> symbols = Files.readAllLines(new File(path).toPath());
            for (String str : symbols) {
                String line = str.trim().replace(".", " ");
                if (line.length() > 0) {
                    String[] lets = line.split("");
                    if (lets.length == 2) {
                        if (!subscribe(lets[0], lets[1], sb)) {
                            success = false;
                        }
                        sb.append("\n");
                    } else {
                        sb.append("invalid line: ").append(line).append("\n");
                    }
                }
            }
        } catch (IOException e) {
            sb.append(e.toString());
            return false;
        }
        return success;
    }

    private boolean subscribe(String symbol, String exchangeStr, StringBuilder sb) {
        short exchange = Short.valueOf(exchangeStr);
        if (service.subscribe(symbol, exchange)) {
            sb.append("subscribe for ").append(symbol).append(" success.");
            return true;
        } else {
            sb.append("subscribe for ").append(symbol).append(" failed.");
            return false;
        }
    }
}
