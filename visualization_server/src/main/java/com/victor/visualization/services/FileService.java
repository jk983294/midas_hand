package com.victor.visualization.services;

import com.victor.utilities.report.CsvParser;
import com.victor.utilities.utils.ArrayHelper;
import com.victor.utilities.utils.OsHelper;
import com.victor.visualization.utils.Helpers;
import com.victor.visualization.utils.VisualException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.util.StringUtil;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;

@Component
public class FileService {

    private static final Logger logger = Logger.getLogger(FileService.class);

    public List<String> getPossibleFiles(String path) {
        Set<String> fileSet = new HashSet<>();
        List<String> filteredFiles = new ArrayList<>();

        if(StringUtils.isNotEmpty(path)){
            String delimiter = OsHelper.getDelimiter();
            path = Helpers.filePathEscape(path);
            int lastIndex = path.lastIndexOf(delimiter);
            if(lastIndex >= 0){
                String directory = path.substring(0, lastIndex + 1);
                String filePattern = path.substring(lastIndex + 1);

                File dir = new File(directory);
                if(dir.exists() && dir.isDirectory()){
                    fileSet.addAll(ArrayHelper.array2list(dir.list()));
                }

                if(StringUtils.isNotEmpty(filePattern)){
                    for(String p : fileSet){
                        if(p.startsWith(filePattern)){
                            filteredFiles.add(directory + p);
                        }
                    }
                } else {
                    for(String p : fileSet){
                        if(p.startsWith(filePattern)){
                            filteredFiles.add(directory + p);
                        }
                    }
                }

                fileSet.clear();
                fileSet.addAll(filteredFiles);
                filteredFiles.clear();

                if(Helpers.isDirectory(path) && !filePattern.equals(".")){
                    if(!path.endsWith(delimiter)){
                        path = path + delimiter;
                    }
                    String[] l = dir.list();
                    if(l != null){
                        for (String p : l){
                            fileSet.add(path + p);
                        }
                    }
                }
            }
        }
        filteredFiles.addAll(fileSet);
        return filteredFiles;
    }

    public Map<String, Object> getCsvContent(String path) throws IOException {
        Map<String, Object> result = new HashMap<>();
        if(StringUtils.isNotEmpty(path)){
            path = Helpers.filePathEscape(path);
            logger.info("get csv content for " + path);
            CsvParser parser = new CsvParser();
            parser.delimiter = ",";
            parser.parse(path);
            parser.mergeDateTime();
            String name = path;
            if(name.lastIndexOf(OsHelper.getDelimiter()) >= 0) {
                name = name.substring(name.lastIndexOf(OsHelper.getDelimiter()) + 1);
            }
            if(name.contains(".")) {
                name = name.substring(0, name.indexOf("."));
            }
            if(name.contains("_")) {
                name = name.substring(0, name.indexOf("_"));
            }
            result.put("name", name);
            result.put("rowMeta", parser.header);
            result.put("columnSize", parser.column2index.size());
            result.put("rows", parser.rows);
        }
        return result;
    }

}
