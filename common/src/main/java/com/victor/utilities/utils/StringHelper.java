package com.victor.utilities.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * helper for String
 */
public class StringHelper {

    public static List<String> toList(Iterator<String> iter){
        List<String> results = new ArrayList<>();
        while (iter.hasNext()){
            String str = (String) iter.next();
            results.add(str);
        }
        return results;
    }
}
