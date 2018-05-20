package com.tecsolvent.wizspeak.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.tecsolvent.wizspeak.model.FriendRequest;
import com.tecsolvent.wizspeak.model.Post;
import com.tecsolvent.wizspeak.model.User;
import com.tecsolvent.wizspeak.service.ProfileService;
import com.tecsolvent.wizspeak.utility.JsonConvert;
import org.apache.log4j.Logger;
import org.codehaus.jettison.json.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import scala.util.parsing.json.JSONObject;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jaison on 12/4/16.
 */

@Controller
public class ProfileControlller {


	public static Logger logger = Logger.getLogger(ProfileControlller.class);

	@Resource(name = "profileService")
	ProfileService profileService;


	@RequestMapping(value = "/getProfilePost/{userIdStr}/{visitorIdStr}/{page}/{lastId}", method = RequestMethod.GET)
	public
	@ResponseBody
	String getAmbitionPost(@PathVariable String userIdStr,@PathVariable String visitorIdStr,@PathVariable int page ,@PathVariable long lastId) throws Exception {


		logger.info("getting pagination values");
		ArrayList<Post> posts = new ArrayList<>();
		posts =  profileService.getProfilePosts(userIdStr,visitorIdStr,page,lastId);

		return JsonConvert.ObjJson(posts);
	}



	@RequestMapping(value ="/userProfileBaseData/{userId}", method = RequestMethod.GET)
	public
	@ResponseBody
	String getProfileBasic(@PathVariable String userId) throws Exception {

		logger.info("in get bafse date profile");

		Map<String,Object> json = profileService.getProfileJson(userId);
		return JsonConvert.ObjJson(json);

	}


	@RequestMapping(value = "/getUserDetails/{userIdStr}", method = RequestMethod.GET)
	public
	@ResponseBody
	String userDetails(@PathVariable String userIdStr) throws Exception {


		User user = profileService.userDetails(userIdStr);


		return JsonConvert.ObjJson(user);
	}


	@RequestMapping(value ="/userProfileRole/{pageUserId}/{loginUserId}", method = RequestMethod.GET)
	public
	@ResponseBody
	String userProfileRole(@PathVariable String pageUserId,@PathVariable String loginUserId) throws Exception {

		int json = profileService.getUserProfileRole(pageUserId,loginUserId);
		return JsonConvert.ObjJson(json);

	}



	@RequestMapping(value = "/getFriendStatus/{user_id_a}/{user_id_b}", method = RequestMethod.GET)
	public
	@ResponseBody
	String getFriendStatus(@PathVariable String user_id_a,@PathVariable String user_id_b) throws Exception

	{
		logger.info((user_id_a + user_id_b));


		ArrayList<FriendRequest> data = new ArrayList<FriendRequest>();

		try {
			logger.info((user_id_a + "hhhhhhhhhhhhhhh"+     user_id_b));
			data = profileService.getFriendStatus(user_id_a, user_id_b);

		}
		catch (Exception e){logger.info(user_id_a+ "test controller"+ e);}


		ObjectWriter checkfrndreq = new ObjectMapper().writer()
				.withDefaultPrettyPrinter();
		String jaward = checkfrndreq.writeValueAsString(data);
		String newout = jaward.replace("NULL", "empty");

		return newout;



	}



	@RequestMapping(value = "/updateUserDetails", method = RequestMethod.POST)
	public
	@ResponseBody
	String updateUserDetails(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		User user = new User();

		try {

			user.setUserId(request.getParameter("user_id"));
			user.setFirst_name(request.getParameter("first_name"));
			user.setLast_name(request.getParameter("last_name"));
			user.setEmail(request.getParameter("email"));
			user.setDob(request.getParameter("dob"));
			user.setCountry(Integer.parseInt(request.getParameter("city")));
		}catch (Exception e){
			logger.error("error in getting user details for update "+e);
		}

		Boolean status= false;
		try{
			status = profileService.updateUserDet(user);

		}catch (Exception e){

			logger.info("erro in calling update user details "+e);
		}


		return JsonConvert.ObjJson(status);
	}

	@RequestMapping(value = "/getUsersMentor/{userIdStr}", method = RequestMethod.GET)
	public
	@ResponseBody
	String getUserMentors(@PathVariable String userIdStr) throws Exception {
		ArrayList<User> mentors = new ArrayList<>();

		try {
			mentors = profileService.getUserMentor(userIdStr);
		}catch (Exception e){

			logger.info("error in getting mentor list "+e);
		}


		return JsonConvert.ObjJson(mentors);
	}

	@RequestMapping(value = "/userMedia/{userStr}/{mediaType}/{page}/{lastId}" , method = RequestMethod.GET)
	public
	@ResponseBody
	String userMedia(@PathVariable String userStr,@PathVariable byte mediaType,@PathVariable byte page,@PathVariable long lastId)throws Exception {

		ArrayList<Post> posts = new ArrayList<>();
		try {
			logger.info("urrl mapped");
			posts =  profileService.getUserMedia(userStr, mediaType, page,lastId);
		}catch (Exception e){

			logger.info(" error in getting media data "+ e);
		}

		return JsonConvert.ObjJson(posts);
	}




