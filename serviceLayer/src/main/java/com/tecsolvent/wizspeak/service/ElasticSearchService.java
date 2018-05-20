package com.tecsolvent.wizspeak.service;
import com.tecsolvent.wizspeak.ElasticSearchDao;
import com.tecsolvent.wizspeak.cache.GuavaAndCouchbaseCache;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class ElasticSearchService {

	public static Logger logger = Logger.getLogger(ElasticSearchService.class);
	private GuavaAndCouchbaseCache guavaAndCouchbaseCache;
	private ElasticSearchDao elasticSearchDao;

	public void setGuavaAndCouchbaseCache(GuavaAndCouchbaseCache guavaAndCouchbaseCache) {
		this.guavaAndCouchbaseCache = guavaAndCouchbaseCache;
	}


	public Map<String,Object> getResult(String type, String id) throws Exception{


		logger.info("in Service layer");

		Map<String, Object> result = new HashMap<>();
		try{
			result = elasticSearchDao.esTest(type,id);

		}catch (Exception e){

			logger.error("error in dao "+e);
		}

		return result;
	}

	public ElasticSearchDao getElasticSearchDao() {
		return elasticSearchDao;
	}

	public void setElasticSearchDao(ElasticSearchDao elasticSearchDao) {
		this.elasticSearchDao = elasticSearchDao;
	}
}
