package com.victor.md.util;

import java.util.Date;

public class MdUtils {

    public static double rcvt() {
        Date date = new Date();
        return date.getTime() * 0.001;
    }
}
