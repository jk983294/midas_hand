package com.victor.utilities.visual;

import org.apache.commons.lang3.ArrayUtils;

public class VisualAssist {
	
	public static <T> void print(T... a) {
		System.out.println(ArrayUtils.toString(a));
	}
	
	public static <T> void print(String description, T... a) {
		System.out.println(description);
		print(a);
	}
	
	public static <T> void print(T a) {
		System.out.println(a);
	}
	
	public static <T> void print(String description, T a) {
		System.out.println(description);
		print(a);
	}
	
	public static <T> void print(T[][] a) {
		System.out.println(ArrayUtils.toString(a));
	}
	
	public static <T> void print(String description, T[][] a) {
		System.out.println(description);
		print(a);
	}
	
	
	/**
	 * for primitive type, generic not applicable for primitive
	 * @param a
	 */
	public static void print(double[] a) {
		System.out.println(ArrayUtils.toString(a));
	}
	
	public static void print(String description, double[] a) {
		System.out.println(description);
		print(a);
	}
	
	public static void print(int[] a) {
		System.out.println(ArrayUtils.toString(a));
	}
	
	public static void print(String description, int[] a) {
		System.out.println(description);
		print(a);
	}

    public static void print(short[] a) {
        System.out.println(ArrayUtils.toString(a));
    }

    public static void print(String description, short[] a) {
        System.out.println(description);
        print(a);
    }

    public static void print(byte[] a) {
        System.out.println(ArrayUtils.toString(a));
    }

    public static void print(String description, byte[] a) {
        System.out.println(description);
        print(a);
    }

    public static void print(long[] a) {
        System.out.println(ArrayUtils.toString(a));
    }

    public static void print(String description, long[] a) {
        System.out.println(description);
        print(a);
    }

    public static void print(float[] a) {
        System.out.println(ArrayUtils.toString(a));
    }

    public static void print(String description, float[] a) {
        System.out.println(description);
        print(a);
    }

    public static void print(char[] a) {
        System.out.println(ArrayUtils.toString(a));
    }

    public static void print(String description, char[] a) {
        System.out.println(description);
        print(a);
    }

    public static void print(boolean[] a) {
        System.out.println(ArrayUtils.toString(a));
    }

    public static void print(String description, boolean[] a) {
        System.out.println(description);
        print(a);
    }
}
