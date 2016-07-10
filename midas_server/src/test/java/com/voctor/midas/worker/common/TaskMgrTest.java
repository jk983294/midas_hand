package com.voctor.midas.worker.common;

import com.victor.midas.model.common.CmdType;
import com.victor.midas.services.TaskMgr;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * unit test for TaskMgr
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/midas-content.xml")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class TaskMgrTest {

    private static final Logger logger = Logger.getLogger(TaskMgrTest.class);

    @Autowired
    private TaskMgr taskMgr;

    @Test
    public void testLoadMarketDataTaskCmd() throws InterruptedException {
        logger.info("taskMgr.cmd(loadMarketData)");
        taskMgr.cmd(CmdType.load, new ArrayList<String>());
        TimeUnit.DAYS.sleep(1);
    }

    @Test
    public void testDeleteAllDataTaskCmd() throws InterruptedException {
        logger.info("taskMgr.cmd(deleteAllData)");
        taskMgr.cmd(CmdType.delete, new ArrayList<String>());
        TimeUnit.DAYS.sleep(1);
    }

    @Test
    public void testCreateDBTaskCmd() throws InterruptedException {
        logger.info("taskMgr.cmd(createDB)");
        taskMgr.cmd(CmdType.create, new ArrayList<String>());
        TimeUnit.DAYS.sleep(1);
    }
}
