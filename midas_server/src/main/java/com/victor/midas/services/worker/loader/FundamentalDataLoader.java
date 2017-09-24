package com.victor.midas.services.worker.loader;

import com.victor.midas.model.common.StockType;
import com.victor.midas.model.vo.StockVo;
import com.victor.utilities.report.CsvParser;
import com.victor.utilities.utils.OsHelper;
import com.victor.utilities.utils.TimeHelper;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class FundamentalDataLoader implements IDataLoader {

    private static final Logger logger = Logger.getLogger(FundamentalDataLoader.class);

    private Map<String, Map<Integer, Map<String, String>>> code2quarter2attribute2value = new HashMap<>();

    @Override
    public Object load(String path) throws Exception {
        logger.info("load fundamental data from dir : " + path);
        List<StockVo> stocks = new ArrayList<>();
        fromDirectory(stocks, path);
        return stocks;
    }

    public void fromDirectory(List<StockVo> stocks, String dir) throws Exception {
        File root = new File(dir);
        File[] files = root.listFiles();
        for(File file: files){
            String fileName = file.getAbsolutePath();
            if(file.getAbsolutePath().endsWith(".csv")){
                fromFile(file);
            }
        }
    }

    /**
     * read market data from file
     * @throws IOException
     */
    public void fromFile(File file) throws Exception {
        String fileName = file.getName();
        if(!fileName.endsWith(".csv")) return;

        fileName = fileName.substring(0, fileName.lastIndexOf("."));
        String quarter = fileName.substring(fileName.lastIndexOf("_") + 1);
        fileName = fileName.substring(0, fileName.lastIndexOf("_"));
        String year = fileName.substring(fileName.lastIndexOf("_") + 1);
        fileName = fileName.substring(0, fileName.lastIndexOf("_"));

        int quarterCount = TimeHelper.quarterCount(Integer.valueOf(year), Integer.valueOf(quarter));

        CsvParser parser = new CsvParser();
        parser.delimiter = ",";
        parser.parse(file.getAbsolutePath());

        int headerCount = parser.header.size();
        for (List<String> row : parser.rows){
            String code = row.get(1);
            if(!code2quarter2attribute2value.containsKey(code)){
                code2quarter2attribute2value.put(code, new TreeMap<>());
            }

            Map<Integer, Map<String, String>> quarter2attribute2value = code2quarter2attribute2value.get(code);
            if(!quarter2attribute2value.containsKey(quarterCount)){
                quarter2attribute2value.put(quarterCount, new TreeMap<>());
            }

            Map<String, String> attribute2value = quarter2attribute2value.get(quarterCount);

            // 0 is count index, 1 is code, 2 is name
            for (int i = 3; i < headerCount; i++) {
                attribute2value.put(parser.header.get(i), parser.getRowAt(row, i));
            }
        }

        parser.clear();
    }

    private String getStockCode(String stockName, String stockCode, String filePath){
        if(stockName.endsWith("指") || stockName.endsWith("指数")){
            return "IDX" + stockCode;
        } else {
            return filePath.substring(filePath.lastIndexOf(OsHelper.getDelimiter()) + 1, filePath.lastIndexOf("."));
        }
    }

    private StockType getStockType(String stockName, String stockCode) throws Exception {
        if(stockCode.startsWith("SH")){
            return StockType.SH;
        } else if(stockCode.startsWith("SZ")){
            return StockType.SZ;
        } else if(stockCode.startsWith("IDX")){
            return StockType.Index;
        }
        logger.error("unknown type for " + stockCode + " " + stockName);
        return StockType.UNKNOWN;
    }

    /**
     * use last second line to check if this line is valid
     */
    private static boolean isLastRecordInvalid(String line){
        String arr[] = line.split("\t");
        Double volume = Double.valueOf(arr[5]);
        return Math.abs(volume) < 1e-6d;
    }
}
