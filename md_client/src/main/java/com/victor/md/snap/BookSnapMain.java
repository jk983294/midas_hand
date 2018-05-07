package com.victor.md.snap;

import com.victor.md.book.MdBook;
import com.victor.md.config.MdConfig;
import com.victor.md.consumer.*;
import com.victor.md.exception.KeyNotExistException;
import com.victor.md.msg.BookChanged;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

public class BookSnapMain {

    private static final Logger logger = Logger.getLogger(BookSnapMain.class);
    private static String fileConfig;
    private final static String configKey = "publisher";
    private int numberOfFailedSubscription = 0;
    private HashMap<String, MySymbol> allSymbols;
    private MdConsumer consumer;

    public static void main(String[] args) throws KeyNotExistException, IOException, InterruptedException {
        new BookSnapMain().start(args);
    }

    void start(String[] args) throws KeyNotExistException, IOException, InterruptedException {
        this.allSymbols = new HashMap<>();

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
        // Add symbol both from symbol and symbol file if have...
        if (symbol != null && !symbol.isEmpty()) {
            allSymbols.put(symbol, new MySymbol(symbol, exchange));
        }

        consumer.start();

        int numberOfSubscription = 0;

        for (Entry<String, MySymbol> entry : allSymbols.entrySet()) {
            MySymbol mySymbol = entry.getValue();
            mySymbol.setBook(new MdBook());
            if (consumer.allocBook(exchange, mySymbol.getBook()) == ConsumerStatus.OK) {
                mySymbol.setSub(new MdSubscription());
                ConsumerStatus status = consumer.subscribe(mySymbol.getSub(), mySymbol.getSymbol(), mySymbol.getExchange(), true);
                if (status == ConsumerStatus.OK) {
                    ++numberOfSubscription;
                    mySymbol.setSubscribed(true);
                    logger.info("Sent subscription request for " + mySymbol.getSymbol());
                } else {
                    logger.error("Failed to send subscription request for " + mySymbol.getSymbol() + " - " + status);
                }
            } else {
                logger.error("Failed to initialize book for: " + mySymbol.getSymbol());
            }
        }

        while (numberOfSubscription > 0 && numberOfSubscription > numberOfFailedSubscription) {
            consumer.dataPoll();
            Thread.sleep(500);
        }
        shutdown();
    }

    private void shutdown() {
        consumer.stop();
    }

    void handleSubscribeResponse(String symbol, short exchange, ConsumerStatus status) {
        if (status != ConsumerStatus.OK) {
            ++numberOfFailedSubscription;
            logger.error(symbol + " subscribe failed, status: " + status);
        } else {
            logger.info(symbol + " subscribe success, status: " + status);
        }
    }

    void handleBookChanged(String symbol, short exchange, BookChanged bc) {
        MySymbol ms = allSymbols.get(symbol);
        ConsumerStatus status = consumer.snap(ms.getSub(), MdDataType.BOOK_SIDE_BOTH, ms.getBook());
        if (status != ConsumerStatus.OK) {
            logger.error("Snap failed due to: " + status);
        } else {
            ms.print_book();
        }
    }

    void handleBookRefreshed(String symbol, short exchange) {
        MySymbol ms = allSymbols.get(symbol);
        ConsumerStatus status = consumer.snap(ms.getSub(), MdDataType.BOOK_SIDE_BOTH, ms.getBook());
        if (status != ConsumerStatus.OK) {
            logger.error("Snap failed due to: " + status);
        } else {
            ms.print_book();
        }
    }

    static void help() {
        logger.error("-c <config>");
        System.exit(-1);
    }
}
