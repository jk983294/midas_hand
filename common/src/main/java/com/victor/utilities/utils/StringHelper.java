package com.victor.utilities.utils;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * helper for String
 */
public class StringHelper {

    public static List<String> toList(Iterator<String> itr){
        List<String> results = new ArrayList<>();
        while (itr.hasNext()){
            String str = itr.next();
            results.add(str);
        }
        return results;
    }

    public static List<String> split(String str, String delimiters){
        return toList(Splitter.on(CharMatcher.anyOf(delimiters)).trimResults().omitEmptyStrings().split(str).iterator());
    }

    public static String toString(List list){
        StringBuilder sb = new StringBuilder();
        for(Object object : list){
            sb.append(object.toString()).append("\n");
        }
        return sb.toString();
    }
}
