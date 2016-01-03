package com.victor.utilities.report.excel;

import com.victor.utilities.report.excel.generator.AppDataReport;
import com.victor.utilities.report.excel.generator.common.ReportException;
import com.victor.utilities.report.excel.model.AppMetadata;
import com.victor.utilities.report.excel.model.AppModel;

import javax.ws.rs.core.Response;
import java.util.List;

public class XlsxGenerator {

    /**
     * Generates the AppData report
     * @param filename - the filename to export to
     * @return Response - byte data of generated file
     * @throws ReportException
     */
    public static Response generateAppDataReport(AppMetadata metadata, List<AppModel> appModels, String filename) throws ReportException {
        return new AppDataReport(metadata, appModels, filename).generateWorkbook();
    }

}
