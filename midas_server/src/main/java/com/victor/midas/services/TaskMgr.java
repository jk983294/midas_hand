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
import org.springframework.context.ApplicationContext;
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
    private ApplicationContext context;

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
        TaskBase callable = null;
    	switch(cmdType){
            case delete :  callable = (DeleteStockCollTask) context.getBean("deleteStockCollTask");   break; //new ( taskDao , stockInfoDao, stockDao, miscDao, params );
            case dayStats :  callable = (DeleteStockCollTask) context.getBean("deleteStockCollTask"); break;//new DeleteStockCollTask( taskDao , stockInfoDao, stockDao, miscDao, params );  break;
            case create : callable = (CreateCollectionTask) context.getBean("createCollectionTask"); break;//new CreateCollectionTask( taskDao , stockInfoDao, stockDao, miscDao, params ); break;
            case load : callable = (MktDataTask) context.getBean("mktDataTask"); break;//new MktDataTask(taskDao, stocksService, environment, params); break;
            case calculate : callable = (CalculateTask) context.getBean("calculateTask"); break; //new CalculateTask(taskDao, stocksService, params);
            case trainSingle :
            case trainStrategy : callable = (TrainTask) context.getBean("trainTask");break;//new TrainTask(taskDao, stocksService, params, cmdType); break;
            case plan : callable = (PlanTask) context.getBean("planTask");break;//new PlanTask(taskDao, stocksService, params); break;
            case score :
                callable = (ScoreTask) context.getBean("scoreTask");
                ((ScoreTask)callable).init(false, true);
                break;//new ScoreTask(taskDao, stocksService, environment, false, params, true); break;
            case crawl : callable = (CrawlTask) context.getBean("crawlTask");break;//new CrawlTask(taskDao, stocksService, params); break;
            case load_score :
                callable = (ScoreTask) context.getBean("scoreTask");
                ((ScoreTask)callable).init(true, true);
                break;//new ScoreTask(taskDao, stocksService, environment, true, params, true); break;
            case chan : callable = (ChanTask) context.getBean("chanTask");break;//new ChanTask(taskDao, stocksService, params); break;
            default : logger.error("no such cmd in task manager.");
        }
        if(callable != null){
            callable.ctor(params, cmdType);
            result = executor.submit(callable);
        }
        Thread.yield();
        return result;
    }

    public void submitPipedTasks(List<String> actions){
        Callable<Integer> callable = (PipedTask) context.getBean("pipedTask");
        ((PipedTask)callable).init(this, actions);
        executor.submit(callable);
        logger.error("task submitted." );
        Thread.yield();
    }

}
