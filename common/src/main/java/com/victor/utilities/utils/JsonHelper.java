package com.victor.utilities.utils;

import com.google.gson.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * I/O Utils
 */
public class JsonHelper {

    private final static String delimiter_base = "    ";

    private List<String> delimiters = new ArrayList<>();

    public JsonHelper() {
    }

    public String toJson(Object o){
        Gson g = (new GsonBuilder().serializeSpecialFloatingPointValues().create());
        JsonElement element = g.toJsonTree(o);
        StringBuilder sb = new StringBuilder();
        toJsonFile(sb, null, element, 0);
        return sb.toString();
    }

    private void toJsonFile(StringBuilder sb, String elementName, JsonElement element, int level){
        String delimiter = getDelimeter(level);

        if(elementName != null){
            sb.append(delimiter).append("\"").append(elementName).append("\" : ");
        }

        if(element.isJsonObject()){
            sb.append(elementName != null ? "" : delimiter).append("{").append("\n");
            JsonObject object = element.getAsJsonObject();
            int len = object.entrySet().size(), cnt = 0;
            for(Map.Entry<String, JsonElement> entry : object.entrySet()){
                toJsonFile(sb, entry.getKey(), entry.getValue(), level + 1);
                ++cnt;
                if(cnt == len){
                    sb.append("\n");
                } else {
                    sb.append(",\n");
                }
            }
            sb.append(delimiter).append("}");
        } else if(element.isJsonArray()){
            sb.append(elementName != null ? "" : delimiter).append("[").append("\n");
            JsonArray array = element.getAsJsonArray();
            int len = array.size(), cnt = 0;
            for (int i = 0; i < array.size(); i++) {
                toJsonFile(sb, null, array.get(i), level + 1);
                ++cnt;
                if(cnt == len){
                    sb.append("\n");
                } else {
                    sb.append(",\n");
                }
            }
            sb.append(delimiter).append("]");
        } else if(element.isJsonPrimitive()){
            sb.append(elementName != null ? "" : delimiter).append(element.getAsJsonPrimitive());
        } else if(element.isJsonNull()){
            sb.append(elementName != null ? "" : delimiter).append("null");
        }
    }

    private String getDelimeter(int level){
        if(level < delimiters.size()){
            return delimiters.get(level);
        } else {
            String delimeter = "";
            for (int i = 0; i < level; i++) {
                delimeter += delimiter_base;
            }
            delimiters.add(delimeter);
            return delimeter;
        }
    }

}
