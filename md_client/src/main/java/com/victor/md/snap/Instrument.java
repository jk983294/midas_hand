package com.victor.md.snap;


import com.victor.md.book.BookLevel;
import com.victor.md.book.MdBook;
import com.victor.md.consumer.ConsumerStatus;
import com.victor.md.consumer.MdDataType;
import com.victor.md.consumer.MdSubscription;
import com.victor.md.util.MdUtils;
import org.apache.log4j.Logger;

import java.text.DecimalFormat;

public class Instrument {
    private static final Logger logger = Logger.getLogger(Instrument.class);

    private String symbol;
    private short exchange;
    private boolean subscribed = false;
    private MdSubscription sub;
    private MdBook book;
    private long[] bidPrices;
    private long[] bidSizes;
    private long[] askPrices;
    private long[] askSizes;
    private String identityMf;

    public Instrument(String symbol, short exchange) {
        this.symbol = symbol;
        this.exchange = exchange;
        StringBuilder sb = new StringBuilder();
        sb.append("id ").append(symbol).append(" ,e ").append(exchange).append(" ,");
        identityMf = sb.toString();
    }

    void snap() {
        if (ConsumerStatus.OK.equals(sub.snap(MdDataType.BOOK_SIDE_BOTH, book))) print_book();
    }

    void print_book() {
        StringBuilder sb = new StringBuilder();
        sb.append(symbol).append(" level1 bid: ").append(book.bidBookLevels[0].price()).append(" ")
                .append(book.bidBookLevels[0].shares()).append(" ask: ").append(book.askBookLevels[0].price())
                .append(" ").append(book.askBookLevels[0].shares());
        logger.info(sb.toString());
    }

    String getMsg() {
        StringBuilder sb = new StringBuilder();
        sb.append("id ").append(symbol).append(" ,e ").append(exchange).append(" ,bbp ")
                .append(book.bidBookLevels[0].price()).append(" ,bbs ")
                .append(book.bidBookLevels[0].shares()).append(" ,bap ")
                .append(book.askBookLevels[0].price()).append(" ,bas ")
                .append(book.askBookLevels[0].shares()).append(" ,;");
        return sb.toString();
    }

    private static DecimalFormat df6 = new DecimalFormat(".######");

    public String getDeltaMf() {
        if (askPrices == null) {
            initInternalBook();
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < askPrices.length; i++) {
            BookLevel ask = book.getAskBookLevels()[i];
            long p = (ask.price() != Long.MIN_VALUE && ask.price() != Long.MAX_VALUE) ? ask.price() : 0;
            if (p != askPrices[i]) {
                double scale = ask.priceScaleCode() > 0 ? Math.pow(10, ask.priceScaleCode()) : 1.0;
                double price = (double) p / scale;
                sb.append("bap").append(i == 0 ? "" : i + 1).append(" ");
                if (price == Math.floor(price)) {
                    int pInt = Double.valueOf(price).intValue();
                    sb.append(pInt).append(" ,");
                } else {
                    sb.append(price).append(" ,");
                }
                askPrices[i] = p;
            }

            long q = (ask.shares() != Long.MIN_VALUE && ask.shares() != Long.MAX_VALUE) ? ask.shares() : 0;
            if (q != askSizes[i]) {
                sb.append("bas").append(i == 0 ? "" : i + 1).append(" ").append(q).append(" ,");
                askSizes[i] = p;
            }
        }

        for (int i = 0; i < bidPrices.length; i++) {
            BookLevel bid = book.getBidBookLevels()[i];
            long p = (bid.price() != Long.MIN_VALUE && bid.price() != Long.MAX_VALUE) ? bid.price() : 0;
            if (p != bidPrices[i]) {
                double scale = bid.priceScaleCode() > 0 ? Math.pow(10, bid.priceScaleCode()) : 1.0;
                double price = (double) p / scale;
                sb.append("bbp").append(i == 0 ? "" : i + 1).append(" ");
                if (price == Math.floor(price)) {
                    int pInt = Double.valueOf(price).intValue();
                    sb.append(pInt).append(" ,");
                } else {
                    sb.append(price).append(" ,");
                }
                bidPrices[i] = p;
            }

            long q = (bid.shares() != Long.MIN_VALUE && bid.shares() != Long.MAX_VALUE) ? bid.shares() : 0;
            if (q != bidSizes[i]) {
                sb.append("bbs").append(i == 0 ? "" : i + 1).append(" ").append(q).append(" ,");
                bidSizes[i] = p;
            }
        }

        if (sb.length() > 0) {
            sb.append("rcvt ").append(df6.format(MdUtils.rcvt())).append(" ,;");
            return identityMf + sb.toString();
        } else {
            return "";
        }
    }

    private void initInternalBook() {
        int askLevel = book.getNumAskLevels();
        askPrices = new long[askLevel];
        askSizes = new long[askLevel];
        int bidLevel = book.getNumBidLevels();
        bidPrices = new long[bidLevel];
        bidSizes = new long[bidLevel];
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
