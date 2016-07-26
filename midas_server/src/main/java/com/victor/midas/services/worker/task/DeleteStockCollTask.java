package com.victor.midas.services.worker.task;

import com.victor.midas.services.worker.common.TaskBase;
import org.apache.log4j.Logger;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class DeleteStockCollTask extends TaskBase {
	private static final Logger logger = Logger.getLogger(DeleteStockCollTask.class);
	private static final String description = "Delete Collection Task";

	@Override
	public void doTask() {
        // clear all documents in task collection
//        taskDao.deleteCollection();
//        taskDao.createCollection();

        stockInfoDao.deleteCollection();
        stockDao.deleteCollection();
        miscDao.deleteCollection();
		logger.info(description + " complete...");
	}

	@Override
	public String getDescription() {
		return description;
	}
}
