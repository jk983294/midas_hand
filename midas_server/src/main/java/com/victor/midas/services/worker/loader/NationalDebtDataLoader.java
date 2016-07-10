package com.victor.midas.services.worker.loader;


import com.victor.midas.model.vo.MidasBond;
import com.victor.midas.util.MidasException;
import com.victor.utilities.algorithm.sort.InsertionSort;
import com.victor.utilities.report.excel.XlsxParser;
import com.victor.utilities.report.excel.parser.common.TabRow;
import com.victor.utilities.report.excel.parser.common.TabTable;
import com.victor.utilities.utils.TimeHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.DateUtil;

import java.io.FileInputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class NationalDebtDataLoader implements IDataLoader {

    private static final Logger logger = Logger.getLogger(FundDataLoader.class);
    DateFormat fmt =new SimpleDateFormat("yyyy/MM/dd");



    @Override
    public Object load(String path) throws Exception  {
        logger.info("load bond data from dir : " + path);
        List<String> filePaths = LoadUtils.getAllExcel(LoadUtils.getAllFilePaths(path));
        Collections.sort(filePaths);
        List<MidasBond> bonds = new ArrayList<>();
        for (String filePath : filePaths){
            bonds.addAll(extractData(filePath));
        }
        InsertionSort.sort(bonds);
        return bonds;
    }

    public List<MidasBond> extractData(String path) throws Exception {
        List<MidasBond> bonds = new ArrayList<>();
        MidasBond bond = null;
        TabTable table = XlsxParser.getTabTable(new FileInputStream(path));
        if(table != null){
            logger.info(table);
            int maxRowIndex = table.getMaxRowIndex();
            for(int i = 2; i <= maxRowIndex; ++i){
                TabRow tabRow = table.getRow(i);
                if(tabRow == null || !tabRow.containsData()){   // empty row
                    continue;
                }
                int cob = 0;
                String dateStr = tabRow.getCellData("A");
                if(StringUtils.isEmpty(dateStr)){
                    throw new MidasException("date format incorrect in row " + i);
                } else if(dateStr.contains("/")){
                    cob = TimeHelper.date2cob(fmt.parse(dateStr));
                } else {
                    cob = TimeHelper.date2cob(DateUtil.getJavaDate(Double.valueOf(dateStr)));
                }
                if(bond == null || bond.cob != cob){
                    bond = new MidasBond();
                    bond.cob = cob;
                    bonds.add(bond);
                }
                if(!"0d".equals(tabRow.getCellData("B"))){
                    bond.termName.add(tabRow.getCellData("B"));
                    bond.term.add(Double.valueOf(tabRow.getCellData("C")));
                    bond.yield.add(Double.valueOf(tabRow.getCellData("D")));
                }
            }
        }
        return bonds;
    }
}
