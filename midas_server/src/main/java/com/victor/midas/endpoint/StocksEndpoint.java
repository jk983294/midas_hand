package com.victor.midas.endpoint;

import com.victor.midas.dao.MiscDao;
import com.victor.midas.model.db.StockInfoDb;
import com.victor.midas.model.train.SingleParameterTrainResults;
import com.victor.midas.model.vo.AipResults;
import com.victor.midas.model.vo.MidasBond;
import com.victor.midas.model.vo.StockDayStats;
import com.victor.midas.model.vo.StockVo;
import com.victor.midas.services.ReportService;
import com.victor.midas.services.StocksService;
import com.victor.midas.util.MidasException;
import com.victor.midas.util.StringPatternAware;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/stocks")
public class StocksEndpoint {
	private final Logger logger = Logger.getLogger(StocksEndpoint.class);

    @Autowired
    private StocksService stocksService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private MiscDao miscDao;
	
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
            String[] stringLets = names.split(" ");
            for (String stringLet : stringLets) {
                if (StringPatternAware.isStockCode(stringLet)) {
                    array.add(stocksService.queryStock(stringLet));
                }
            }
        }
        return array;
    }

    @GET
    @RequestMapping("/singleTrainResult")
    @Produces(MediaType.APPLICATION_JSON)
    public SingleParameterTrainResults getSingleTrainResult() throws MidasException {
        logger.info("fetch single train result ");
        return stocksService.querySingleParameterTrainResults();
    }

    @GET
    @RequestMapping("/stockinfos")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StockInfoDb> getStockBasicInfo() {
        return stocksService.getStockBasicInfo();
    }

    @GET
    @RequestMapping("/score")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getStockScoreRecord() {
        logger.info("fetch all score result ...");
        return stocksService.queryScore(null, null);
    }

    @GET
    @RequestMapping("/score/{cobFrom}/{cobTo}")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getStockScoreRecord(@PathVariable("cobFrom") Integer cobFrom,
                                                      @PathVariable("cobTo") Integer cobTo) {
        return stocksService.queryScore(cobFrom, cobTo);
    }

    @GET
    @RequestMapping("/national-debt")
    @Produces(MediaType.APPLICATION_JSON)
    public List<MidasBond> getNationalDebt() {
        return stocksService.queryNationalDebt();
    }

    @GET
    @RequestMapping("/day-stats/{cob}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<StockDayStats> getStockScoreRecord(@PathVariable("cob") Integer cob) throws MidasException, ParseException {
        return stocksService.queryDayStatsList(cob);
    }

    @GET
    @RequestMapping("/reports/{query}")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, List<String>> getStockReports(@PathVariable("query") String query)
            throws MidasException, IOException, org.apache.lucene.queryparser.classic.ParseException {
        if(StringUtils.isEmpty(query)) return new HashMap<>();
        else return reportService.queryReports(query);
    }

    @GET
    @RequestMapping("/aip")
    @Produces(MediaType.APPLICATION_JSON)
    public AipResults getAipData() {
        return miscDao.queryAipData();
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
