package com.victor.md.consumer;

public class MdDataType {

    public static final byte CTRL_CONNECT_TYPE = 'I';
    public static final byte CTRL_CONNECT_RESPONSE_TYPE = 'Z';
    public static final byte CTRL_DISCONNECT_TYPE = 'B';
    public static final byte CTRL_DISCONNECT_RESPONSE_TYPE = 'Y';
    public static final byte CTRL_SUBSCRIBE_TYPE = 'C';
    public static final byte CTRL_SUBSCRIBE_RESPONSE_TYPE = 'X';
    public static final byte CTRL_UNSUBSCRIBE_TYPE = 'D';
    public static final byte CTRL_UNSUBSCRIBE_RESPONSE_TYPE = 'V';

    public static final byte DATA_TRADING_ACTION_TYPE = 'E';
    public static final byte DATA_BOOK_CHANGED_TYPE = 'G';
    public static final byte DATA_BOOK_REFRESHED_TYPE = 'H';
    public static final byte DATA_HEARTBEAT_TYPE = 'J';

    public static final byte ADMIN_BOOK_CLEAR_TYPE = 'a';

    public static final byte SHM_TYPE_EVENT_QUEUE = 'E';
    public static final byte SHM_TYPE_BOOK_CACHE = 'B';

    public static final long DATA_BID_PRICE_BLANK = Long.MIN_VALUE;
    public static final long DATA_ASK_PRICE_BLANK = Long.MAX_VALUE;
    public static final byte DATA_PRICE_SCALE_FACTOR_DEFAULT = 4;
    public static final int DATA_LOTSIZE_DEFAULT = 100;

    public static final byte SUBSCRIBE_STATUS_OK = 0;
    public static final byte SUBSCRIBE_STATUS_BAD_SYMBOL = 2;
    public static final byte SUBSCRIBE_STATUS_BAD_EXCHANGE = 3;

    public static final int BOOK_SIDE_BID = 0;
    public static final int BOOK_SIDE_ASK = 1;
    public static final int BOOK_SIDE_BOTH = 2;

    public static final int FLAG_SEND_BOOK_REFRESHED = 0x00000001;
    public static final int FLAG_SEND_BOOK_CHANGED = 0x00000002;
    public static final int FLAG_SEND_TRADING_ACTION = 0x00000004;
    public static final int FLAG_SEND_IMBALANCE = 0x00000008;
    public static final int FLAG_SEND_TRADE = 0x00000010;
    public static final int FLAG_SEND_QUOTE = 0x00000020;
    public static final int FLAG_SEND_DATA_HEARTBEAT = 0x00000040;

    public static final short ExchangeNone = 0x0000;
    public static final short ExchangeCFFEX = 0x0001;  // 中国金融交易所
    public static final short ExchangeCZCE = 0x0002;   // 郑州商品交易所
    public static final short ExchangeDCE = 0x0003;    // 大连商品交易所
    public static final short ExchangeINE = 0x0004;    // 上海国际能源交易中心股份有限公司
    public static final short ExchangeSHFE = 0x0005;   // 上海期货交易所
    public static final short ExchangeCtpAll = 0x0006;
}
