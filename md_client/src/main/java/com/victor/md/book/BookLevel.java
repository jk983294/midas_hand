package com.victor.md.book;

import com.victor.md.util.UnsafeAccess;
import sun.misc.Unsafe;

public abstract class BookLevel implements Comparable<BookLevel> {
    private long baseAddress;

    private static final Unsafe UNSAFE = UnsafeAccess.getUnsafe();

    public void loadAddr(long baseAddress) {
        this.baseAddress = baseAddress;
    }

    public long baseAddr() {
        return baseAddress;
    }

    public long price() {
        return UNSAFE.getLong(baseAddress + 0);
    }

    public long shares() {
        return UNSAFE.getLong(baseAddress + 8);
    }

    public int orders() {
        return UNSAFE.getInt(baseAddress + 16);
    }

    public short exchange() {
        return UNSAFE.getShort(baseAddress + 20);
    }

    public long sequence() {
        return UNSAFE.getLong(baseAddress + 22);
    }

    public long updateTS() {
        return UNSAFE.getLong(baseAddress + 30);
    }

    public long timestamp() {
        return UNSAFE.getLong(baseAddress + 38);
    }

    public byte priceScaleCode() {
        return UNSAFE.getByte(baseAddress + 46);
    }

    public int lotSize() {
        return UNSAFE.getInt(baseAddress + 47);
    }

    @Override
    public abstract int compareTo(BookLevel rhs);

    public abstract int compareTo(long price);

    public static short sizeOfBookLevel() {
        return 51;
    }

    /**
     * Convert the time get from timstamp() to Epoch time.
     *
     * @param nanosecondsInADay [IN] Value get from timestamp(), may Epoch time or wall clock time.
     * @param base              [IN] Base nanoseconds of the day.
     * @return Epoch time according to nanosecondsInADay.
     */
    public long toEpochTime(long nanosecondsInADay, long base) {
        if (nanosecondsInADay > (86400 * 1000000000L)) {
            return nanosecondsInADay;
        } else {
            return (nanosecondsInADay + base);
        }
    }

    /**
     * Convert the time get from timstamp() to Epoch time.
     * Sugg
     *
     * @param nanosecondsInADay [IN] Value get from timestamp(), may Epoch time or wall clock time.
     * @return Epoch time according to nanosecondsInADay.
     */
    public long toEpochTime(long nanosecondsInADay) {
        if (nanosecondsInADay > (86400 * 1000000000L)) {
            return nanosecondsInADay;
        } else {
            return nanosecondsInADay + ((System.currentTimeMillis() / (86400 * 1000)) * 86400 * 1000000000L);
        }
    }
}
