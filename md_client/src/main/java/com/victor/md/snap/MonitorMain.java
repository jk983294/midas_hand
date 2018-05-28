package com.victor.md.snap;

import com.victor.md.exception.KeyNotExistException;

import java.io.IOException;

public class MonitorMain {
    public static void main(String[] args) throws KeyNotExistException, IOException, InterruptedException {
        new MonitorService().start(args);
    }
}
