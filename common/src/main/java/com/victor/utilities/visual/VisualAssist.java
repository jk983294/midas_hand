package com.victor.utilities.visual;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

public class VisualAssist {

    private static final Logger logger = Logger.getLogger(VisualAssist.class);
	
	public static <T> void print(T... a) {
        logger.info(ArrayUtils.toString(a));
	}
	
	public static <T> void print(String description, T... a) {
		logger.info(description);
		print(a);
	}
	
	public static <T> void print(T a) {
		logger.info(a);
	}
	
	public static <T> void print(String description, T a) {
		logger.info(description);
		print(a);
	}
	
	public static <T> void print(T[][] a) {
		logger.info(ArrayUtils.toString(a));
	}
	
	public static <T> void print(String description, T[][] a) {
		logger.info(description);
		print(a);
	}
	
	
	/**
	 * for primitive type, generic not applicable for primitive
	 * @param a
	 */
	public static void print(double[] a) {
		logger.info(ArrayUtils.toString(a));
	}
	
	public static void print(String description, double[] a) {
		logger.info(description);
		print(a);
	}
	
	public static void print(int[] a) {
		logger.info(ArrayUtils.toString(a));
	}
	
	public static void print(String description, int[] a) {
		logger.info(description);
		print(a);
	}

    public static void print(short[] a) {
        logger.info(ArrayUtils.toString(a));
    }

    public static void print(String description, short[] a) {
        logger.info(description);
        print(a);
    }

    public static void print(byte[] a) {
        logger.info(ArrayUtils.toString(a));
    }

    public static void print(String description, byte[] a) {
        logger.info(description);
        print(a);
    }

    public static void print(long[] a) {
        logger.info(ArrayUtils.toString(a));
    }

    public static void print(String description, long[] a) {
        logger.info(description);
        print(a);
    }

    public static void print(float[] a) {
        logger.info(ArrayUtils.toString(a));
    }

    public static void print(String description, float[] a) {
        logger.info(description);
        print(a);
    }

    public static void print(char[] a) {
        logger.info(ArrayUtils.toString(a));
    }

    public static void print(String description, char[] a) {
        logger.info(description);
        print(a);
    }

    public static void print(boolean[] a) {
        logger.info(ArrayUtils.toString(a));
    }

    public static void print(String description, boolean[] a) {
        logger.info(description);
        print(a);
    }
}
