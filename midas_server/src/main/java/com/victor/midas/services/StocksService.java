package com.victor.midas.services;

import java.util.List;
import java.util.Set;

import com.victor.midas.calculator.common.IndexCalcBase;
import com.victor.midas.dao.*;
import com.victor.midas.model.db.DayFocusDb;
import com.victor.midas.model.db.StockInfoDb;
import com.victor.midas.model.db.misc.StockNamesDb;
import com.victor.midas.model.vo.StockVo;

import com.victor.midas.model.vo.TrainResult;
import com.victor.midas.model.vo.concept.StockCrawlData;
import com.victor.midas.util.MidasException;
import com.victor.midas.util.ModelConvertor;
import org.apache.log4j.Logger;

import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StocksService {
	private final Logger logger = Logger.getLogger(StocksService.class);

    @Autowired
    private StockInfoDao stockInfoDao;
    @Autowired
    private TaskDao taskDao;
    @Autowired
    private StockDao stockDao;
    @Autowired
    private MiscDao miscDao;
    @Autowired
    private TrainDao trainDao;
    @Autowired
    private DayFocusDao dayFocusDao;
    @Autowired
    private ScoreDao scoreDao;
    @Autowired
    private ConceptScoreDao conceptScoreDao;
    @Autowired
    private StockCrawlDataDao stockCrawlDataDao;
	@Autowired
	private TypeAhead typeAhead;

    // register all calculator to factory
    static {
        Reflections reflections = new Reflections("com.victor.midas.calculator");
        Set<Class<? extends IndexCalcBase>> subTypes = reflections.getSubTypesOf(IndexCalcBase.class);
    }


    public void saveStocks(List<StockVo> stocks) throws MidasException {
        StockNamesDb stockNames = ModelConvertor.convert2StockNames(stocks);
        miscDao.saveMisc(stockNames);

        List<StockInfoDb> stockInfoDbs = ModelConvertor.convert2StockInfo(stocks);
        stockInfoDao.saveStockInfo(stockInfoDbs);
        stockDao.saveStock(stocks);
    }

    public TrainResult queryTrainResult(Long trainId){
        return trainDao.queryTrainResult(trainId);
    }

    public TrainResult queryLastTrainResult() throws MidasException {
        return trainDao.queryLastTrainResult();
    }

    public StockVo queryStock(String stockName){
        return stockDao.queryStock(stockName);
    }

    public List<StockVo> queryAllStock(){
        return stockDao.queryAllStock();
    }

    public List<String> queryAllStockNames(){
        return miscDao.queryStockNames().getStockNames();
    }

    public List<StockCrawlData> queryAllStockCrawlData(){
        return stockCrawlDataDao.queryAllCrawlData();
    }

    public void saveAllStockCrawlData(List<StockCrawlData> crawlDatas){
        stockCrawlDataDao.saveCrawlData(crawlDatas);
    }

    public List<StockInfoDb> getStockBasicInfo() {
        return stockInfoDao.queryAllBasicInfo();
    }

    public List<DayFocusDb> getStockDayFocus() {
        return dayFocusDao.queryAllDayFocus();
    }

    public List<DayFocusDb> getStockDayFocus(int n) {
        return dayFocusDao.queryLastDayFocus(n);
    }


    public StockInfoDao getStockInfoDao() {
        return stockInfoDao;
    }

    public TaskDao getTaskDao() {
        return taskDao;
    }

    public StockDao getStockDao() {
        return stockDao;
    }

    public MiscDao getMiscDao() {
        return miscDao;
    }

    public TypeAhead getTypeAhead() {
        return typeAhead;
    }

    public TrainDao getTrainDao() {
        return trainDao;
    }

    public void setTrainDao(TrainDao trainDao) {
        this.trainDao = trainDao;
    }

    public DayFocusDao getDayFocusDao() {
        return dayFocusDao;
    }

    public ScoreDao getScoreDao() {
        return scoreDao;
    }

    public void setScoreDao(ScoreDao scoreDao) {
        this.scoreDao = scoreDao;
    }

    public ConceptScoreDao getConceptScoreDao() {
        return conceptScoreDao;
    }

    public void setConceptScoreDao(ConceptScoreDao conceptScoreDao) {
        this.conceptScoreDao = conceptScoreDao;
    }
}
