package com.victor.md.snap;


import com.victor.md.book.MdBook;
import com.victor.md.consumer.ConsumerStatus;
import com.victor.md.consumer.MdDataType;
import com.victor.md.consumer.MdSubscription;
import org.apache.log4j.Logger;

public class MySymbol {
    private static final Logger logger = Logger.getLogger(MySymbol.class);

    private String symbol;
    private short exchange;
    private boolean subscribed = false;
    private MdSubscription sub;
    private MdBook book;

    public MySymbol(String symbol, short exchange) {
        this.symbol = symbol;
        this.exchange = exchange;
    }

    void snap() {
        if (ConsumerStatus.OK.equals(sub.snap(MdDataType.BOOK_SIDE_BOTH, book))) print_book();
    }

    void print_book() {
        StringBuilder sb = new StringBuilder();
        sb.append(symbol).append(" level1 bid: ").append(book.bidBookLevels()[0].price()).append(" ")
                .append(book.bidBookLevels()[0].shares()).append(" ask: ").append(book.askBookLevels()[0].price())
                .append(" ").append(book.askBookLevels()[0].shares());
        logger.info(sb.toString());
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public short getExchange() {
        return exchange;
    }

    public void setExchange(short exchange) {
        this.exchange = exchange;
    }

    public boolean isSubscribed() {
        return subscribed;
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }

    public MdSubscription getSub() {
        return sub;
    }

    public void setSub(MdSubscription sub) {
        this.sub = sub;
    }

    public void setBook(MdBook book) {
        this.book = book;
    }

    public MdBook getBook() {
        return book;
    }

}
