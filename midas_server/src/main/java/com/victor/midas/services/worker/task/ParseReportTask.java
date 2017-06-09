package com.victor.midas.services.worker.task;

import com.victor.midas.report.FinancialReportParser;
import com.victor.midas.services.worker.common.TaskBase;
import com.victor.utilities.utils.OsHelper;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class ParseReportTask extends TaskBase {
	
	private static final Logger logger = Logger.getLogger(ParseReportTask.class);
	private static final String description = "Parse Report Task";

    @Override
	public void doTask() throws Exception {
        FinancialReportParser parser = new FinancialReportParser();
        parser.init(OsHelper.getPathByOs(environment.getProperty("MktDataLoader.Fundamental.cninfo"), "000001/reports/13907526_20040415_深发展Ａ2003年年度报告.pdf"));
        parser.parse();
		logger.info( description + " complete...");
	}

    @Override
    public String getDescription() {
        return description;
    }

}
