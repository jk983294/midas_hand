package com.victor.md.util;

import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class PidAccess {
    private static final Logger logger = Logger.getLogger(PidAccess.class);

    private static final int PID = doGetPID();
    private static final String PATH = "/proc/self/stat";

    public static int getPID() {
        return PID;
    }

    private static int doGetPID() {
        try {
            FileInputStream fileIn = new FileInputStream(PATH);
            Scanner scan = new Scanner(fileIn);
            if (scan.hasNextInt()) {
                return scan.nextInt();
            }
            logger.error("Failed to get PID from /proc/self/stat, return -1 instead");
            return -1;
        } catch (FileNotFoundException e) {

            logger.error("Failed to open /proc/self/stat", e);
            throw new Error("Failed to open /proc/self/stat", e);
        }
    }
}
