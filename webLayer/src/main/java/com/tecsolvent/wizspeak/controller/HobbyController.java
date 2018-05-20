package com.tecsolvent.wizspeak.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.tecsolvent.wizspeak.model.Category;
import com.tecsolvent.wizspeak.model.SubCategory;
import com.tecsolvent.wizspeak.service.HobbyService;
import com.tecsolvent.wizspeak.utility.JsonConvert;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jaison on 16/3/16.
 */
@Controller
public class HobbyController  {

	public static Logger logger = Logger.getLogger(HobbyController.class);
	@Resource(name = "hobbyService")
	HobbyService hobbyService;


	@RequestMapping(value="/getHobbyCategories", method = RequestMethod.GET)
	public
	@ResponseBody
	String getHobbyCategories() throws Exception {
		ArrayList<Category> categories  = new ArrayList<>();
		try {
			categories = hobbyService.getCategories();
		}catch (Exception e){

			logger.info("error in hobbies service "+e);
		}

		logger.info("inside ambition Contoller");
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(categories);


		return json;
	}


		@RequestMapping(value="/getHobbySubCategories", method = RequestMethod.GET)
		public
		@ResponseBody
		String getAmbitionSubCategories() throws Exception
		{

			ArrayList<SubCategory> subCategories  = new ArrayList<>();
			subCategories = hobbyService.getSubCategories();

			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String json = ow.writeValueAsString(subCategories);

			return json;
		}


		@RequestMapping(value = "/myHobby", method = RequestMethod.GET)
		public
		@ResponseBody
		String myHobby() throws Exception{

			return "success";
		}




		@RequestMapping(value = "/getPageJson/{userId}", method = RequestMethod.GET)
		public
		@ResponseBody
		String getPageJson(@PathVariable long userId)throws Exception{

		Map<String,Object> json = new HashMap<>();

		try{
			json = 	hobbyService.getHobbyJson(userId);

		}catch (Exception e){

			logger.info("error in getting hobbies json "+e);
		}
			return  JsonConvert.ObjJson(json);
		}

}
