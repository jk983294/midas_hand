package com.victor.midas.services.worker.common;

import com.victor.midas.model.common.CmdType;
import com.victor.midas.services.TaskMgr;
import com.victor.midas.util.MidasException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

@Component
@Scope("prototype")
public class PipedTask extends TaskBase {

	private static final Logger logger = Logger.getLogger(PipedTask.class);
	private static final String description = "Pipe Task";

    @Autowired
	private TaskMgr taskMgr;

	@Override
	public void doTask() throws Exception {
        logger.info( description + " start...");
        logger.info("cmd " + params);
        if(CollectionUtils.isNotEmpty(params)){
            for(String action : params){
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

    @Override
    public String getDescription() {
        return description;
    }

}
