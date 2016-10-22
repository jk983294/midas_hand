package com.victor.utilities.utils;


public class SqlHelper {

    public static String escapeNewLineCharacter(String sql){
        return sql.replace("\n", "'||CHR(10)||'");
    }
}
