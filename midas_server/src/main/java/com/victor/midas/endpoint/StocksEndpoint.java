package com.victor.midas.endpoint;

import com.victor.midas.model.db.DayFocusDb;
import com.victor.midas.model.db.StockInfoDb;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.model.vo.TrainResult;
import com.victor.midas.model.vo.score.StockScoreRecord;
import com.victor.midas.services.StocksService;
import com.victor.midas.util.MidasException;
import com.victor.midas.util.StringPatternAware;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@RestController
@RequestMapping("/stocks")
public class StocksEndpoint {
	private final Logger logger = Logger.getLogger(StocksEndpoint.class);

    @Autowired
    private StocksService stocksService;
	
	@GET
    @RequestMapping("/{name}")
	@Produces(MediaType.APPLICATION_JSON)
	public StockVo getStock(@PathVariable("name") String name) {
		return stocksService.queryStock(name);
	}

    @GET
    @RequestMapping("/multiply/{names}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StockVo> getTwoStocks(@PathVariable("names") String names) {
        List<StockVo> array = new ArrayList<>();
        if(names != null) {
            String[] stringlets = names.split(" ");
            for (int i = 0; i < stringlets.length; i++) {
                if(StringPatternAware.isStockCode(stringlets[i])){
                    array.add(stocksService.queryStock(stringlets[i]));
                }
            }
        }
        return array;
    }


    @GET
    @RequestMapping("/trainResult/{trainId}")
    @Produces(MediaType.APPLICATION_JSON)
    public TrainResult getTrainResult(@PathVariable("trainId") Long trainId) throws MidasException {
        logger.info("fetch train result for trainId : " + trainId);
        if(trainId == null || trainId < 0) return stocksService.queryLastTrainResult();
        else return stocksService.queryTrainResult(trainId);
    }

    @GET
    @RequestMapping("/stockinfos")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StockInfoDb> getStockBasicInfo() {
        return stocksService.getStockBasicInfo();
    }

    @GET
    @RequestMapping("/plan/focus")
    @Produces(MediaType.APPLICATION_JSON)
    public List<DayFocusDb> getFocusDb() {
        int n = 5;
        logger.info("fetch "+ n + " plan result ...");
        return stocksService.getStockDayFocus(n);
    }

    @GET
    @RequestMapping("/score")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StockScoreRecord> getStockScoreRecord() {
        int n = 10;
        logger.info("fetch "+ n + " score result ...");
        return stocksService.getScoreDao().queryLastStockScoreRecord(n);
    }

    @GET
    @RequestMapping("/score/{cobFrom}/{cobTo}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StockScoreRecord> getStockScoreRecord(@PathVariable("cobFrom") Integer cobFrom,
                                                      @PathVariable("cobTo") Integer cobTo) {
        return stocksService.getScoreDao().queryStockScoreRecordByRange(cobFrom, cobTo);
    }

    @GET
    @RequestMapping("/conceptScore")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StockScoreRecord> getConceptStockScoreRecord() {
        int n = 10;
        logger.info("fetch "+ n + " concept score result ...");
        return stocksService.getConceptScoreDao().queryLastStockScoreRecord(n);
    }

    @GET
    @RequestMapping("/conceptScore/{cobFrom}/{cobTo}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StockScoreRecord> getConceptStockScoreRecord(@PathVariable("cobFrom") Integer cobFrom,
                                                      @PathVariable("cobTo") Integer cobTo) {
        return stocksService.getConceptScoreDao().queryStockScoreRecordByRange(cobFrom, cobTo);
    }

	@GET
	@RequestMapping("/test/")
	public String test() {
//		logger.info(stockInfoDao.getStockCount());
//		logger.info("page");
//		logger.info(stockInfoDao.getStockByPaging(4, 1));
//		logger.info("basic info");
//		logger.info(stockInfoDao.queryAllBasicInfo());
		return "OK";
	}
	
	
}
