package com.victor.midas.services;


import com.victor.midas.model.common.CmdType;
import com.victor.midas.services.worker.common.PipedTask;
import com.victor.midas.services.worker.common.TaskBase;
import com.victor.midas.services.worker.task.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
public class TaskMgr {

    private static final Logger logger = Logger.getLogger(TaskMgr.class);

    @Autowired
    private ApplicationContext context;

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
            case delete :  callable = (DeleteStockCollTask) context.getBean("deleteStockCollTask");   break;
            case dayStats :  callable = (DeleteStockCollTask) context.getBean("deleteStockCollTask"); break;
            case create : callable = (CreateCollectionTask) context.getBean("createCollectionTask"); break;
            case load : callable = (MktDataTask) context.getBean("mktDataTask"); break;
            case calculate : callable = (CalculateTask) context.getBean("calculateTask"); break;
            case trainSingle :
            case trainStrategy : callable = (TrainTask) context.getBean("trainTask");break;
            case plan : callable = (PlanTask) context.getBean("planTask");break;
            case score :
                callable = (ScoreTask) context.getBean("scoreTask");
                ((ScoreTask)callable).init(false, true);
                break;
            case crawl : callable = (CrawlTask) context.getBean("crawlTask");break;
            case load_score :
                callable = (ScoreTask) context.getBean("scoreTask");
                ((ScoreTask)callable).init(true, true);
                break;
            case chan : callable = (ChanTask) context.getBean("chanTask");break;
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
        TaskBase callable = (PipedTask) context.getBean("pipedTask");
        callable.ctor(actions, null);
        executor.submit(callable);
        logger.error("task submitted." );
        Thread.yield();
    }

}
