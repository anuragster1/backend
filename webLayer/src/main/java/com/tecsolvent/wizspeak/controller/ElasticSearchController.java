package com.tecsolvent.wizspeak.controller;

/**
 * Created by jaison on 8/6/16.
 */

import javax.annotation.Resource;
import com.tecsolvent.wizspeak.service.ElasticSearchService;
import com.tecsolvent.wizspeak.utility.JsonConvert;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


import java.util.HashMap;
import java.util.Map;

@Controller

public class ElasticSearchController {

	public static Logger logger = Logger.getLogger(ElasticSearchController.class);

	@Resource(name = "elasticSearchService")
	ElasticSearchService elasticSearchService;


	@RequestMapping(value = "/elastic/{type}/{id}", method = RequestMethod.GET)
	public
	@ResponseBody
	String elastic(@PathVariable String type,@PathVariable String id) throws Exception {
		logger.info("in controlller ");

		Map<String, Object> result = new HashMap<>();
	try{
		result= elasticSearchService.getResult(type,id);

	}catch(Exception e){
		logger.info("error in getting from "+e);
	}

		return JsonConvert.ObjJson(result);
	}





}
