package com.victor.utilities.report.excel.generator;

import com.victor.utilities.report.excel.generator.common.ReportException;
import com.victor.utilities.report.excel.generator.common.ReportXssfBase;
import com.victor.utilities.report.excel.generator.common.TabWriterBase;
import com.victor.utilities.report.excel.model.AppMetadata;
import com.victor.utilities.report.excel.model.AppModel;
import com.victor.utilities.report.excel.util.AppSortingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates Appdata Report export
 * Both reports use the same basic format
 */
public class AppDataReport extends ReportXssfBase {

    private final static Logger logger = LoggerFactory.getLogger(AppDataReport.class);

    private  List<AppModel> appModels;

    private AppMetadata metadata;

    private Map<String, Map<String, List<AppModel>>> field12groupInfo2appModels;

    public AppDataReport(AppMetadata metadata, List<AppModel> appModels, String filename) {
        super(filename);
        this.metadata = metadata;
        this.appModels = appModels;
    }

    @Override
    public void writeTabs() throws ReportException {
        switch (metadata.getA()){
            case "a" : {
                aggregate();
                AppSortingUtil.getDataSortedByField1(field12groupInfo2appModels);
                int sheetIdx = 0;
                TabWriterBase tabWriter = new TabWriterForAppData(wb, metadata);
                tabWriter.generate("data", sheetIdx++, field12groupInfo2appModels);
            } break;
            case "b" : {

            } break;
            default: {
                logger.error("no a for " + metadata.getA());
                throw new ReportException("no a for " + metadata.getA());
            }
        }
    }

    /**
     * use TreeMap to get data sorted
     */
    private void aggregate(){
        field12groupInfo2appModels = new LinkedHashMap<>();
        for(AppModel appModel : appModels){
            String field1 = appModel.getField1();
            String groupInfo = appModel.getGroupInfo();
            if(field12groupInfo2appModels.containsKey(field1)){
                Map<String, List<AppModel>> groupInfo2appModels = field12groupInfo2appModels.get(field1);
                if(groupInfo2appModels.containsKey(groupInfo)){
                    groupInfo2appModels.get(groupInfo).add(appModel);
                } else {
                    List<AppModel> list = new ArrayList<>();
                    list.add(appModel);
                    groupInfo2appModels.put(groupInfo, list);
                }
            } else {
                Map<String, List<AppModel>> groupInfo2appModels = new LinkedHashMap<>();
                List<AppModel> list = new ArrayList<>();
                list.add(appModel);
                groupInfo2appModels.put(groupInfo, list);
                field12groupInfo2appModels.put(field1, groupInfo2appModels);
            }
        }
    }

}