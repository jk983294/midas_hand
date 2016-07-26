package com.victor.midas.services.worker.common;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.Callable;

import com.victor.midas.dao.*;
import com.victor.midas.model.common.CmdType;
import com.victor.midas.services.StocksService;
import com.victor.midas.util.MidasException;
import org.apache.log4j.Logger;

import com.victor.midas.model.db.TaskDb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public abstract class TaskBase implements Callable<Integer> {

	private TaskDb taskDb;

	@Autowired
	public TaskDao taskDao;
    @Autowired
    public StocksService stocksService;
    @Autowired
    public ScoreDao scoreDao;
    @Autowired
    public ConceptScoreDao conceptScoreDao;
    @Autowired
    public StockInfoDao stockInfoDao;
    @Autowired
    public StockDao stockDao;
    @Autowired
    public MiscDao miscDao;
    @Autowired
    public Environment environment;
    @Autowired
    public TrainDao trainDao;

	
	private static final Logger logger = Logger.getLogger(TaskBase.class);

    protected List<String> params;
    protected CmdType cmdType;
	
	public void ctor(List<String> params, CmdType cmdType) {
		taskDb = new TaskDb(getDescription());
        this.params = params;
        this.cmdType = cmdType;
	}

	public TaskBase() {
	}

	/**
	 * framework to run a task, handle status change, and responsible for DB serialization
	 */
	@Override
	public Integer call() {
		taskDb.setStatus(TaskStatus.Execute);
		taskDao.saveTask(taskDb);		// save task to DB
		try {
			doTask();							
		} catch (Exception e) {
			e.printStackTrace();
            taskDb.setFailInfo(e.toString());
			taskDb.setStatus(TaskStatus.Error);
            throw new RuntimeException(e);
		} finally{
			taskDb.setFinish(new Timestamp(System.currentTimeMillis()));
			if(taskDb.getStatus() != TaskStatus.Error) taskDb.setStatus(TaskStatus.Finished);
			taskDao.saveTask(taskDb);
		}
		return 0;
	}
	
	/**
	 * concrete sub task should implement its business logic 
	 * should not swallow exception, it should be handled by run framework
	 * @throws Exception
	 */
	public abstract void doTask() throws Exception;

	public abstract String getDescription();

    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }
}
