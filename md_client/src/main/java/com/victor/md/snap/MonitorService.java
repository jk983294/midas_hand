package com.victor.md.snap;

import com.victor.md.book.MdBook;
import com.victor.md.config.MdConfig;
import com.victor.md.consumer.*;
import com.victor.md.exception.KeyNotExistException;
import com.victor.md.msg.BookChanged;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MonitorService {

    private static final Logger logger = Logger.getLogger(MonitorMain.class);
    private String fileConfig;
    private String configKey = "publisher";
    private ConcurrentHashMap<String, Instrument> instruments;
    private MdConsumer consumer;
    private DataThread dataThread;
    private AtomicLong bookUpdatedCount = new AtomicLong(0);
    private AtomicLong bookRefreshedCount = new AtomicLong(0);

    void start(String[] args) throws KeyNotExistException, IOException, InterruptedException {
        this.instruments = new ConcurrentHashMap<>();

        for (int count = 0; count < args.length; count++) {
            if (!args[count].startsWith("-")) {
                //even numbered arguments should begin with -
                logger.error("command line arguments syntax error. ");
                help();
                return;
            }
            //if args[count]==any flag then args[count+1] contains the value for that flag
            if (args[count].equals("-c"))
                fileConfig = args[++count];
            if (args[count].equals("-?"))
                help();
        }

        if (fileConfig == null) {
            help();
        }

        ConsumerCallBacks callbacks = new ConsumerCallBacks();
        // Set callbacks
        callbacks.setOnSubscribe((String symbol, short exchange, ConsumerStatus status) -> handleSubscribeResponse(symbol, exchange, status));
        callbacks.setBookChanged((String symbol, short exchange, BookChanged bc) -> handleBookChanged(symbol, exchange, bc));
        callbacks.setBookRefreshed((String symbol, short exchange) -> handleBookRefreshed(symbol, exchange));
        // Initialize MDConsumer
        consumer = new MdConsumer(fileConfig, configKey, callbacks);

        // ctrl + c to shutdown the process
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                shutdown();
            }
        });

        MdConfig mdConfig = consumer.mdConfig;
        String symbol = mdConfig.queryString("subscribe", "symbol");
        short exchange = mdConfig.queryShort("subscribe", "exchange_code");
        int adminPort = mdConfig.queryInt("consumer", "admin_port");
        int dataPort = mdConfig.queryInt("consumer", "data_port");

        new AdminThread(this, adminPort).start();
        dataThread = new DataThread(dataPort);
        dataThread.start();
        consumer.start();

        // Add symbol both from symbol and symbol file if have...
        if (symbol != null && !symbol.isEmpty()) {
            subscribe(symbol, exchange);
        }

        for (; ; ) {
            consumer.dataPoll();
        }
    }

    public synchronized boolean subscribe(String symbol, short exchange) {
        if (instruments.containsKey(symbol)) {
            logger.warn("already subscribed for " + symbol);
            return false;
        }

        Instrument instrument = new Instrument(symbol, exchange);
        instrument.setBook(new MdBook());
        if (consumer.allocBook(exchange, instrument.getBook()) == ConsumerStatus.OK) {
            instrument.setSub(new MdSubscription());
            instruments.put(symbol, instrument);
            ConsumerStatus status = consumer.subscribe(instrument.getSub(), symbol, exchange, true);
            if (status == ConsumerStatus.OK) {
                logger.info("Sent subscription request for " + symbol);
            } else {
                instruments.remove(symbol);
                logger.error("Failed to send subscription request for " + symbol + " - " + status);
                return false;
            }
        } else {
            logger.error("Failed to initialize book for: " + symbol);
            return false;
        }
        instrument.setSubscribed(true);
        return true;
    }

    private void shutdown() {
        consumer.stop();
    }

    void handleSubscribeResponse(String symbol, short exchange, ConsumerStatus status) {
        if (status != ConsumerStatus.OK) {
            logger.error(symbol + " subscribe failed, status: " + status);
        } else {
            logger.info(symbol + " subscribe success, status: " + status);
        }
    }

    void handleBookChanged(String symbol, short exchange, BookChanged bc) {
        bookUpdatedCount.getAndIncrement();
        if (!instruments.containsKey(symbol)) {
            return;
        }

        Instrument ms = instruments.get(symbol);
        ConsumerStatus status = consumer.snap(ms.getSub(), MdDataType.BOOK_SIDE_BOTH, ms.getBook());
        if (status != ConsumerStatus.OK) {
            logger.error("Snap failed due to: " + status);
        } else {
            String mf = ms.getDeltaMf();
            if (mf.length() > 0) {
                dataThread.add2publishQueue(mf);
            }
        }
    }

    void handleBookRefreshed(String symbol, short exchange) {
        bookRefreshedCount.getAndIncrement();
        if (!instruments.containsKey(symbol)) {
            return;
        }

        Instrument instrument = instruments.get(symbol);
        ConsumerStatus status = consumer.snap(instrument.getSub(), MdDataType.BOOK_SIDE_BOTH, instrument.getBook());
        if (status != ConsumerStatus.OK) {
            logger.error("Snap failed due to: " + status);
        } else {
            String mf = instrument.getDeltaMf();
            if (mf.length() > 0) {
                dataThread.add2publishQueue(mf);
            }
        }
    }

    public String meters() {
        StringBuilder sb = new StringBuilder();
        sb.append("updated: ").append(bookUpdatedCount.get()).append("\t\trefreshed: ").append(bookRefreshedCount.get())
                .append("\n").append("subscribed: ");
        for (String s : instruments.keySet()) {
            sb.append(s).append(", ");
        }
        return sb.toString();
    }

    static void help() {
        logger.error("-c <config>");
        System.exit(-1);
    }
}
