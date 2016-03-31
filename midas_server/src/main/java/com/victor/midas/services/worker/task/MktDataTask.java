package com.victor.midas.services.worker.task;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.victor.midas.calculator.indicator.IndexChangePct;
import com.victor.midas.model.common.StockType;
import com.victor.midas.util.MidasException;
import com.victor.midas.calculator.IndexCalculator;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.services.StocksService;
import com.victor.midas.services.worker.common.TaskBase;
import com.victor.midas.util.MidasConstants;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.victor.midas.dao.TaskDao;
import org.springframework.core.env.Environment;

public class MktDataTask extends TaskBase {
	
	private static final Logger logger = Logger.getLogger(MktDataTask.class);
	private static final String description = "Market Data Load Task";

	private StocksService stocksService;
    private Environment environment;
    private boolean loadStock = true;   // false means load fund
    private String loadPath;

	public MktDataTask(TaskDao taskdao, StocksService stocksService, Environment environment, List<String> params) {
		super(description, taskdao, params);
		this.stocksService = stocksService;
        this.environment = environment;
        loadPath = environment.getProperty("MktDataLoader.Stock.Path");
        if(params != null && params.size() > 0 && params.get(0).equalsIgnoreCase("fund")){
            loadStock = false;
            loadPath = environment.getProperty("MktDataLoader.Fund.Path");
        }
	}

	@Override
	public void doTask() throws Exception {
        List<StockVo> stocks = getStockFromFileSystem(loadPath);
        stocksService.saveStocks(stocks);
		logger.info( description + " complete...");
	}

    public static List<StockVo> getStockFromFileSystem(String path) throws Exception {
        logger.info("load data from dir : " + path);
        List<StockVo> stocks = new ArrayList<>();
        fromDirectory(stocks, path);
        calcIndex(stocks);
        return stocks;
    }

    public static void fromDirectory(List<StockVo> stocks, String dir) throws Exception {
        File root = new File(dir);
        File[] files = root.listFiles();
        for(File file:files){
            if(file.isDirectory()){
                fromDirectory(stocks, file.getAbsolutePath());
            }else{
                String fileName = file.getAbsolutePath();
                if(fileName.endsWith(".TXT")||fileName.endsWith(".txt")){
                    StockVo stock = fromFile(file.getAbsolutePath());
                    if(stock == null || stock.getStartDate() == null){
                    } else {
                        stocks.add(stock);
                    }
                }
            }
        }
    }

    private static void calcIndex(List<StockVo> stocks) throws MidasException {
        IndexCalculator indexCalculator = new IndexCalculator(stocks, IndexChangePct.INDEX_NAME);
        indexCalculator.calculate();
    }
	
	/**
	 * read market data from file
	 * @param path
	 * @return
	 * @throws IOException 
	 */
	public static StockVo fromFile(String path) throws Exception {
        File file = new File(path);
        List<String> contents = FileUtils.readLines(file, "GBK");

        if(contents.size() < 1) return null;

        String arrs[] = contents.get(0).split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < arrs.length-2; i++) {
            sb.append(arrs[i]);
        }
        String stockCode = getStockCode(sb.toString(), arrs[0], path);

        if(contents.size() < 4){
            logger.error("no data in stock " + stockCode + ", skip it...");
            return null;
        }
        int cnt = contents.size() - 3 - (isLastRecordInvalid(contents.get(contents.size() - 2)) ? 1 : 0);
        double[] start = new double[cnt];
        double[] end = new double[cnt];
        double[] max = new double[cnt];
        double[] min = new double[cnt];
        double[] total = new double[cnt];
        double[] volume = new double[cnt];
        int[] date = new int[cnt];

        for (int i = 0; i < cnt; i++) {
            String arr[] = contents.get( i + 2 ).split("\t");
            date[i] = string2date(arr[0]);
            start[i] = Double.valueOf(arr[1]);
            max[i] = Double.valueOf(arr[2]);
            min[i] = Double.valueOf(arr[3]);
            end[i] = Double.valueOf(arr[4]);
            volume[i] = Double.valueOf(arr[5]);
            total[i] = Double.valueOf(arr[6]);
        }

        StockVo stock = new StockVo(stockCode, sb.toString(), getStockType(stockCode));
        stock.addTimeSeries(date);
        stock.addIndex(MidasConstants.INDEX_NAME_START, start);
        stock.addIndex(MidasConstants.INDEX_NAME_MAX, max);
        stock.addIndex(MidasConstants.INDEX_NAME_MIN, min);
        stock.addIndex(MidasConstants.INDEX_NAME_END, end);
        stock.addIndex(MidasConstants.INDEX_NAME_VOLUME, volume);
        stock.addIndex(MidasConstants.INDEX_NAME_TOTAL, total);
		return stock;
	}

    private static String getStockCode(String stockName, String stockCode, String filePath){
        if(stockName.endsWith("指") || stockName.endsWith("指数")){
            return "IDX" + stockCode;
        } else {
            return filePath.substring(filePath.lastIndexOf("\\") + 1, filePath.lastIndexOf("."));
        }
    }

    private static StockType getStockType(String stockCode) throws Exception {
       if(stockCode.startsWith("SH")){
           return StockType.SH;
       } else if(stockCode.startsWith("SZ")){
           return StockType.SZ;
       } else if(stockCode.startsWith("IDX")){
            return StockType.Index;
       } else {
           throw new Exception("UNKNOWN StockType for " + stockCode);
       }
    }

    /**
     * use last second line to check if this line is valid
     */
    private static boolean isLastRecordInvalid(String line){
        String arr[] = line.split("\t");
        Double volume = Double.valueOf(arr[5]);
        return Math.abs(volume) < 1e-6d;
    }
	
	private static int string2date(String str){
        int date = 0;
        date += Integer.valueOf(str.substring(0,4)) * 10000;
        date += Integer.valueOf(str.substring(5,7)) * 100;
        date += Integer.valueOf(str.substring(8,10));
        return date;
    }

    /**
     * pre-fetch all paths
     * maybe used for load balance
     * @return
     * @throws Exception
     */
    public List<String> getAllFilePaths() throws Exception{
        List<String> filepaths = new ArrayList<>();
        filepaths.addAll(getAllFilePaths("F:\\Data\\MktData\\ALL"));
        return filepaths;
    }

    public List<String> getAllFilePaths(String dir) throws Exception{
        File root = new File(dir);
        File[] files = root.listFiles();
        List<String> filepaths = new ArrayList<>();
        for(File file:files){
            if(file.isDirectory()){
                filepaths.addAll(getAllFilePaths(file.getAbsolutePath()));
            }else{
                filepaths.add(file.getAbsolutePath());
            }
        }
        return filepaths;
    }
}
