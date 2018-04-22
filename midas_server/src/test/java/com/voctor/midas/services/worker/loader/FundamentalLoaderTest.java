package com.voctor.midas.services.worker.loader;

import com.victor.utilities.report.CsvParser;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

public class FundamentalLoaderTest {

    @Ignore
    @Test
    public void csvFileLoadTest() throws IOException {
        String path = "/home/kun/Data/MktData/fundamental/stock_report/cashflow_data_2017_1.csv";
        CsvParser parser = new CsvParser();
        parser.delimiter = ",";
        parser.parse(path);
        System.out.println(parser.columnCount);
    }

    @Test
    public void stripDataTest(){
        String fileName = "cashflow_data_2017_1.csv";
        fileName = fileName.substring(0, fileName.lastIndexOf("."));
        String quarter = fileName.substring(fileName.lastIndexOf("_") + 1);
        fileName = fileName.substring(0, fileName.lastIndexOf("_"));
        String year = fileName.substring(fileName.lastIndexOf("_") + 1);
        fileName = fileName.substring(0, fileName.lastIndexOf("_"));
        System.out.println(fileName);
        System.out.println(year);
        System.out.println(quarter);

        String data = "1723,600030,中信证券,,,,,,";
        String[] lets = data.split(",", -1);
        System.out.println(lets[0]);
    }
}
