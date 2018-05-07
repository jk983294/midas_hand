package com.victor.md.util;

import org.apache.log4j.Logger;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public final class UnsafeAccess {
    /**
     * Must be initialised before {@link #UNSAFE}, because used in {@link #doGetUnsafe()}.
     */
    private static final Logger logger = Logger.getLogger(UnsafeAccess.class);

    private static final Unsafe UNSAFE = doGetUnsafe();

    private UnsafeAccess() {
    }

    /**
     * Provides access to sun.misc.Unsafe- encapsulating the way to get at it.
     * <p>
     * When use can take a local copy if want to reduce work for JIT inliner:
     * <br><code>private static final Unsafe UNSAFE = D1Util.getUnsafe();</code>
     * <br><i>I don't normally uppercase static object names, but its worth using <code>UNSAFE</code> here to make use stand out when you read code that uses Unsafe.</i>
     * </p>
     *
     * @return
     */
    public static Unsafe getUnsafe() {
        return UNSAFE;
    }

    /**
     * Get in a way suitable for use in non JDK code.
     * <p>
     * eg see: http://grepcode.com/file/repo1.maven.org/maven2/org.elasticsearch/elasticsearch/0.20.6/jsr166e/StampedLock.java#StampedLock.0U
     * </p>
     */
    private static Unsafe doGetUnsafe() {
        // Can't call Unsafe.getUnsafe() as done in JDK, because throws a SecurityException if not called from primary classloader (from a class in bootclasspath)
        // This is Java convention workaround to get at sun.misc.Unsafe ...
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            return (Unsafe) f.get(null);
        } catch (Exception e) {
            logger.error("Failed to access sun.misc.Unsafe", e); // Ensure logged.
            throw new Error("Failed to get sun.misc.Unsafe using reflection", e);
        }
    }

    /**
     * Helper for calling to get an instance filed offset inline to initialise a <code>private static final</code>
     *
     * @param c     Class with instance field.
     * @param field Filed name - must be correct - else will throw an Error
     * @return Instance field offset from
     * @throws Error If unsuccessful. Except in rare cases ONLY call this when it WILL work - typically to init a private static final inline.
     */
    public static long objectFieldOffset(Class c, String field) {
        try {
            return UnsafeAccess.UNSAFE.objectFieldOffset(c.getDeclaredField(field));
        } catch (Exception e) {
            String msg = "Error getting field offset of: " + c.getName() + "#" + field;
            logger.error(msg, e); // Ensure logged.
            throw new Error(msg, e); // This is a design time error - this util is only for when you KNOW the filed exists.
        }
    }
}

