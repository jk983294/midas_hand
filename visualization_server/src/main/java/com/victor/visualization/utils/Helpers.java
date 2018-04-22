package com.victor.visualization.utils;

import com.victor.utilities.utils.OsHelper;

import java.io.File;

public class Helpers {

    public static boolean isDirectory(String filePath){
        File dir = new File(filePath);
        return dir.exists() && dir.isDirectory();
    }

    public static String filePathEscape(String path){
        String delimiter = OsHelper.getDelimiter();
        path = path.replace("|", delimiter);
        path = path.replace("@", ".");
        return path;
    }
}
