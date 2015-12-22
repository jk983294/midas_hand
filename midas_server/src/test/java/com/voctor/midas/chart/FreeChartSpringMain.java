package com.voctor.midas.chart;

import com.victor.midas.model.vo.StockVo;
import com.victor.midas.services.StocksService;
import com.victor.midas.services.TaskMgr;
import com.victor.midas.util.MidasException;
import com.victor.utilities.visual.VisualAssist;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * create db
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/midas-content.xml")
public class FreeChartSpringMain {

    private static final Logger logger = Logger.getLogger(FreeChartSpringMain.class);

    @Autowired
    private StocksService stocksService;

    @Test
    public void testFreeChart() throws MidasException {
        logger.info("\n\n\n\n");


        StockVo stock = stocksService.queryStock("IDX999999");
        double[] end = (double[])stock.queryCmpIndex("end");
        int[] ctf = (int[])stock.queryCmpIndex("ctf2");
        VisualAssist.print(ctf);
        VisualAssist.print(end);


        logger.info("\n\n\n\n");
    }
}
