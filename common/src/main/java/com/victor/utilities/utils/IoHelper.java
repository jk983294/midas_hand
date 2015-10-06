package com.victor.utilities.utils;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * I/O Utils
 */
public class IoHelper {

    public static String toJson(Object o){
        Gson g = new Gson();
        String json = g.toJson(o);
        return json;
    }

    public static void toJsonFileWithIndent(Object o, String filePath) throws IOException {
        FileUtils.writeStringToFile(new File(filePath), new JsonHelper().toJson(o));
    }

    public static void toJsonFileWithoutIndent(Object o, String filePath) throws IOException {
        FileUtils.writeStringToFile(new File(filePath), toJson(o));
    }

}
