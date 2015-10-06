package com.victor.midas.dao;

import com.victor.midas.model.vo.concept.StockCrawlData;
import com.victor.midas.util.MidasConstants;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StockCrawlDataDao {
	private final String COLLECTION_NAME = MidasConstants.STOCK_CRAWL_DATA_COLLECTION_NAME;
	
	private static final Logger logger = Logger.getLogger(StockCrawlDataDao.class);
	
	
	@Autowired  
    MongoTemplate mongoTemplate; 

	/**
	 * save stock, cause its name is Id in MongoDB, so it should have name field first
	 */
    public void saveCrawlData(List<StockCrawlData> crawlDatas){
        for (StockCrawlData crawlData : crawlDatas){
            saveCrawlData(crawlData);
        }
    }
	public void saveCrawlData(StockCrawlData crawlData){
		mongoTemplate.save(crawlData, COLLECTION_NAME);
	}
	
	/**
	 * query one stock by its name
	 */
	public StockCrawlData queryByName(String stockName){
		return mongoTemplate.findOne(new Query(Criteria.where("_id").is(stockName)), StockCrawlData.class, COLLECTION_NAME);
    }

    public List<StockCrawlData> queryAllCrawlData(){
        return mongoTemplate.findAll(StockCrawlData.class, COLLECTION_NAME);
    }

    /**
     * create collection
     */
    public void createCollection(){
        if (!mongoTemplate.collectionExists(COLLECTION_NAME)) {
            logger.info("mongoTemplate create collection");
            mongoTemplate.createCollection(COLLECTION_NAME);
        }
    }

    /**
     * delete collection, means that all documents will be deleted
     */
    public void deleteCollection(){
        if (mongoTemplate.collectionExists(COLLECTION_NAME)) {
            logger.info("drop task Collection");
            mongoTemplate.dropCollection(COLLECTION_NAME);
        }
    }
}
