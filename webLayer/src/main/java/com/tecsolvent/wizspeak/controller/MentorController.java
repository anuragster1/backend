package com.tecsolvent.wizspeak.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.tecsolvent.wizspeak.model.Post;
import com.tecsolvent.wizspeak.model.User;
import com.tecsolvent.wizspeak.service.MentorService;
import com.tecsolvent.wizspeak.service.ProfileService;
import com.tecsolvent.wizspeak.utility.JsonConvert;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import scala.util.parsing.combinator.testing.Str;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by jaison on 15/4/16.
 */


	@Controller
	public class MentorController {


		public static Logger logger = Logger.getLogger(MentorController.class);

		@Resource(name = "mentorService")
		MentorService mentorService;


	@Resource(name = "profileService")
	ProfileService profileService;


	@RequestMapping(value = "/addUserProfilePic/{link}/{userStr}/{wallType}", method = RequestMethod.GET)
	public
	@ResponseBody
	String addUserProfilePic(@PathVariable String link, @PathVariable String userStr, @PathVariable byte wallType) throws Exception {

		logger.info("add profile pic controller");

		boolean json = profileService.addUserProfilePic(link, userStr, wallType);
		return JsonConvert.ObjJson(json);

	}

	@RequestMapping(value ="/mentorBaseData/{userIdStr}/{visitorStr}", method = RequestMethod.GET)
	public
	@ResponseBody
	String mentorBaseData(@PathVariable String userIdStr,@PathVariable String visitorStr) throws Exception {

		logger.info("getting mentor page basic details");

		Map<String,Object> json = mentorService.getMentorPageBasic(userIdStr,visitorStr);
		return JsonConvert.ObjJson(json);

	}


	@RequestMapping(value = "/getMentorFollowers/{user_id}", method = RequestMethod.GET)
	public
	@ResponseBody
	String getMentorFollowers(@PathVariable long user_id) throws Exception
	{

		ArrayList<User> user = new ArrayList<User>();
		logger.info("getting mentor followers ");
		try {
			user = mentorService.getMentorFollowers(user_id);
		}catch (Exception e){

			logger.info("error in calling mentor Service "+e);
		}

		return JsonConvert.ObjJson(user);


	}




	@RequestMapping(value = "/getMentorWallPost/{userId}/{visitor}/{lastPost}/{page}", method = RequestMethod.GET)
	public
	@ResponseBody
	String getMentorFollowers(@PathVariable String userId,@PathVariable String visitor,@PathVariable long lastPost,@PathVariable int page) throws Exception
	{

		ArrayList<Post> posts = new ArrayList<Post>();
		logger.info("getting mentor wall posts ");
		try {
			posts = mentorService.getMentorWallPosts(userId,visitor,lastPost,page);
		}catch (Exception e){

			logger.info("error in calling mentor Service "+e);
		}

		return JsonConvert.ObjJson(posts);


	}

	@RequestMapping(value = "/getMentorMediaPost/{userId}/{visitor}/{mediaType}/{page}", method = RequestMethod.GET)
	public
	@ResponseBody
	String getMentorMediaPost(@PathVariable String userId,@PathVariable String visitor,@PathVariable byte mediaType,@PathVariable int page) throws Exception
	{

		ArrayList<Post> posts = new ArrayList<Post>();
		logger.info("getting mentor wall posts ");
		try {
			posts = mentorService.getMentorMediaPost(userId,visitor,mediaType,page);
		}catch (Exception e){

			logger.info("error in calling mentor Service "+e);
		}

		return JsonConvert.ObjJson(posts);


	}

	@RequestMapping(value = "/getMentorMediaPostCount/{userId}/{mediaType}", method = RequestMethod.GET)
	public
	@ResponseBody
	String getMentorMediaPostCount(@PathVariable String userId,@PathVariable byte mediaType) throws Exception
	{

		int cnt =0 ;
		logger.info("getting mentor media file count  ");
		try {
			cnt = mentorService.getMentorMediaPostCount(userId,mediaType);
		}catch (Exception e){

			logger.info("error in calling mentor Service "+e);
		}

		return JsonConvert.ObjJson(cnt);


	}






	@RequestMapping(value = "/getMentorUserRelation/{mentorIdStr}/{visitorIdStr}", method = RequestMethod.GET)
	public
	@ResponseBody
	byte getMentorUserRelation(@PathVariable String mentorIdStr,@PathVariable String visitorIdStr) throws Exception
	{

		byte relation = 3;
		logger.info("getting mentor wall posts ");
		try {
			relation = mentorService.MentorUserRelation(mentorIdStr,visitorIdStr);
		}catch (Exception e){

			logger.info("error in calling mentor Service "+e);
		}

		return relation;


	}


	@RequestMapping(value = "/getTalk/{mentorIdStr}/{visitorIdStr}/{page}/{lastId}", method = RequestMethod.GET)
	public
	@ResponseBody
	String getTalk(@PathVariable String mentorIdStr,@PathVariable String visitorIdStr,@PathVariable byte page,@PathVariable long lastId) throws Exception
	{
		ArrayList<Post> posts = new ArrayList<>();

		logger.info("getting mentor talk wall posts ");
		try {
			posts = mentorService.mentorTalk(mentorIdStr,visitorIdStr,page,lastId);
		}catch (Exception e){

			logger.info("error in calling mentor Service "+e);
		}

		return JsonConvert.ObjJson(posts);


	}



	@RequestMapping(value = "/getAchieve/{mentorIdStr}/{visitorIdStr}/{page}/{lastId}", method = RequestMethod.GET)
	public
	@ResponseBody
	String getAchieve(@PathVariable String mentorIdStr,@PathVariable String visitorIdStr,@PathVariable byte page,@PathVariable long lastId) throws Exception
	{
		ArrayList<Post> posts = new ArrayList<>();

		logger.info("getting mentor talk wall posts ");
		try {
			posts = mentorService.getAchieve(mentorIdStr,visitorIdStr,page,lastId);
		}catch (Exception e){

			logger.info("error in calling mentor Service "+e);
		}

		return JsonConvert.ObjJson(posts);


	}


	@RequestMapping(value = "/checkMentorFollow/{mentorIdStr}/{userIdStr}", method = RequestMethod.GET)
	public
	@ResponseBody
	String checkMentorFollow(@PathVariable String mentorIdStr,@PathVariable String userIdStr) throws Exception
	{

logger.info("  checkMentorFollow controller");
		byte post = 0;
		try {
			post = mentorService.checkFollow(mentorIdStr,userIdStr);
		}catch (Exception e){

			logger.info("error in calling mentor Service "+e);
		}

		return JsonConvert.ObjJson(post);


	}


	}
