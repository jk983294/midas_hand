package com.victor.utilities.report.excel.util;

import com.victor.utilities.model.KeyValue;
import com.victor.utilities.report.excel.model.AppModel;
import com.victor.utilities.utils.RegExpHelper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.regex.Pattern;

/**
 * for report purpose, display order need to reorder
 */
public class AppSortingUtil {

    private static final Logger logger = Logger.getLogger(AppSortingUtil.class);

    private static final String[] field1s = new String[]{"aa", "bb", "cc", "dd", "ee"};
    private static final Pattern[] field1Patterns = new Pattern[field1s.length];

    static {
        for(int i = 0; i < field1s.length; ++i){
            field1Patterns[i] = Pattern.compile(field1s[i]);
        }
    }

    /**
     * sort by Field1
     * field12groupInfo2appModels must be a LinkedHashMap
     * @param field12groupInfo2appModels
     * @return
     */
    public static void getDataSortedByField1(Map<String, Map<String, List<AppModel>>> field12groupInfo2appModels){
        KeyValue<String, Map<String, List<AppModel>>>[] data = new KeyValue[field1s.length];
        Map<String, Map<String, List<AppModel>>> missing = new LinkedHashMap<>();
        boolean hasData = false;
        for(Map.Entry<String, Map<String, List<AppModel>>> entry : field12groupInfo2appModels.entrySet()) {
            String field1 = entry.getKey();
            int index = getIndex(field1Patterns, field1);
            if(index >= 0){
                data[index] = new KeyValue<>(field1, entry.getValue());
                hasData = true;
            } else {
                missing.put(field1, entry.getValue());
                logger.error("can not find field1 order : " + field1);
            }
        }
        if(hasData){
            field12groupInfo2appModels.clear();
            for(KeyValue<String, Map<String, List<AppModel>>> a : data){
                if(a != null){
                    field12groupInfo2appModels.put(a.getKey(), a.getValue());
                }
            }
            field12groupInfo2appModels.putAll(missing);
        }
    }

    /**
     * sort appModels list by order
     */
    public static void sortByOrder(List<AppModel> appModels){
        if(CollectionUtils.isNotEmpty(appModels)){
            Collections.sort(appModels, new OrderComparator());
        }
    }

    private static class OrderComparator implements Comparator<AppModel> {
        @Override
        public int compare(AppModel o1, AppModel o2) {
            return o1.getOrder().compareTo(o2.getOrder());
        }
    }

    private static int getIndex(String[] data, String toFind){
        toFind = toFind.toLowerCase().trim();
        for(int i = 0; i < data.length; ++i){
            if(toFind.contains(data[i])){
                return i;
            }
        }
        return -1;
    }

    private static int getIndex(Pattern[] patterns, String toFind){
        toFind = toFind.toLowerCase().trim();
        for(int i = 0; i < patterns.length; ++i){
            if(RegExpHelper.contains(toFind, patterns[i])){
                return i;
            }
        }
        return -1;
    }

}