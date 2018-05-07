package com.victor.md.consumer;

public enum ConsumerStatus {
    OK(0),
    NOT_CONNECTED(1),
    NOT_AUTHORIZED(2),
    BAD_SYMBOL(3),
    ERROR(4),
    SLOW_CONSUMER(5),
    SHM_ERROR(6),
    TIMED_OUT(7),
    BAD_BOOK(8),
    NOT_SUBSCRIBED(9),
    NO_DATA(10),
    ALREADY_SUBSCRIBED(11);

    int value;

    ConsumerStatus(int value) {
        this.value = value;
    }

    public static ConsumerStatus fromValue(int value) {
        switch (value) {
            case 0:
                return ConsumerStatus.OK;
            case 1:
                return ConsumerStatus.NOT_CONNECTED;
            case 2:
                return ConsumerStatus.NOT_AUTHORIZED;
            case 3:
                return ConsumerStatus.BAD_SYMBOL;
            case 4:
                return ConsumerStatus.ERROR;
            case 5:
                return ConsumerStatus.SLOW_CONSUMER;
            case 6:
                return ConsumerStatus.SHM_ERROR;
            case 7:
                return ConsumerStatus.TIMED_OUT;
            case 8:
                return ConsumerStatus.BAD_BOOK;
            case 9:
                return ConsumerStatus.NOT_SUBSCRIBED;
            case 10:
                return ConsumerStatus.NO_DATA;
            case 11:
                return ConsumerStatus.ALREADY_SUBSCRIBED;
            default:
                throw new IllegalArgumentException();
        }
    }
}
