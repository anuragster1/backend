package com.tecsolvent.wizspeak.service;

import com.tecsolvent.wizspeak.GroupDao;
import com.tecsolvent.wizspeak.cache.GuavaAndCouchbaseCache;
import com.tecsolvent.wizspeak.model.*;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by jaison on 16/3/16.
 */
public class HobbyService {


	private CommentService commentService;
	private UserService userService;
	private GroupService groupService;
	private FriendService friendService;
	private MentorService mentorService;
	private PostService postService;

	private GroupDao groupDao;

	private GuavaAndCouchbaseCache guavaAndCouchbaseCache;




	public static Logger logger = Logger.getLogger(AmbitionService.class);

	public void setCommentService(CommentService commentService) {
		this.commentService = commentService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}

	public void setFriendService(FriendService friendService) {
		this.friendService = friendService;
	}

	public void setPostService(PostService postService) {
		this.postService = postService;
	}

	public void setMentorService(MentorService mentorService) {
		this.mentorService = mentorService;
	}

	public void setGuavaAndCouchbaseCache(GuavaAndCouchbaseCache guavaAndCouchbaseCache) {
		this.guavaAndCouchbaseCache = guavaAndCouchbaseCache;
	}


	public void setGroupDao(GroupDao groupDao) {
		this.groupDao = groupDao;
	}

	public Map<String,Object> getHobbyJson(long userId) throws Exception{

		logger.info("getting json obj for hobbies page ");

		Map<String,Object> hobbyJson = (Map<String, Object>) guavaAndCouchbaseCache.getObjectFromCache("hobbyJsonf"+userId,Map.class);


		if(hobbyJson!=null){
			logger.info("ambition page json from cache");
			return hobbyJson;
		}

		Map<String,Object> mapObject = new HashMap<>();

		//user profile
		User user = userService.userDetails(userId);
		mapObject.put("userDetails",user);

		//groupList

//get user groups

		try {

			ArrayList userGroups = groupService.getUserGroups(userId);
			mapObject.put("userGroup",userGroups);

		}catch (Exception e){

			logger.info("error in fetching userGroups "+e);
		}

		//get user friends
		ArrayList<User> friends = new ArrayList<>();
		try {

			friends = friendService.getUserFriends(userId);

		}catch (Exception e){

			logger.info("error in fetching friends "+e);
		}

		mapObject.put("frindList",friends);

		//get mentor list

		ArrayList<User> mentors = new ArrayList<>();

		try {
			mentors = mentorService.getUserMentors(userId);

		}catch (Exception e){

			logger.info("errro in getting mentor list "+e);
		}
		mapObject.put("mentorList",mentors);

		ArrayList<Post> posts = new ArrayList<>();
		ArrayList<Post> newPostArray = new ArrayList<>();


		mapObject.put("userPosts",newPostArray);

		guavaAndCouchbaseCache.putObjectAsByteInCache("hobbyJson"+userId, mapObject);

		logger.info("                      completed fetching json for hobbyJson page ");

		return mapObject;
	}


	public ArrayList<Category> getCategories() throws Exception {


		ArrayList<Category> category = (ArrayList<Category>) guavaAndCouchbaseCache.getObjectFromCache("ambitionCategoriesf",ArrayList.class);

		if(category!=null){

			return category;
		}

		ArrayList<Category> categories = new ArrayList<>();

		categories = groupDao.getCategories(2);
		guavaAndCouchbaseCache.putObjectAsByteInCache("ambitionCategories",categories);

		return categories;
	}


	public ArrayList<SubCategory> getSubCategories() throws Exception{

		logger.info("in service category ");

		ArrayList<SubCategory> category = (ArrayList<SubCategory>) guavaAndCouchbaseCache.getObjectFromCache("hobbySubCategories",ArrayList.class);

		if(category!=null){

			return category;
		}


		ArrayList<SubCategory> subCategories = new ArrayList<>();

		try {

			subCategories = groupDao.getSubCategories();

		}catch (Exception e){

			logger.info(" error in ambition subcategories "+e);
		}

		guavaAndCouchbaseCache.putObjectAsByteInCache("hobbySubCategoriesx",subCategories);

		return subCategories;
	}



}
