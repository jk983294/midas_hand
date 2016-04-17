package com.victor.midas.services;


import com.victor.midas.dao.StockDao;
import com.victor.midas.dao.MiscDao;
import com.victor.midas.model.common.CmdType;
import com.victor.midas.services.worker.common.PipedTask;
import com.victor.midas.services.worker.common.TaskBase;
import com.victor.midas.services.worker.task.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PreferencesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;

import com.victor.midas.dao.TaskDao;
import com.victor.midas.dao.StockInfoDao;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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

    @Autowired
    private Environment environment;

    @Resource(name="filepath2prefix")
    private Map<String, String> filepath2prefix;

    private ExecutorService executor = Executors.newFixedThreadPool(10);
    
    /**
     * take the instruction for different command
     */
    public Future<Integer> cmd(CmdType cmdType, List<String> params){
        Future<Integer> result = null;
        Callable<Integer> callable = null;
    	switch(cmdType){
            case delete :  callable = new DeleteStockCollTask( taskDao , stockInfoDao, stockDao, miscDao, params );  break;
            case create : callable = new CreateCollectionTask( taskDao , stockInfoDao, stockDao, miscDao, params ); break;
            case loadStock : callable = new MktDataTask(taskDao, stocksService, environment, params, true); break;
            case loadFund : callable = new MktDataTask(taskDao, stocksService, environment, params, false); break;
            case calculate : callable = new CalculateTask(taskDao, stocksService, params); break;
            case trainSingle :
            case trainStrategy : callable = new TrainTask(taskDao, stocksService, params, cmdType); break;
            case plan : callable = new PlanTask(taskDao, stocksService, params); break;
            case score : callable = new ScoreTask(taskDao, stocksService, environment, false, params, true); break;
            case crawl : callable = new CrawlTask(taskDao, stocksService, params); break;
            case load_score : callable = new ScoreTask(taskDao, stocksService, environment, true, params, true); break;
            case chan : callable = new ChanTask(taskDao, stocksService, params); break;
            case perf : callable = new PerfTask(taskDao, stocksService, params); break;
            default : logger.error("no such cmd in task manager.");
        }
        if(callable != null){
            result = executor.submit(callable);
        }
        Thread.yield();
        return result;
    }

    public void submitPipedTasks(List<String> actions){
        executor.submit(new PipedTask(taskDao, this, actions));
        Thread.yield();
    }

}
