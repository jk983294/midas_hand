package com.victor.utilities.utils;

import org.apache.log4j.Logger;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * used to do gc manually
 */
public class PerformanceUtil {

    private static final Logger logger = Logger.getLogger(PerformanceUtil.class);

    /**
     * manually log performance before and after GC run
     */
    public static void manuallyGC(List<Object> objects){
        logPerformance();
        releaseResource(objects);
        forceGc();
        logPerformance();
    }

    public static void manuallyGC(Object object){
        logPerformance();
        releaseResource(object);
        forceGc();
        logPerformance();
    }

    /**
     * run GC manually
     */
    private static void forceGc(){
        System.gc();
        System.runFinalization();
    }

    private static void logPerformance(){
        long maxMemory = Runtime.getRuntime().maxMemory();
        logger.info(String.format("available processors %d cores, free memory %f MB, max memory %f MB, total memory %f MB",
                Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().freeMemory() / 1024.0 / 1024.0,
                (maxMemory == Long.MAX_VALUE ? 0.0 : maxMemory / 1024.0 / 1024.0 ),
                Runtime.getRuntime().totalMemory() / 1024.0 / 1024.0));
    }

    /**
     * release collection space consume
     * @param objects
     */
    private static void releaseResource(List<Object> objects){
        for(Object object : objects){
            releaseResource(object);
        }
    }

    private static void releaseResource(Object object){
        if(object instanceof List){
            ((List) object).clear();
        } else if(object instanceof Set){
            ((Set) object).clear();
        } else if(object instanceof Map){
            ((Map) object).clear();
        }
    }

    public static void printGcInfo(){
        List<GarbageCollectorMXBean> list = ManagementFactory.getGarbageCollectorMXBeans();
        for(GarbageCollectorMXBean bean : list){
            logger.info("Name: " + bean.getName());
            logger.info("Number of collections: " + bean.getCollectionCount());
            logger.info("Collection time: " + bean.getCollectionTime());
            logger.info("Pool names: " + bean.getName());
            for(String name : bean.getMemoryPoolNames()){
                logger.info("\t" + name);
            }
        }
    }

    public static void main(String[] args) {
        // -XX-useMarkSweep
        printGcInfo();
    }
}
