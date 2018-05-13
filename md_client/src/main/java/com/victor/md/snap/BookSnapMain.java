package com.victor.md.snap;

import com.victor.md.exception.KeyNotExistException;

import java.io.IOException;

public class BookSnapMain {
    public static void main(String[] args) throws KeyNotExistException, IOException, InterruptedException {
        new SnapService().start(args);
    }
}
