package com.victor.md.consumer;

public enum FeedStatus {
    CONNECT_STATUS_OK(0),
    CONNECT_STATUS_ALREADY_CONNECTED('D'),
    CONNECT_STATUS_VERSION_MISMATCH('V'),
    CONNECT_STATUS_UNREGISTERED_CONNECTION('U'),
    CONNECT_STATUS_SHARED_MEMORY_FAILURE('S'),
    CONNECT_STATUS_INVALID_ID('I');

    int value;

    FeedStatus(int value) {
        this.value = value;
    }

    public static FeedStatus fromValue(int value) {
        switch (value) {
            case 0:
                return FeedStatus.CONNECT_STATUS_OK;
            case 'D':
                return FeedStatus.CONNECT_STATUS_ALREADY_CONNECTED;
            case 'V':
                return FeedStatus.CONNECT_STATUS_VERSION_MISMATCH;
            case 'U':
                return FeedStatus.CONNECT_STATUS_UNREGISTERED_CONNECTION;
            case 'S':
                return FeedStatus.CONNECT_STATUS_SHARED_MEMORY_FAILURE;
            case 'I':
                return FeedStatus.CONNECT_STATUS_INVALID_ID;
            default:
                throw new IllegalArgumentException();
        }
    }
}
