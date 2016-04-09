package com.victor.midas.services.worker.common;

import com.victor.midas.dao.TaskDao;
import com.victor.midas.model.common.CmdType;
import com.victor.midas.services.TaskMgr;
import com.victor.midas.util.MidasException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

public class PipedTask extends TaskBase {

	private static final Logger logger = Logger.getLogger(PipedTask.class);
	private static final String description = "Pipe Task";

	private TaskMgr taskMgr;
    private List<String> actionStrs;


	public PipedTask(TaskDao taskdao, TaskMgr taskMgr, List<String> actionStrs) {
		super(description, taskdao, actionStrs);
		this.actionStrs = actionStrs;
        this.taskMgr = taskMgr;
	}

	@Override
	public void doTask() throws Exception {
        logger.info( description + " start...");
        logger.info("cmd " + actionStrs);
        if(CollectionUtils.isNotEmpty(actionStrs)){
            for(String action : actionStrs){
                String[] actions = action.split(" ");
                if(actions.length > 0){
                    try {
                        CmdType cmdType = CmdType.valueOf(actions[0]);
                        List<String> params = new ArrayList<>(Arrays.asList(actions));
                        params.remove(0);
                        Future<Integer> result = taskMgr.cmd(cmdType, params);
                        if(result != null){
                            Integer a = result.get();
                            if(a < 0){
                                throw new MidasException("cmd execute failed " + action);
                            }
                        }
                    } catch (IllegalArgumentException e){
                        throw new MidasException("cmd not found in pipe task", e);
                    }
                }
            }
        }
		logger.info( description + " complete...");
	}

}
