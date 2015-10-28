package com.victor.utilities.lib.guava.collection;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;


/**
 * BiMap
 */
public class BimapUsage {

    public static void main(String[] args) {
        BiMap<Integer, String> empIDNameMap = HashBiMap.create();

        empIDNameMap.put(new Integer(101), "Mahesh");
        empIDNameMap.put(new Integer(102), "Sohan");
        empIDNameMap.put(new Integer(103), "Ramesh");

        //Emp Id of Employee "Mahesh"
        System.out.println(empIDNameMap.inverse().get("Mahesh"));
    }
}
