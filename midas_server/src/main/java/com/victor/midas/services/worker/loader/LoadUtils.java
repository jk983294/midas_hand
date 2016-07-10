package com.victor.midas.services.worker.loader;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class LoadUtils {

    /**
     * pre-fetch all paths
     * maybe used for load balance
     * @return
     * @throws Exception
     */
    public static List<String> getAllFilePaths(String dir) throws Exception{
        File root = new File(dir);
        File[] files = root.listFiles();
        List<String> filePaths = new ArrayList<>();
        for(File file: files){
            if(file.isDirectory()){
                filePaths.addAll(getAllFilePaths(file.getAbsolutePath()));
            }else{
                filePaths.add(file.getAbsolutePath());
            }
        }
        return filePaths;
    }

    public static List<String> getAllTxt(List<String> filePaths){
        return filterWithSuffix(filePaths, txtSuffixPattern);
    }

    public static List<String> getAllCsv(List<String> filePaths){
        return filterWithSuffix(filePaths, csvSuffixPattern);
    }

    public static List<String> getAllExcel(List<String> filePaths){
        return filterWithSuffix(filePaths, excelSuffixPattern);
    }

    private static final Pattern txtSuffixPattern = Pattern.compile(".*\\.txt$");
    private static final Pattern csvSuffixPattern = Pattern.compile(".*\\.csv$");
    private static final Pattern excelSuffixPattern = Pattern.compile(".*\\.(xls|xlsx)$");
    public static List<String> filterWithSuffix(List<String> filePaths, Pattern suffix){
        List<String> results = new ArrayList<>();
        for(String fileName : filePaths){
            if(suffix.matcher(fileName.toLowerCase()).matches()){
                results.add(fileName);
            }
        }
        return filePaths;
    }

}
