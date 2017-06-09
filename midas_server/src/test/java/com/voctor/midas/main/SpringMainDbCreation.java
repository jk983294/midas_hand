package com.voctor.midas.main;

import com.victor.midas.model.common.CmdType;
import com.victor.midas.services.TaskMgr;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;

/**
 * create db
 */
public class SpringMainDbCreation {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("/midas-content.xml");
        TaskMgr taskMgr = (TaskMgr) context.getBean("taskMgr");
        taskMgr.cmd(CmdType.bad_cmd, new ArrayList<String>());
    }
}
