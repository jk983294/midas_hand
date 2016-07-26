package com.victor.midas.services.worker.task;

import com.victor.midas.services.worker.common.TaskBase;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class CreateCollectionTask extends TaskBase {
	private static final Logger logger = Logger.getLogger(CreateCollectionTask.class);
	private static final String description = "Create Collection Task";

	@Override
	public void doTask() throws Exception {
		taskDao.createCollection();
        stockInfoDao.createCollection();
        stockDao.createCollection();
        miscDao.createCollection();
		logger.info(description + " complete...");
	}

	@Override
	public String getDescription() {
		return description;
	}

}
