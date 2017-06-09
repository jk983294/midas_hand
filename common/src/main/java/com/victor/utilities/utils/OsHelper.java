package com.victor.utilities.utils;


public class OsHelper {

    public static boolean isLinux(){
        return System.getProperty("os.name").equalsIgnoreCase("linux");
    }

    public static String getPathByOs(String path){
        if(isLinux()){
            return "/tmp/" + path.replace("\\", "/");
        } else {
            return "D:\\" + path.replace("/", "\\");
        }
    }

    public static String getPathByOs(String linuxPrefix, String windowsPrefix, String path){
        if(isLinux()){
            return (linuxPrefix + path).replace("\\", "/");
        } else {
            return (windowsPrefix + path).replace("/", "\\");
        }
    }

    public static String getPathByOs(String prefix, String path){
        if(isLinux()){
            return (prefix + path).replace("\\", "/");
        } else {
            return (prefix + path).replace("/", "\\");
        }
    }

    public static String getDelimiter(){
        if(isLinux()){
            return "/";
        } else {
            return "\\";
        }
    }

}
