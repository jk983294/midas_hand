package com.victor.midas.services.worker.task.lucene;

import com.victor.midas.services.ReportService;
import com.victor.midas.services.worker.common.TaskBase;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class LuceneTask extends TaskBase {
	
	private static final Logger logger = Logger.getLogger(LuceneTask.class);
	private static final String description = "Lucene Report Task";

    @Autowired
    public ReportService reportService;

    @Override
	public void doTask() throws Exception {
        reportService.indexReports();
		logger.info( description + " complete...");
	}

    @Override
    public String getDescription() {
        return description;
    }

}
