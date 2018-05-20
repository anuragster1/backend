package com.tecsolvent.wizspeak;

import com.tecsolvent.wizspeak.elasticsearch.ESContentIndexer;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jaison on 8/6/16.
 */
public class ElasticSearchDao {


	public static Logger logger = Logger.getLogger(ElasticSearchDao.class);


	public Map<String, Object> esTest(String type, String id)throws Exception{

		logger.info("inside data layer");

		Map<String, Object> result = new HashMap<>();

		ESContentIndexer esContentIndexer = ESContentIndexer.getInstance();
	try{
		logger.info(type+" "+id);
			result = esContentIndexer.get(type,id);
		}catch (Exception e){

		logger.info("error in getting elastic connection "+e);
	}

		return result;
	}


}
