package com.victor.midas.services;


import com.victor.midas.dao.StockDao;
import com.victor.midas.dao.MiscDao;
import com.victor.midas.model.common.CmdType;
import com.victor.midas.services.worker.common.TaskBase;
import com.victor.midas.services.worker.task.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;

import com.victor.midas.dao.TaskDao;
import com.victor.midas.dao.StockInfoDao;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.victor.midas.model.common.CmdType.*;

@Component
public class TaskMgr {

    private static final Logger logger = Logger.getLogger(TaskMgr.class);

	@Autowired
    private StockInfoDao stockInfoDao;
	@Autowired
    private TaskDao taskDao;
    @Autowired
    private StockDao stockDao;
    @Autowired
    private MiscDao miscDao;

    @Autowired
    private StocksService stocksService;

    @Resource(name="filepath2prefix")
    private Map<String, String> filepath2prefix;

    private TaskExecutor taskExecutor;

    @Autowired
    public TaskMgr(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }
    
    /**
     * take the instruction for different command
     */
    public void cmd(CmdType cmdType, List<String> params){
    	switch(cmdType){
            case delete :  addTask( new DeleteStockCollTask( taskDao , stockInfoDao, stockDao, miscDao, params ) );  break;
            case create : addTask( new CreateCollectionTask( taskDao , stockInfoDao, stockDao, miscDao, params ) ); break;
            case load : addTask(new MktDataTask(taskDao, stocksService, params)); break;
            case calculate : addTask(new CalculateTask(taskDao, stocksService, params)); break;
            case train : addTask(new TrainTask(taskDao, stocksService, params)); break;
            case plan : addTask(new PlanTask(taskDao, stocksService, params)); break;
            case score : addTask(new ScoreTask(taskDao, stocksService, false, params)); break;
            case crawl : addTask(new CrawlTask(taskDao, stocksService, params)); break;
            case load_score : addTask(new ScoreTask(taskDao, stocksService, true, params)); break;
            case chan : addTask(new ChanTask(taskDao, stocksService, params)); break;
            case perf : addTask(new PerfTask(taskDao, stocksService, params)); break;
            default : logger.error("no such cmd in task manager.");
        }
        Thread.yield();
    }

    public void addTask(TaskBase task) {
    	taskExecutor.execute(task);
    }

}