	@RequestMapping(value = "/userUserStatus/{userId}/{visitorId}", method = RequestMethod.GET)
	public
	@ResponseBody
	String userUserStatus(@PathVariable String userId,@PathVariable String visitorId) throws Exception

	{
		HashMap<String,Object> status = new HashMap<>();
		try {

			status = profileService.userUserStatus(userId, visitorId);
		}catch (Exception e){

			logger.info("error in getting user user status "+e);
		}

		return JsonConvert.ObjJson(status);

	}


	@RequestMapping(value = "/amOnline/{userId}", method = RequestMethod.GET)
	public
	@ResponseBody
	String amOnline(@PathVariable String userId) throws Exception

	{
		boolean status = profileService.amOnline(userId);
		return JsonConvert.ObjJson(status);
	}

	@RequestMapping(value = "/addUserRating/{profileId}/{userId}/{rate}", method = RequestMethod.GET)
	public
	@ResponseBody
	String addUserRating(@PathVariable String profileId,@PathVariable String userId,@PathVariable byte rate) throws Exception

	{
		boolean status = profileService.addUserRating(profileId,userId,rate);
		return JsonConvert.ObjJson(status);
	}



	@RequestMapping(value = "/getUserUserRating/{profileId}/{userId}", method = RequestMethod.GET)
	public
	@ResponseBody
	String getUserUserRating(@PathVariable String profileId,@PathVariable String userId) throws Exception

	{
		byte status = profileService.getUserUserRating(profileId,userId);
		return JsonConvert.ObjJson(status);
	}

	@RequestMapping(value = "/getUserRating/{profileId}", method = RequestMethod.GET)
	public
	@ResponseBody
	String getUserRating(@PathVariable String profileId) throws Exception

	{
		byte status = profileService.getUserRating(profileId);
		return JsonConvert.ObjJson(status);
	}



	@RequestMapping(value = "/getUserCategories/{vertical}/{userStr}/{isMentor}", method = RequestMethod.GET)
	public
	@ResponseBody
	String getUserCategories(@PathVariable byte vertical,@PathVariable String userStr,@PathVariable byte isMentor) throws Exception

	{
		HashMap<Integer,String> cate = new HashMap<>();
		try{

			cate = profileService.getUserCateMap(vertical,userStr,isMentor);
		}catch (Exception e){

			logger.info("error in getting user cate "+e);

		}

		return JsonConvert.ObjJson(cate);
	}

	@RequestMapping(value = "/addUserCatRel/{category}/{vertical}/{userStr}/{userType}", method = RequestMethod.GET)
	public
	@ResponseBody
	Boolean addUserCatRel(@PathVariable int category, @PathVariable byte vertical, @PathVariable String userStr,@PathVariable byte userType) throws Exception{

		boolean status = false;
		try{
			status = profileService.addUserCatRel(category,vertical,userStr,userType);
		}catch (Exception e){
			logger.info(" error in adding / updating user category "+e);
		}
		return status;
	}



	@RequestMapping(value = "/removeAllUserCatMap/{vertical}/{userStr}/{userType}", method = RequestMethod.GET)
	public
	@ResponseBody
	Boolean addUserCatRel(@PathVariable byte vertical, @PathVariable String userStr,@PathVariable byte userType) throws Exception{

		boolean status = false;
		try{
			status = profileService.removeAllUserCatMap(vertical,userStr,userType);
		}catch (Exception e){
			logger.info(" error in adding / updating user category "+e);
		}
		return status;
	}


	@RequestMapping(value = "/addCoverPic/{link}/{userStr}", method = RequestMethod.GET)
	public
	@ResponseBody
	String addUserProfilePic(@PathVariable String link, @PathVariable String userStr) throws Exception {

		logger.info("add profile pic controller");

		boolean json = profileService.addCoverPic(link, userStr);
		return JsonConvert.ObjJson(json);

	}

	@RequestMapping(value = "/addGrpCoverPic/{link}/{userStr}", method = RequestMethod.GET)
	public
	@ResponseBody
	String addGrpCoverPic(@PathVariable String link, @PathVariable String userStr) throws Exception {

		logger.info("add profile pic controller");

		boolean json = profileService.add_grp_coverpic(link, userStr);
		return JsonConvert.ObjJson(json);

	}


	@RequestMapping(value = "/getUserCountry/{countryId}", method = RequestMethod.GET)
	public
	@ResponseBody
	String getMentorFollowerId(@PathVariable long countryId) throws Exception {

		ArrayList<String> user = new ArrayList<String>();
		user = profileService.countryId(countryId);

		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(user);


		return json;

	}


	@RequestMapping(value="/userLive",method = RequestMethod.POST)
	public
	@ResponseBody
	String userLive(HttpServletRequest request, HttpServletResponse response)throws Exception{
		Map<String,Boolean> live = new HashMap<>();

		try {
			String myjson = request.getParameter("userId");
			String result = myjson.replaceAll("[^\\dA-Za-z , ]", "");

			String[] array = result.split("\\,", -1);
			live = profileService.checkLive(array);
			return JsonConvert.ObjJson(live);
		}catch (Exception e){

			logger.error("errror in "+e.getMessage());
		}
		return JsonConvert.ObjJson(live);
	}
}
