package com.victor.md.msg;

public class Heartbeat {
    private long sessionId = 0;
    private byte streamId = 0;

    /**
     * different with C++ MDConsumer, Java do not support type cast in a piece of memory.
     * Timestamps class is designed for the data read from shared memory, producer only set producerTransmit,
     * so we only keep producerTransmit in Heartbeat class for client query
     */
    private long producerTransmit = 0; // only producerTransmit is set

    public void loadHeartbeat(long session, byte streamId, long producerTransmit) {
        this.sessionId = session;
        this.streamId = streamId;
        this.producerTransmit = producerTransmit;
    }

    public long sessionId() {
        return sessionId;
    }

    public byte streamId() {
        return streamId;
    }

    public long producerTransmit() {
        return producerTransmit;
    }
}
